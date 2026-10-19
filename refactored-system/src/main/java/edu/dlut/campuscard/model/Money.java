package edu.dlut.campuscard.model;

/**
 * 金额工具：系统内金额统一以“分”（long）表示，避免浮点误差。
 *
 * <p>保留重构前 {@code pct()} 的四舍五入语义：{@code (amount * percent + 50) / 100}，
 * 以保证计费结果逐分等价。</p>
 */
public final class Money {

    private Money() {
    }

    /**
     * 计算 amount 的 percent 百分比，四舍五入到分。
     *
     * @param amount  金额（分）
     * @param percent 百分比（整数）
     * @return 四舍五入后的金额（分）
     */
    public static long percentOf(long amount, int percent) {
        return (amount * percent + 50) / 100;
    }
}
