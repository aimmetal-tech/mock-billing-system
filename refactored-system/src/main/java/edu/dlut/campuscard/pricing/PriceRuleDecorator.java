package edu.dlut.campuscard.pricing;

/**
 * 优惠规则装饰器基类（Decorator 模式）。
 *
 * <p>持有被装饰的“内层”组件，子类先取内层价格、再叠加自己的规则。</p>
 */
public abstract class PriceRuleDecorator implements PriceComponent {

    /** 被装饰的内层组件。 */
    protected final PriceComponent inner;

    protected PriceRuleDecorator(PriceComponent inner) {
        this.inner = inner;
    }
}
