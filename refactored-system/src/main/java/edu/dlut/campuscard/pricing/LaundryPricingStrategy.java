package edu.dlut.campuscard.pricing;

import edu.dlut.campuscard.model.Money;

/**
 * 洗衣房计费：加收 10%。
 *
 * <p><b>缺陷修复点</b>：重构前该分支写成 {@code case "laundry "}（多了一个空格）导致永远匹配不到，
 * 洗衣房实际按原价收费。重构后用类型路由消除了这类“字符串拼写导致分支不可达”的隐患。</p>
 */
public final class LaundryPricingStrategy implements PricingStrategy {

    @Override
    public long apply(long amount) {
        return Money.percentOf(amount, 110);
    }
}
