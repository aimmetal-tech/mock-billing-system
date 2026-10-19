package edu.dlut.campuscard.config;

/**
 * 全局配置（Singleton 模式，不可变）。
 *
 * <p>重构前 {@code AppConfig} 的字段是 {@code public static} 可任意改写，测试会互相污染；
 * 重构后配置为不可变单例，只能通过 {@link AppConfigBuilder} 在启动时构造一次。</p>
 */
public final class AppConfig {

    private static final class Holder {
        private static final AppConfig INSTANCE = new AppConfigBuilder().build();
    }

    private final int dailyLimit;
    private final int vipDiscountPercent;
    private final int timeDiscountPercent;
    private final int fullReductionThreshold;
    private final int fullReductionAmount;
    private final String defaultPayChannel;

    AppConfig(AppConfigBuilder builder) {
        this.dailyLimit = builder.dailyLimit;
        this.vipDiscountPercent = builder.vipDiscountPercent;
        this.timeDiscountPercent = builder.timeDiscountPercent;
        this.fullReductionThreshold = builder.fullReductionThreshold;
        this.fullReductionAmount = builder.fullReductionAmount;
        this.defaultPayChannel = builder.defaultPayChannel;
    }

    /** 获取全局唯一配置。 */
    public static AppConfig get() {
        return Holder.INSTANCE;
    }

    /** 单日消费上限（分）。 */
    public int dailyLimit() {
        return dailyLimit;
    }

    /** VIP 折扣百分比。 */
    public int vipDiscountPercent() {
        return vipDiscountPercent;
    }

    /** 时段折扣百分比（13:00-14:00）。 */
    public int timeDiscountPercent() {
        return timeDiscountPercent;
    }

    /** 满减门槛（分）。 */
    public int fullReductionThreshold() {
        return fullReductionThreshold;
    }

    /** 满减金额（分）。 */
    public int fullReductionAmount() {
        return fullReductionAmount;
    }

    /** 默认支付渠道。 */
    public String defaultPayChannel() {
        return defaultPayChannel;
    }
}
