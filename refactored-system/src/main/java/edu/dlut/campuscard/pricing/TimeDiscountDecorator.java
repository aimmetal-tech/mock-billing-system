package edu.dlut.campuscard.pricing;

import edu.dlut.campuscard.config.AppConfig;
import edu.dlut.campuscard.model.Money;

/**
 * 时段折扣装饰器：13:00-14:00 内按配置百分比降价。
 */
public final class TimeDiscountDecorator extends PriceRuleDecorator {

    private final AppConfig config;

    public TimeDiscountDecorator(PriceComponent inner, AppConfig config) {
        super(inner);
        this.config = config;
    }

    @Override
    public long price(PriceRequest request) {
        long paid = inner.price(request);
        if (request.hour() >= 13 && request.hour() < 14 && config.timeDiscountPercent() > 0) {
            paid -= Money.percentOf(paid, config.timeDiscountPercent());
        }
        return paid;
    }
}
