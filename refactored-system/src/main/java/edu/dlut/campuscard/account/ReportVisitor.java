package edu.dlut.campuscard.account;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 对账报表访问者（Visitor 模式）。
 *
 * <p>遍历账户树，汇总个人余额、商户收款与各商户类型笔数，并结合 {@link Ledger}
 * 生成与重构前<b>逐字符一致</b>的报表字符串。</p>
 */
public class ReportVisitor implements AccountVisitor {

    private final Ledger ledger;

    private long personalBalance;
    private long merchantBalance;
    private final Map<String, Integer> countByMerchantType = new TreeMap<>();

    public ReportVisitor(Ledger ledger) {
        this.ledger = ledger;
    }

    @Override
    public void visit(PersonalAccount account) {
        personalBalance += account.balance();
    }

    @Override
    public void visit(MerchantAccount account) {
        merchantBalance += account.totalPaid();
        // 与重构前一致：只统计发生过成功交易的商户类型（未使用的商户不产生 count 项）
        if (account.txnCount() > 0) {
            countByMerchantType.merge(account.merchantType(), account.txnCount(), Integer::sum);
        }
    }

    /**
     * 生成报表字符串。
     *
     * <p>格式与重构前一致：{@code consumed=.. recharged=.. personalBalance=.. merchantBalance=..
     * count.<type>=..}，其中 {@code count.<type>} 按类型名升序拼接。</p>
     */
    public String toReportString() {
        StringBuilder sb = new StringBuilder();
        sb.append("consumed=").append(ledger.totalConsumed());
        sb.append(" recharged=").append(ledger.totalRecharged());
        sb.append(" personalBalance=").append(personalBalance);
        sb.append(" merchantBalance=").append(merchantBalance);
        List<String> keys = new ArrayList<>(countByMerchantType.keySet());
        keys.sort(String::compareTo);
        for (String key : keys) {
            sb.append(" count.").append(key).append('=').append(countByMerchantType.get(key));
        }
        return sb.toString();
    }
}
