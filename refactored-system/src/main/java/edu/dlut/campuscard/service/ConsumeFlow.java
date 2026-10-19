package edu.dlut.campuscard.service;

import edu.dlut.campuscard.model.Result;
import edu.dlut.campuscard.validation.ConsumeContext;

/**
 * 消费流程模板（Template Method 模式）。
 *
 * <p>用 {@code final} 的 {@link #execute} 固定“解析 → 计费前校验 → 计费 → 计费后校验 → 结算”
 * 这一不可变的算法骨架，各步骤留给子类实现。取代重构前把整条流程写死在 {@code consume()} 里的做法，
 * 使流程可被扩展（例如新增“风控前置”步骤）而无需改动骨架。</p>
 */
public abstract class ConsumeFlow {

    /**
     * 执行一笔消费（模板方法，不可被子类覆盖）。
     *
     * @return 操作结果
     */
    public final Result execute(String cardId, String merchantId, long amount, int hour) {
        ConsumeContext context = new ConsumeContext(cardId, merchantId, amount, hour);

        resolve(context);

        Result preRejected = preCheck(context);
        if (preRejected != null) {
            return preRejected;
        }

        price(context);

        Result postRejected = postCheck(context);
        if (postRejected != null) {
            return postRejected;
        }

        settle(context);
        return Result.ok(context.paid(), context.card().balance());
    }

    /** 解析卡片与商户。 */
    protected abstract void resolve(ConsumeContext context);

    /** 计费前校验（存在性、状态、金额合法性）。 */
    protected abstract Result preCheck(ConsumeContext context);

    /** 计算应付金额。 */
    protected abstract void price(ConsumeContext context);

    /** 计费后校验（余额、单日限额）。 */
    protected abstract Result postCheck(ConsumeContext context);

    /** 扣款、记账、账户与通知。 */
    protected abstract void settle(ConsumeContext context);
}
