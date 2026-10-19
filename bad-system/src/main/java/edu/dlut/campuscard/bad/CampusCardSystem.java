package edu.dlut.campuscard.bad;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 坏味道集中营（重构前的基线）。
 *
 * <p>本类刻意写成“上帝类”（God Class），把一卡通系统里几乎所有职责塞在一起：
 * 账户、消费、计费、优惠、校验、挂失、通知、支付渠道、对账报表……
 * 每个坏味道都对应重构版中的一个设计模式修复点。</p>
 *
 * <ul>
 *   <li>上帝类                → 分层 + Facade</li>
 *   <li>巨型 switch 计费      → Strategy + 简单工厂</li>
 *   <li>优惠 if 嵌套          → Decorator</li>
 *   <li>校验逻辑散落          → Chain of Responsibility</li>
 *   <li>状态用 int + if       → State</li>
 *   <li>通知硬编码            → Observer</li>
 *   <li>异构账户遍历          → Composite + Visitor</li>
 *   <li>全局静态配置          → Singleton + Builder</li>
 *   <li>第三方渠道直接 new    → Adapter + Abstract Factory</li>
 *   <li>消费流程硬编码        → Template Method</li>
 *   <li>★ 埋了一个缺陷        → 见 consume() 中 laundry/print 分支恒不可达</li>
 * </ul>
 */
public class CampusCardSystem {

    // ===== 状态码：用 int 表示卡状态（坏味道：State）=====
    public static final int STATUS_NORMAL = 1;
    public static final int STATUS_LOST = 2;
    public static final int STATUS_FROZEN = 3;
    public static final int STATUS_CLOSED = 4;

    // ===== 数据全部裸露在“系统”里 =====
    public final Map<String, Card> cards = new HashMap<>();
    public final Map<String, Merchant> merchants = new HashMap<>();
    public final List<Transaction> transactions = new ArrayList<>();

    // 对账用的散装计数器（坏味道：职责不分）
    public long totalConsumed = 0;
    public long totalRecharged = 0;
    public final Map<String, Integer> countByMerchantType = new HashMap<>();

    private int txnSeq = 0;

    /** 上帝类里嵌套的数据类，粗暴当作结构体使用。 */
    public static class Card {
        public String id;
        public String holder;
        public String type;      // STUDENT / TEACHER / STAFF
        public boolean vip;
        public int status = STATUS_NORMAL;
        public long balance;
        public long todaySpent;
        public String todayDate = "";
    }

    public static class Merchant {
        public String id;
        public String name;
        public String type;      // canteen / supermarket / laundry / print / online
    }

    public static class Transaction {
        public String id;
        public String cardId;
        public String merchantId;
        public long amount;
        public long paid;
        public String channel;
        public String note;
    }

    /** 统一返回结果。 */
    public static class Result {
        public String status;   // OK / REJECT
        public String reason;   // 拒绝原因
        public long paid;
        public long balance;
    }

    private Result ok(long paid, long balance) {
        Result r = new Result();
        r.status = "OK";
        r.reason = "";
        r.paid = paid;
        r.balance = balance;
        return r;
    }

    private Result reject(String reason, long balance) {
        Result r = new Result();
        r.status = "REJECT";
        r.reason = reason;
        r.paid = 0;
        r.balance = balance;
        return r;
    }

    // ------------------------------------------------------------------
    // 开户 / 充值
    // ------------------------------------------------------------------
    public void addCard(String id, String holder, String type, boolean vip, long balance) {
        Card c = new Card();
        c.id = id;
        c.holder = holder;
        c.type = type;
        c.vip = vip;
        c.balance = balance;
        cards.put(id, c);
    }

    public void addMerchant(String id, String name, String type) {
        Merchant m = new Merchant();
        m.id = id;
        m.name = name;
        m.type = type;
        merchants.put(id, m);
    }

    public Result recharge(String cardId, long amount) {
        Card c = cards.get(cardId);
        if (c == null) {
            return reject("卡不存在", 0);
        }
        // 坏味道：状态判断散落，且 CLOSED 之外都能充
        if (c.status == STATUS_CLOSED) {
            return reject("卡已注销", c.balance);
        }
        if (amount <= 0) {
            return reject("金额非法", c.balance);
        }
        c.balance += amount;
        totalRecharged += amount;
        return ok(0, c.balance);
    }

