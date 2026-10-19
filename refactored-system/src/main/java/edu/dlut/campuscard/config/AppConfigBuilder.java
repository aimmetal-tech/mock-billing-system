package edu.dlut.campuscard.config;

/**
 * 配置建造者（Builder 模式）。
 *
 * <p>为 {@link AppConfig} 提供链式、带默认值的构造方式，
 * 默认值与重构前 {@code AppConfig} 的常量保持完全一致。</p>
 */
public class AppConfigBuilder {

    /** 单日消费上限（分）。 */
    int dailyLimit = 20000;
    /** VIP 折扣百分比。 */
    int vipDiscountPercent = 5;
    /** 时段折扣百分比。 */
    int timeDiscountPercent = 10;
    /** 满减门槛（分）。 */
    int fullReductionThreshold = 5000;
    /** 满减金额（分）。 */
    int fullReductionAmount = 300;
    /** 默认支付渠道。 */
    String defaultPayChannel = "ALIPAY";

    public AppConfigBuilder dailyLimit(int value) {
        this.dailyLimit = value;
        return this;
    }

    public AppConfigBuilder vipDiscountPercent(int value) {
        this.vipDiscountPercent = value;
        return this;
    }

    public AppConfigBuilder timeDiscountPercent(int value) {
        this.timeDiscountPercent = value;
        return this;
    }

    public AppConfigBuilder fullReductionThreshold(int value) {
        this.fullReductionThreshold = value;
        return this;
    }

    public AppConfigBuilder fullReductionAmount(int value) {
        this.fullReductionAmount = value;
        return this;
    }

    public AppConfigBuilder defaultPayChannel(String value) {
        this.defaultPayChannel = value;
        return this;
    }

    /** 构建不可变配置。 */
    public AppConfig build() {
        return new AppConfig(this);
    }
}
