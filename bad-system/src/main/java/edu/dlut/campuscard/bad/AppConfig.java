package edu.dlut.campuscard.bad;

/**
 * 坏味道：全局可变静态配置（Singleton 反面）。
 * 所有常量都是 public static 且可被任意类随意修改，测试之间会互相污染。
 */
public class AppConfig {
    public static int DAILY_LIMIT = 20000;              // 单日消费上限（分）
    public static int VIP_DISCOUNT_PERCENT = 5;         // VIP 折扣百分比
    public static int TIME_DISCOUNT_PERCENT = 10;       // 13:00-14:00 时段折扣百分比
    public static int FULL_REDUCTION_THRESHOLD = 5000;  // 满减门槛（分）
    public static int FULL_REDUCTION_AMOUNT = 300;      // 满减金额（分）
    public static String DEFAULT_PAY_CHANNEL = "ALIPAY";
}
