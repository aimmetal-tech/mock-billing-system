package edu.dlut.campuscard.pricing;

/** 超市计费：原价。 */
public final class SupermarketPricingStrategy implements PricingStrategy {

    @Override
    public long apply(long amount) {
        return amount;
    }
}
