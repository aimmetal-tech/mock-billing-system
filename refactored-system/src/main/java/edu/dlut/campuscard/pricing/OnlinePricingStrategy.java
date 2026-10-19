package edu.dlut.campuscard.pricing;

import edu.dlut.campuscard.model.Money;

/** 线上计费：加收 2%。 */
public final class OnlinePricingStrategy implements PricingStrategy {

    @Override
    public long apply(long amount) {
        return Money.percentOf(amount, 102);
    }
}
