package edu.dlut.campuscard.bad;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 场景脚本执行器（重构前版本）。
 *
 * <p>读取 scenarios/*.json，对坏系统执行一串操作，输出<b>归一化文本</b>。
 * 重构版拥有同名执行器、输出完全相同的格式，二者逐行对比即可证明“行为等价”。</p>
 *
 * <p>用法：{@code java ...BadScenarioRunner <scenario.json> [out.txt]}</p>
 */
public class BadScenarioRunner {

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.err.println("用法: BadScenarioRunner <scenario.json> [out.txt]");
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
        CampusCardSystem sys = new CampusCardSystem();

        for (Object o : (List<Object>) sc.getOrDefault("cards", new ArrayList<>())) {
            Map<String, Object> c = (Map<String, Object>) o;
            sys.addCard(str(c, "id"), str(c, "holder"), str(c, "type"),
                    bool(c, "vip"), num(c, "balance"));
        }
        for (Object o : (List<Object>) sc.getOrDefault("merchants", new ArrayList<>())) {
            Map<String, Object> m = (Map<String, Object>) o;
            sys.addMerchant(str(m, "id"), str(m, "name"), str(m, "type"));
        }

        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (Object o : (List<Object>) sc.getOrDefault("ops", new ArrayList<>())) {
            Map<String, Object> op = (Map<String, Object>) o;
            String kind = str(op, "op");
            i++;
            switch (kind) {
                case "recharge": {
                    CampusCardSystem.Result r = sys.recharge(str(op, "card"), num(op, "amount"));
                    sb.append("recharge ").append(str(op, "card")).append(' ').append(num(op, "amount"))
                            .append(" | ").append(fmt(r)).append('\n');
                    break;
                }
                case "consume": {
                    CampusCardSystem.Result r = sys.consume(str(op, "card"), str(op, "merchant"),
                            num(op, "amount"), (int) num(op, "hour"));
                    sb.append("consume ").append(str(op, "card")).append(' ').append(str(op, "merchant"))
                            .append(' ').append(num(op, "amount")).append(' ').append(num(op, "hour"))
                            .append(" | ").append(fmt(r)).append('\n');
                    break;
                }
                case "loss": {
                    CampusCardSystem.Result r = sys.reportLoss(str(op, "card"));
                    sb.append("loss ").append(str(op, "card")).append(" | ").append(fmt(r)).append('\n');
                    break;
                }
                case "recover": {
                    CampusCardSystem.Result r = sys.recover(str(op, "card"));
                    sb.append("recover ").append(str(op, "card")).append(" | ").append(fmt(r)).append('\n');
                    break;
                }
                case "state": {
                    sb.append(stateLine(sys, str(op, "card"))).append('\n');
                    break;
                }
                case "report": {
                    sb.append("report ").append(sys.report()).append('\n');
                    break;
                }
                default:
                    throw new IllegalArgumentException("未知操作: " + kind);
            }
        }

        // 末尾统一输出所有卡的最终状态与总报表，保证对比时顺序稳定
        List<String> ids = new ArrayList<>(sys.cards.keySet());
        ids.sort(String::compareTo);
        for (String id : ids) {
            sb.append(stateLine(sys, id)).append('\n');
        }
        sb.append("report ").append(sys.report()).append('\n');
        return sb.toString();
    }

    private static String stateLine(CampusCardSystem sys, String cardId) {
        CampusCardSystem.Card c = sys.cards.get(cardId);
        if (c == null) {
            return "state " + cardId + " MISSING balance=0";
        }
        return "state " + cardId + " " + sys.statusName(c.status) + " balance=" + c.balance;
    }

    private static String fmt(CampusCardSystem.Result r) {
        if ("OK".equals(r.status)) {
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
        return ((Number) v).longValue();
    }

    private static boolean bool(Map<String, Object> m, String k) {
        Object v = m.get(k);
        return v instanceof Boolean ? (Boolean) v : "true".equals(String.valueOf(v));
    }
}
