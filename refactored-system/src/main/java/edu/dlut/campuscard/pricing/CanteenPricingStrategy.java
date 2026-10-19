package edu.dlut.campuscard.pricing;

/** 食堂计费：原价。 */
public final class CanteenPricingStrategy implements PricingStrategy {

    @Override
    public long apply(long amount) {
        return amount;
    }
}
