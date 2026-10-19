package edu.dlut.campuscard.pricing;

import edu.dlut.campuscard.model.Money;

/**
 * 文印计费：加收 5%。
 *
 * <p><b>缺陷修复点</b>：重构前该分支写成 {@code case "PRINT"}（大写）而数据里是小写 {@code print}，
 * 同样永远匹配不到。重构后按类型路由，分支真实可达。</p>
 */
public final class PrintPricingStrategy implements PricingStrategy {

    @Override
    public long apply(long amount) {
        return Money.percentOf(amount, 105);
    }
}
