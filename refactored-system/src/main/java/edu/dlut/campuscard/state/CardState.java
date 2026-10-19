package edu.dlut.campuscard.state;

import edu.dlut.campuscard.model.CardStatus;

/**
 * 卡片状态接口（State 模式）。
 *
 * <p>把“每种状态下允许/禁止哪些操作、被禁止时给出什么原因”内聚到各自的状态类中，
 * 取代重构前散落各处的 {@code if (status == ...)} 分支。</p>
 *
 * <p>状态类均无实例状态，作为单例复用。</p>
 */
public interface CardState {

    /** 状态标识。 */
    CardStatus status();

    /** 状态展示名。 */
    String name();

    /** 消费时若被本状态禁止，返回拒绝原因；允许则返回 {@code null}。 */
    default String rejectConsume() {
        return null;
    }

    /** 充值时若被本状态禁止，返回拒绝原因；允许则返回 {@code null}。 */
    default String rejectRecharge() {
        return null;
    }

    /** 是否允许挂失。 */
    default boolean canReportLoss() {
        return false;
    }

    /** 是否允许解挂。 */
    default boolean canRecover() {
        return false;
    }
}
