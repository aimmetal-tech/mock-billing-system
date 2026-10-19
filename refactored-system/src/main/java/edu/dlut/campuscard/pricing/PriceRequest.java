package edu.dlut.campuscard.pricing;

/**
 * 计费请求（不可变），承载计费所需的全部上下文。
 *
 * @param amount 消费金额（分）
 * @param hour   消费所在小时（用于时段折扣）
 * @param vip    是否 VIP
 */
public record PriceRequest(long amount, int hour, boolean vip) {
}
