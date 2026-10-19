package edu.dlut.campuscard.pricing;

import edu.dlut.campuscard.config.AppConfig;
import edu.dlut.campuscard.model.Merchant;

/**
 * 计费计算器：装配“基础价 + 优惠装饰器链”并做最终兜底。
 *
 * <p>叠加顺序严格复刻重构前语义：<b>商户计费 → 时段折扣 → VIP 折扣 → 满减 → 非负兜底</b>。</p>
 */
public final class PriceCalculator {

    private final AppConfig config;

    public PriceCalculator(AppConfig config) {
        this.config = config;
    }

    /**
     * 计算某商户下一笔消费的应付金额。
     *
     * @param merchant 商户（决定基础计费策略）
     * @param request  计费请求
     * @return 应付金额（分），保证不小于 0
     */
    public long calculate(Merchant merchant, PriceRequest request) {
        PricingStrategy strategy = PricingStrategyFactory.of(merchant.type());
        // 由内向外装配装饰器：内层先算基础价，外层依次叠加优惠
        PriceComponent chain = new MerchantPricingComponent(strategy);
        chain = new TimeDiscountDecorator(chain, config);
        chain = new VipDiscountDecorator(chain, config);
        chain = new FullReductionDecorator(chain, config);
        long paid = chain.price(request);
        return Math.max(0, paid);
    }
}
