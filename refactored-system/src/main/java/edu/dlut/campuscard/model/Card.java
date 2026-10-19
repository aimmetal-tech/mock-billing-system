package edu.dlut.campuscard.model;

import edu.dlut.campuscard.state.CardState;
import edu.dlut.campuscard.state.NormalState;

/**
 * 一卡通卡片，同时充当<b>状态模式</b>的 Context。
 *
 * <p>重构前卡片状态是一个 {@code int}，到处用 if-else 判断“这个状态下能不能做某事”；
 * 重构后把状态行为内聚到 {@link CardState} 家族，卡片只负责持有当前状态并在合法时切换。</p>
 */
public class Card {

    private final String id;
    private final String holder;
    private final CardType type;
    private final boolean vip;

    /** 余额（分）。 */
    private long balance;
    /** 当日累计消费（分）。 */
    private long todaySpent;
    /** 当日标识（重构前用 {@code hour + "-day"} 作为占位日期）。 */
    private String todayDate = "";

    /** 当前状态对象（默认正常态）。 */
    private CardState state = NormalState.INSTANCE;

    public Card(String id, String holder, CardType type, boolean vip, long balance) {
        this.id = id;
        this.holder = holder;
        this.type = type;
        this.vip = vip;
        this.balance = balance;
    }

    // ---------------- 只读属性 ----------------
    public String id() {
        return id;
    }

    public String holder() {
        return holder;
    }

    public CardType type() {
        return type;
    }

    public boolean vip() {
        return vip;
    }

    public long balance() {
        return balance;
    }

    // ---------------- 状态（State 模式） ----------------
    public CardState state() {
        return state;
    }

    /** 切换状态（由服务层在状态允许时调用）。 */
    public void transitionTo(CardState next) {
        this.state = next;
    }

    /** 当前状态标识。 */
    public CardStatus status() {
        return state.status();
    }

    /** 当前状态展示名（NORMAL/LOST/FROZEN/CLOSED）。 */
    public String statusName() {
        return state.name();
    }

    // ---------------- 金额变动 ----------------
    public void decreaseBalance(long amount) {
        balance -= amount;
    }

    public void increaseBalance(long amount) {
        balance += amount;
    }

    // ---------------- 当日累计 ----------------
    public String todayDate() {
        return todayDate;
    }

    public long todaySpent() {
        return todaySpent;
    }

    /** 重置当日累计（跨日时调用）。 */
    public void resetToday(String date) {
        this.todayDate = date;
        this.todaySpent = 0;
    }

    public void addTodaySpent(long amount) {
        todaySpent += amount;
    }
}
