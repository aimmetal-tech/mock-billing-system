package edu.dlut.campuscard.pricing;

/**
 * 计费策略工厂（简单工厂）。
 *
 * <p>把“商户类型 → 计费策略”的映射集中在一处，业务代码不再关心具体策略类，
 * 调换/新增计费规则只需改这里。</p>
 */
public final class PricingStrategyFactory {

    private static final PricingStrategy CANTEEN = new CanteenPricingStrategy();
    private static final PricingStrategy SUPERMARKET = new SupermarketPricingStrategy();
    private static final PricingStrategy ONLINE = new OnlinePricingStrategy();
    private static final PricingStrategy LAUNDRY = new LaundryPricingStrategy();
    private static final PricingStrategy PRINT = new PrintPricingStrategy();
    private static final PricingStrategy DEFAULT = new DefaultPricingStrategy();

    private PricingStrategyFactory() {
    }

    /** 依据商户类型取得计费策略。 */
    public static PricingStrategy of(String merchantType) {
        if (merchantType == null) {
            return DEFAULT;
        }
        switch (merchantType) {
            case "canteen":
                return CANTEEN;
            case "supermarket":
                return SUPERMARKET;
            case "online":
                return ONLINE;
            case "laundry":
                return LAUNDRY;
            case "print":
                return PRINT;
            default:
                return DEFAULT;
        }
    }
}
