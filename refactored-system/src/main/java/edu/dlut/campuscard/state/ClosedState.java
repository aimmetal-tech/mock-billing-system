package edu.dlut.campuscard.state;

import edu.dlut.campuscard.model.CardStatus;

/** 注销态：禁止消费与充值（原因“卡已注销”），不可挂失/解挂。 */
public final class ClosedState implements CardState {

    /** 单例。 */
    public static final ClosedState INSTANCE = new ClosedState();

    private ClosedState() {
    }

    @Override
    public CardStatus status() {
        return CardStatus.CLOSED;
    }

    @Override
    public String name() {
        return "CLOSED";
    }

    @Override
    public String rejectConsume() {
        return "卡已注销";
    }

    @Override
    public String rejectRecharge() {
        return "卡已注销";
    }
}
