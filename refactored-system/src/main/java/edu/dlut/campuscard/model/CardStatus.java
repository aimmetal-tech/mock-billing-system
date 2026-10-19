package edu.dlut.campuscard.model;

/**
 * 卡片状态枚举。
 *
 * <p>重构前用 {@code int status == 1/2/3/4} 贯穿全系统，语义靠注释维系；
 * 重构后由 {@code state} 包的状态类持有，本枚举仅作为对外展示与查询用的稳定标识。</p>
 */
public enum CardStatus {

    /** 正常。 */
    NORMAL,
    /** 已挂失。 */
    LOST,
    /** 已冻结。 */
    FROZEN,
    /** 已注销。 */
    CLOSED
}
