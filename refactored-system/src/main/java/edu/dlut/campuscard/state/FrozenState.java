package edu.dlut.campuscard.state;

import edu.dlut.campuscard.model.CardStatus;

/** 冻结态：禁止消费（原因“卡已冻结”），不可挂失/解挂。 */
public final class FrozenState implements CardState {

    /** 单例。 */
    public static final FrozenState INSTANCE = new FrozenState();

    private FrozenState() {
    }

    @Override
    public CardStatus status() {
        return CardStatus.FROZEN;
    }

    @Override
    public String name() {
        return "FROZEN";
    }

    @Override
    public String rejectConsume() {
        return "卡已冻结";
    }
}
