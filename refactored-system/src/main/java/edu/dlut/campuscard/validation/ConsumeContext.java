package edu.dlut.campuscard.validation;

import edu.dlut.campuscard.model.Card;
import edu.dlut.campuscard.model.Merchant;

/**
 * 消费校验上下文：在责任链各环节之间传递的“可变上下文”。
 *
 * <p>随校验进度逐步填充已解析的实体与应付金额，供后续校验环节读取。</p>
 */
public class ConsumeContext {

    private final String cardId;
    private final String merchantId;
    private final long amount;
    private final int hour;

    private Card card;
    private Merchant merchant;
    private long paid;

    public ConsumeContext(String cardId, String merchantId, long amount, int hour) {
        this.cardId = cardId;
        this.merchantId = merchantId;
        this.amount = amount;
        this.hour = hour;
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

    public int hour() {
        return hour;
    }

    public Card card() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public Merchant merchant() {
        return merchant;
    }

    public void setMerchant(Merchant merchant) {
        this.merchant = merchant;
    }

    public long paid() {
        return paid;
    }

    public void setPaid(long paid) {
        this.paid = paid;
    }
}
