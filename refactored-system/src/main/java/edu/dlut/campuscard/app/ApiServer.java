package edu.dlut.campuscard.app;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import edu.dlut.campuscard.model.Card;
import edu.dlut.campuscard.model.Result;
import edu.dlut.campuscard.service.CardService;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 重构后系统的极简 REST 演示壳（JDK 内置 HttpServer，默认端口 8082）。
 *
 * <p>与重构前 {@code BadApiServer} 暴露<b>同一套 REST 语义</b>，供前端页面并排对比演示：</p>
 * <ul>
 *   <li>POST /api/consume  {card, merchant, amount, hour}</li>
 *   <li>POST /api/recharge {card, amount}</li>
 *   <li>POST /api/loss     {card}</li>
 *   <li>POST /api/recover  {card}</li>
 *   <li>GET  /api/report</li>
 *   <li>GET  /api/state</li>
 * </ul>
 *
 * <p>约束：HTTP 层只做参数解析与 JSON 序列化，全部业务语义都在 {@code service} 及以下包中。</p>
 */
public class ApiServer {

    private static final String DEFAULT_WEB_DIR = "web";

    public static void main(String[] args) throws IOException {
        int port = args.length >= 1 ? Integer.parseInt(args[0]) : 8082;
        Path webIndex = resolveWebIndex(args.length >= 2 ? args[1] : null);

        CardService service = DemoData.build();
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        server.createContext("/api/consume", ex -> handle(ex, () -> {
            Map<String, Object> body = MiniJson.parseObject(readBody(ex));
            return resultJson(service.consume(str(body, "card"), str(body, "merchant"),
                    num(body, "amount"), (int) num(body, "hour")));
        }));
        server.createContext("/api/recharge", ex -> handle(ex, () -> {
            Map<String, Object> body = MiniJson.parseObject(readBody(ex));
            return resultJson(service.recharge(str(body, "card"), num(body, "amount")));
        }));
        server.createContext("/api/loss", ex -> handle(ex, () -> {
            Map<String, Object> body = MiniJson.parseObject(readBody(ex));
            return resultJson(service.reportLoss(str(body, "card")));
        }));
        server.createContext("/api/recover", ex -> handle(ex, () -> {
            Map<String, Object> body = MiniJson.parseObject(readBody(ex));
            return resultJson(service.recover(str(body, "card")));
        }));
        server.createContext("/api/report", ex -> handle(ex, () -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("report", service.report());
            return MiniJson.write(m);
        }));
        server.createContext("/api/state", ex -> handle(ex, () -> stateJson(service)));

        server.createContext("/", ex -> serveStatic(ex, webIndex));

        server.setExecutor(null);
        server.start();
        System.out.println("重构后(REST) 已启动: http://localhost:" + port + "/  (web: " + webIndex + ")");
    }

    // ------------------------------------------------------------------
    // 处理
    // ------------------------------------------------------------------
    private interface Supplier {
        String json() throws IOException;
    }

    private static void handle(HttpExchange ex, Supplier supplier) throws IOException {
        addCors(ex);
        if ("OPTIONS".equalsIgnoreCase(ex.getRequestMethod())) {
            ex.sendResponseHeaders(204, -1);
            ex.close();
            return;
        }
        String json;
        try {
            json = supplier.json();
        } catch (RuntimeException e) {
            json = "{\"error\":\"" + e.getMessage() + "\"}";
        }
        respond(ex, 200, json);
    }

    private static void serveStatic(HttpExchange ex, Path index) throws IOException {
        addCors(ex);
        String path = ex.getRequestURI().getPath();
        if ("/".equals(path) || "/index.html".equals(path)) {
            if (index != null && Files.exists(index)) {
                respond(ex, 200, Files.readString(index, StandardCharsets.UTF_8), "text/html; charset=UTF-8");
                return;
            }
            respond(ex, 404, "web/index.html 未找到");
            return;
        }
        respond(ex, 404, "not found");
    }

    private static String readBody(HttpExchange ex) throws IOException {
        return new String(ex.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
    }

    private static void respond(HttpExchange ex, int code, String body) throws IOException {
        respond(ex, code, body, "application/json; charset=UTF-8");
    }

    private static void respond(HttpExchange ex, int code, String body, String contentType) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().add("Content-Type", contentType);
        ex.sendResponseHeaders(code, bytes.length);
        try (OutputStream os = ex.getResponseBody()) {
            os.write(bytes);
        }
    }

    private static void addCors(HttpExchange ex) {
        ex.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        ex.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
        ex.getResponseHeaders().add("Access-Control-Allow-Methods", "GET,POST,OPTIONS");
    }

    // ------------------------------------------------------------------
    // JSON 视图
    // ------------------------------------------------------------------
    static String resultJson(Result r) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("status", r.status);
        m.put("reason", r.reason);
        m.put("paid", r.paid);
        m.put("balance", r.balance);
        return MiniJson.write(m);
    }

    static String stateJson(CardService service) {
        List<Object> list = new ArrayList<>();
        for (String id : service.cardIds()) {
            Card c = service.card(id);
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", c.id());
            m.put("holder", c.holder());
            m.put("status", c.statusName());
            m.put("balance", c.balance());
            list.add(m);
        }
        Map<String, Object> root = new LinkedHashMap<>();
        root.put("cards", list);
        return MiniJson.write(root);
    }

    // ------------------------------------------------------------------
    // 取值辅助
    // ------------------------------------------------------------------
    private static String str(Map<String, Object> m, String k) {
        Object v = m.get(k);
        return v == null ? "" : String.valueOf(v);
    }

    private static long num(Map<String, Object> m, String k) {
        Object v = m.get(k);
        if (v == null) {
            return 0;
        }
        return v instanceof Number ? ((Number) v).longValue() : Long.parseLong(String.valueOf(v));
    }

    private static Path resolveWebIndex(String explicit) {
        List<Path> candidates = new ArrayList<>();
        if (explicit != null) {
            candidates.add(Path.of(explicit, "index.html"));
            candidates.add(Path.of(explicit));
        }
        candidates.add(Path.of(DEFAULT_WEB_DIR, "index.html"));
        candidates.add(Path.of("..", DEFAULT_WEB_DIR, "index.html"));
        for (Path p : candidates) {
            if (Files.exists(p)) {
                return p;
            }
        }
        return candidates.get(candidates.size() - 1);
    }
}
