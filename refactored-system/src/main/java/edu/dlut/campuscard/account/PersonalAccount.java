package edu.dlut.campuscard.account;

import edu.dlut.campuscard.model.Card;

/** 个人账户（叶子）：直接映射到一张卡片，余额即卡内余额。 */
public class PersonalAccount implements AccountComponent {

    private final Card card;

    public PersonalAccount(Card card) {
        this.card = card;
    }

    public Card card() {
        return card;
    }

    /** 当前余额（分）。 */
    public long balance() {
        return card.balance();
    }

    @Override
    public void accept(AccountVisitor visitor) {
        visitor.visit(this);
    }
}
