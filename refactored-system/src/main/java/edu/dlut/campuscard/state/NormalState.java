package edu.dlut.campuscard.state;

import edu.dlut.campuscard.model.CardStatus;

/** 正常态：允许消费与充值，可挂失，不可解挂。 */
public final class NormalState implements CardState {

    /** 单例。 */
    public static final NormalState INSTANCE = new NormalState();

    private NormalState() {
    }

    @Override
    public CardStatus status() {
        return CardStatus.NORMAL;
    }

    @Override
    public String name() {
        return "NORMAL";
    }

    @Override
    public boolean canReportLoss() {
        return true;
    }
}
