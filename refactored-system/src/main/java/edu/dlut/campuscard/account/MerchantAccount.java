package edu.dlut.campuscard.account;

/**
 * 商户账户（叶子）：按商户累计其收款额与笔数。
 *
 * <p>重构前没有商户账户这一数据结构，报表只能反查流水硬算；
 * 重构后每笔成功消费直接记入对应商户账户。</p>
 */
public class MerchantAccount implements AccountComponent {

    private final String merchantId;
    private final String merchantType;

    private long totalPaid;
    private int txnCount;

    public MerchantAccount(String merchantId, String merchantType) {
        this.merchantId = merchantId;
        this.merchantType = merchantType;
    }

    /** 记入一笔成功消费。 */
    public void record(long paid) {
        totalPaid += paid;
        txnCount++;
    }

    public String merchantId() {
        return merchantId;
    }

    public String merchantType() {
        return merchantType;
    }

    /** 该商户累计收款（分）。 */
    public long totalPaid() {
        return totalPaid;
    }

    /** 该商户成功交易笔数。 */
    public int txnCount() {
        return txnCount;
    }

    @Override
    public void accept(AccountVisitor visitor) {
        visitor.visit(this);
    }
}
