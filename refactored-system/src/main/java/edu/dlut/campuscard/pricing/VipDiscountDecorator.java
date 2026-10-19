package edu.dlut.campuscard.pricing;

import edu.dlut.campuscard.config.AppConfig;
import edu.dlut.campuscard.model.Money;

/**
 * VIP 折扣装饰器：VIP 持卡人按配置百分比降价。
 */
public final class VipDiscountDecorator extends PriceRuleDecorator {

    private final AppConfig config;

    public VipDiscountDecorator(PriceComponent inner, AppConfig config) {
        super(inner);
        this.config = config;
    }

    @Override
    public long price(PriceRequest request) {
        long paid = inner.price(request);
        if (request.vip() && config.vipDiscountPercent() > 0) {
            paid -= Money.percentOf(paid, config.vipDiscountPercent());
        }
        return paid;
    }
}
