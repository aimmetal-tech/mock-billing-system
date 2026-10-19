package edu.dlut.campuscard.pricing;

/**
 * 基础价组件：把商户计费策略适配为一个 {@link PriceComponent}，
 * 作为优惠装饰器链的最内层。
 */
public final class MerchantPricingComponent implements PriceComponent {

    private final PricingStrategy strategy;

    public MerchantPricingComponent(PricingStrategy strategy) {
        this.strategy = strategy;
    }

    @Override
    public long price(PriceRequest request) {
        return strategy.apply(request.amount());
    }
}
