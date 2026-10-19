package edu.dlut.campuscard.pricing;

import edu.dlut.campuscard.config.AppConfig;

/**
 * 满减装饰器：原始金额达到门槛即减去固定金额。
 *
 * <p>注意：满减以<b>原始金额</b>（{@link PriceRequest#amount()}）判断门槛，
 * 与重构前一致。</p>
 */
public final class FullReductionDecorator extends PriceRuleDecorator {

    private final AppConfig config;

    public FullReductionDecorator(PriceComponent inner, AppConfig config) {
        super(inner);
        this.config = config;
    }

    @Override
    public long price(PriceRequest request) {
        long paid = inner.price(request);
        if (request.amount() >= config.fullReductionThreshold()) {
            paid -= config.fullReductionAmount();
        }
        return paid;
    }
}
