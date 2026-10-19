package edu.dlut.campuscard.model;

/**
 * 交易流水（不可变）。
 *
 * <p>重构前流水的各字段散落在上帝类里，此处内聚为一个值对象。</p>
 */
public final class Transaction {

    private final String id;
    private final String cardId;
    private final String merchantId;
    private final long amount;
    private final long paid;
    private final String channel;
    private final String note;

    public Transaction(String id, String cardId, String merchantId,
                       long amount, long paid, String channel, String note) {
        this.id = id;
        this.cardId = cardId;
        this.merchantId = merchantId;
        this.amount = amount;
        this.paid = paid;
        this.channel = channel;
        this.note = note;
    }

    public String id() {
        return id;
    }

    public String cardId() {
        return cardId;
    }

    public String merchantId() {
        return merchantId;
    }

    public long amount() {
        return amount;
    }

    public long paid() {
        return paid;
    }

    public String channel() {
        return channel;
    }

    public String note() {
        return note;
    }
}
