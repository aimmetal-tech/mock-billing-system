package edu.dlut.campuscard.account;

/**
 * 全局台账：累计消费额与累计充值额。
 *
 * <p>重构前这两个数散落在上帝类里由各方法随手累加；此处内聚为独立对象，
 * 与账户树（Composite）一起为报表（Visitor）提供数据。</p>
 */
public class Ledger {

    private long totalConsumed;
    private long totalRecharged;

    /** 记录一笔成功消费（分）。 */
    public void addConsumed(long paid) {
        totalConsumed += paid;
    }

    /** 记录一笔成功充值（分）。 */
    public void addRecharged(long amount) {
        totalRecharged += amount;
    }

    public long totalConsumed() {
        return totalConsumed;
    }

    public long totalRecharged() {
        return totalRecharged;
    }
}
