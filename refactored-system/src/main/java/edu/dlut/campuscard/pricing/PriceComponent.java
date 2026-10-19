package edu.dlut.campuscard.pricing;

/**
 * 计费组件（Decorator 模式的 Component）。
 *
 * <p>计费被表达为“基础价 → 若干优惠规则依次叠加”的组件链：
 * 每个装饰器读取内层的价格，再施加自己的规则。<br>
 * 取代重构前层层嵌套的 {@code if}，新增优惠只需新增装饰器、自由决定叠加顺序。</p>
 */
public interface PriceComponent {

    /** 计算应付金额（分）。 */
    long price(PriceRequest request);
}