    // ------------------------------------------------------------------
    // 消费：把 校验 + 计费 + 优惠 + 扣款 + 记账 + 通知 + 支付 全塞进一个方法
    // ------------------------------------------------------------------
    public Result consume(String cardId, String merchantId, long amount, int hour) {
        Card c = cards.get(cardId);
        Merchant m = merchants.get(merchantId);

        // 坏味道：校验逻辑内联、层层 if
        if (c == null) {
            return reject("卡不存在", 0);
        }
        if (m == null) {
            return reject("商户不存在", c.balance);
        }
        if (c.status == STATUS_LOST) {
            return reject("卡已挂失", c.balance);
        }
        if (c.status == STATUS_FROZEN) {
            return reject("卡已冻结", c.balance);
        }
        if (c.status == STATUS_CLOSED) {
            return reject("卡已注销", c.balance);
        }
        if (amount <= 0) {
            return reject("金额非法", c.balance);
        }

        // ===== 计费：巨型 switch（坏味道：Strategy）=====
        long paid = amount;
        switch (m.type) {
            case "canteen":
                break;
            case "supermarket":
                break;
            case "online":
                paid = pct(paid, 102);          // 线上 +2%
                break;
            case "laundry ":
                paid = pct(paid, 110);          // ★ 缺陷：多了个空格，永远匹配不到
                break;
            case "PRINT":
                paid = pct(paid, 105);          // ★ 缺陷：大小写不符，永远匹配不到
                break;
            default:
                break;
        }

        // ===== 优惠：if 层层嵌套（坏味道：Decorator）=====
        if (hour >= 13 && hour < 14) {
            if (AppConfig.TIME_DISCOUNT_PERCENT > 0) {
                paid = paid - pct(paid, AppConfig.TIME_DISCOUNT_PERCENT);
            }
        }
        if (c.vip) {
            if (AppConfig.VIP_DISCOUNT_PERCENT > 0) {
                paid = paid - pct(paid, AppConfig.VIP_DISCOUNT_PERCENT);
            }
        }
        if (amount >= AppConfig.FULL_REDUCTION_THRESHOLD) {
            paid = paid - AppConfig.FULL_REDUCTION_AMOUNT;
        }
        if (paid < 0) {
            paid = 0;
        }

        // 余额校验
        if (paid > c.balance) {
            return reject("余额不足", c.balance);
        }

        // ===== 单日限额：日期字符串比较（坏味道：校验散落）=====
        String today = hour + "-day";     // 简化：占位日期
        if (!today.equals(c.todayDate)) {
            c.todayDate = today;
            c.todaySpent = 0;
        }
        if (c.todaySpent + paid > AppConfig.DAILY_LIMIT) {
            return reject("超出单日限额", c.balance);
        }

        // ===== 扣款 =====
        c.balance -= paid;
        c.todaySpent += paid;

        // ===== 支付渠道：直接 new（坏味道：Adapter + AbstractFactory）=====
        String channel;
        if (c.type.equals("STUDENT")) {
            channel = new AlipayThing().pay(cardId, amount);
        } else if (c.type.equals("TEACHER")) {
            channel = new WechatThing().pay(cardId, amount);
        } else {
            channel = new AlipayThing().pay(cardId, amount);
        }

        // ===== 记账 =====
        Transaction t = new Transaction();
        t.id = "T" + (++txnSeq);
        t.cardId = cardId;
        t.merchantId = merchantId;
        t.amount = amount;
        t.paid = paid;
        t.channel = channel;
        t.note = "消费";
        transactions.add(t);

        totalConsumed += paid;
        countByMerchantType.merge(m.type, 1, Integer::sum);

        // ===== 通知：硬编码三个渠道（坏味道：Observer）=====
        new SmsThing().send(c.holder, "消费 " + c.id + " 金额 " + paid);
        new PushThing().send(c.holder, "消费 " + c.id + " 金额 " + paid);
        new MailThing().send(c.holder, "消费 " + c.id + " 金额 " + paid);

        return ok(paid, c.balance);
    }

    // ------------------------------------------------------------------
    // 挂失 / 解挂
    // ------------------------------------------------------------------
    public Result reportLoss(String cardId) {
        Card c = cards.get(cardId);
        if (c == null) {
            return reject("卡不存在", 0);
        }
        if (c.status != STATUS_NORMAL) {
            return reject("当前状态不可挂失", c.balance);
        }
        c.status = STATUS_LOST;
        new SmsThing().send(c.holder, "卡 " + c.id + " 已挂失");
        new PushThing().send(c.holder, "卡 " + c.id + " 已挂失");
        new MailThing().send(c.holder, "卡 " + c.id + " 已挂失");
        return ok(0, c.balance);
    }

    public Result recover(String cardId) {
        Card c = cards.get(cardId);
        if (c == null) {
            return reject("卡不存在", 0);
        }
        if (c.status != STATUS_LOST) {
            return reject("当前状态不可解挂", c.balance);
        }
        c.status = STATUS_NORMAL;
        return ok(0, c.balance);
    }

    public String statusName(int s) {
        switch (s) {
            case STATUS_NORMAL: return "NORMAL";
            case STATUS_LOST: return "LOST";
            case STATUS_FROZEN: return "FROZEN";
            case STATUS_CLOSED: return "CLOSED";
            default: return "UNKNOWN";
        }
    }

    /** 对账报表：坏味道——对异构账户分别循环累加（Composite 反面）。 */
    public String report() {
        long personalSum = 0;
        for (Card c : cards.values()) {
            personalSum += c.balance;
        }
        long merchantSum = 0;
        // 坏味道：商户账户没有独立数据结构，只能再遍历一遍流水硬算
        Map<String, Long> byMerchant = new HashMap<>();
        for (Transaction t : transactions) {
            byMerchant.merge(t.merchantId, t.paid, Long::sum);
        }
        for (Long v : byMerchant.values()) {
            merchantSum += v;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("consumed=").append(totalConsumed);
        sb.append(" recharged=").append(totalRecharged);
        sb.append(" personalBalance=").append(personalSum);
        sb.append(" merchantBalance=").append(merchantSum);
        // 各商户类型笔数
        List<String> keys = new ArrayList<>(countByMerchantType.keySet());
        keys.sort(String::compareTo);
        for (String k : keys) {
            sb.append(" count.").append(k).append("=").append(countByMerchantType.get(k));
        }
        return sb.toString();
    }

    // ===== 工具：四舍五入的百分比计算 =====
    private long pct(long amount, int percent) {
        return (amount * percent + 50) / 100;
    }

    /** 坏味道：第三方支付被直接 new，且接口不统一（Adapter 反面）。 */
    static class AlipayThing {
        String pay(String cardId, long amount) {
            return "ALIPAY";
        }
    }

    static class WechatThing {
        String pay(String cardId, long amount) {
            return "WECHAT";
        }
    }

    static class SmsThing {
        void send(String to, String msg) { /* 模拟发送短信 */ }
    }

    static class PushThing {
        void send(String to, String msg) { /* 模拟 App 推送 */ }
    }

    static class MailThing {
        void send(String to, String msg) { /* 模拟邮件 */ }
    }
}
