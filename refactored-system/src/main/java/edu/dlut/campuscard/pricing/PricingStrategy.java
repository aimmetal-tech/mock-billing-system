package edu.dlut.campuscard.pricing;

/**
 * 商户计费策略（Strategy 模式）。
 *
 * <p>取代重构前的巨型 {@code switch (m.type)}：每种商户类型的计费规则各成一个策略类，
 * 新增商户类型 = 新增策略类 + 工厂注册，符合开闭原则。</p>
 */
public interface PricingStrategy {

    /** 按商户类型计算基础应付金额（分）。 */
    long apply(long amount);
}
