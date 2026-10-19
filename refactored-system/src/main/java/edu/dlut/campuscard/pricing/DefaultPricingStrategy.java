package edu.dlut.campuscard.pricing;

/** 默认计费：未知商户类型按原价（与重构前 {@code switch} 的 default 分支等价）。 */
public final class DefaultPricingStrategy implements PricingStrategy {

    @Override
    public long apply(long amount) {
        return amount;
    }
}
