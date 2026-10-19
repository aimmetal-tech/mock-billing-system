package edu.dlut.campuscard.model;

/**
 * 统一操作结果（不可变值对象）。
 *
 * <p>字段与重构前 {@code CampusCardSystem.Result} 完全一致：{@code status/reason/paid/balance}，
 * 保证对外表现等价。</p>
 */
public final class Result {

    /** 操作状态：成功。 */
    public static final String OK = "OK";
    /** 操作状态：被拒绝。 */
    public static final String REJECT = "REJECT";

    /** 状态（OK / REJECT）。 */
    public final String status;
    /** 拒绝原因（成功时为空串）。 */
    public final String reason;
    /** 实付金额（分）。 */
    public final long paid;
    /** 操作后的余额（分）。 */
    public final long balance;

    private Result(String status, String reason, long paid, long balance) {
        this.status = status;
        this.reason = reason;
        this.paid = paid;
        this.balance = balance;
    }

    /** 成功结果。 */
    public static Result ok(long paid, long balance) {
        return new Result(OK, "", paid, balance);
    }

    /** 拒绝结果（实付恒为 0）。 */
    public static Result reject(String reason, long balance) {
        return new Result(REJECT, reason, 0, balance);
    }

    /** 是否成功。 */
    public boolean isOk() {
        return OK.equals(status);
    }
}
