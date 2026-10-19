package edu.dlut.campuscard.validation;

import edu.dlut.campuscard.model.Result;

/**
 * 校验：卡片当前状态是否允许消费。
 *
 * <p>拒绝原因由状态对象自身给出（State 模式协作），从而消除分散的状态分支。</p>
 */
public final class CardStatusHandler extends ValidationHandler {

    @Override
    protected Result check(ConsumeContext context) {
        String reason = context.card().state().rejectConsume();
        if (reason != null) {
            return Result.reject(reason, context.card().balance());
        }
        return null;
    }
}
