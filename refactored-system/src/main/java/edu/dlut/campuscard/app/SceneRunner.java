package edu.dlut.campuscard.app;

import edu.dlut.campuscard.model.Card;
import edu.dlut.campuscard.model.Result;
import edu.dlut.campuscard.service.CardService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 场景脚本执行器（重构后版本）。
 *
 * <p>与重构前 {@code BadScenarioRunner} 读取完全相同的 scenarios/*.json，
 * 输出<b>逐字符相同格式</b>的归一化文本；两套执行器的输出 diff 即为“行为等价”证据。</p>
 *
 * <p>用法：{@code java ...SceneRunner <scenario.json> [out.txt]}</p>
 */
public class SceneRunner {

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.err.println("用法: SceneRunner <scenario.json> [out.txt]");
            System.exit(2);
        }
        String text = Files.readString(Path.of(args[0]), StandardCharsets.UTF_8);
        String out = run(text);
        System.out.print(out);
        if (args.length >= 2) {
            Files.writeString(Path.of(args[1]), out, StandardCharsets.UTF_8);
        }
    }

    /** 执行场景并返回归一化结果文本。 */
    @SuppressWarnings("unchecked")
    public static String run(String scenarioJson) {
        Map<String, Object> sc = MiniJson.parseObject(scenarioJson);
        CardService service = new CardService();

        for (Object o : (List<Object>) sc.getOrDefault("cards", new ArrayList<>())) {
            Map<String, Object> c = (Map<String, Object>) o;
            service.addCard(str(c, "id"), str(c, "holder"), str(c, "type"),
                    bool(c, "vip"), num(c, "balance"));
        }
        for (Object o : (List<Object>) sc.getOrDefault("merchants", new ArrayList<>())) {
            Map<String, Object> m = (Map<String, Object>) o;
            service.addMerchant(str(m, "id"), str(m, "name"), str(m, "type"));
        }

        StringBuilder sb = new StringBuilder();
        for (Object o : (List<Object>) sc.getOrDefault("ops", new ArrayList<>())) {
            Map<String, Object> op = (Map<String, Object>) o;
            String kind = str(op, "op");
            switch (kind) {
                case "recharge": {
                    Result r = service.recharge(str(op, "card"), num(op, "amount"));
                    sb.append("recharge ").append(str(op, "card")).append(' ').append(num(op, "amount"))
                            .append(" | ").append(fmt(r)).append('\n');
                    break;
                }
                case "consume": {
                    Result r = service.consume(str(op, "card"), str(op, "merchant"),
                            num(op, "amount"), (int) num(op, "hour"));
                    sb.append("consume ").append(str(op, "card")).append(' ').append(str(op, "merchant"))
                            .append(' ').append(num(op, "amount")).append(' ').append(num(op, "hour"))
                            .append(" | ").append(fmt(r)).append('\n');
                    break;
                }
                case "loss": {
                    Result r = service.reportLoss(str(op, "card"));
                    sb.append("loss ").append(str(op, "card")).append(" | ").append(fmt(r)).append('\n');
                    break;
                }
                case "recover": {
                    Result r = service.recover(str(op, "card"));
                    sb.append("recover ").append(str(op, "card")).append(" | ").append(fmt(r)).append('\n');
                    break;
                }
                case "state": {
                    sb.append(stateLine(service, str(op, "card"))).append('\n');
                    break;
                }
                case "report": {
                    sb.append("report ").append(service.report()).append('\n');
                    break;
                }
                default:
                    throw new IllegalArgumentException("未知操作: " + kind);
            }
        }

        // 末尾统一输出所有卡的最终状态与总报表，保证对比时顺序稳定
        for (String id : service.cardIds()) {
            sb.append(stateLine(service, id)).append('\n');
        }
        sb.append("report ").append(service.report()).append('\n');
        return sb.toString();
    }

    private static String stateLine(CardService service, String cardId) {
        Card c = service.card(cardId);
        if (c == null) {
            return "state " + cardId + " MISSING balance=0";
        }
        return "state " + cardId + " " + c.statusName() + " balance=" + c.balance();
    }

    private static String fmt(Result r) {
        if (Result.OK.equals(r.status)) {
            return "OK paid=" + r.paid + " balance=" + r.balance;
        }
        return "REJECT " + r.reason + " balance=" + r.balance;
    }

    // ---- 取值辅助 ----
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

    private static boolean bool(Map<String, Object> m, String k) {
        Object v = m.get(k);
        return v instanceof Boolean ? (Boolean) v : "true".equals(String.valueOf(v));
    }
}
