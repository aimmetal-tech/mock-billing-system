package edu.dlut.campuscard.state;

import edu.dlut.campuscard.model.CardStatus;

/** 已挂失态：禁止消费（原因“卡已挂失”），允许解挂。 */
public final class LostState implements CardState {

    /** 单例。 */
    public static final LostState INSTANCE = new LostState();

    private LostState() {
    }

    @Override
    public CardStatus status() {
        return CardStatus.LOST;
    }

    @Override
    public String name() {
        return "LOST";
    }

    @Override
    public String rejectConsume() {
        return "卡已挂失";
    }

    @Override
    public boolean canRecover() {
        return true;
    }
}
