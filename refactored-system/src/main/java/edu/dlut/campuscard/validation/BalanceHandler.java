package edu.dlut.campuscard.validation;

import edu.dlut.campuscard.model.Result;

/**
 * 校验：应付金额不得超过余额（原因“余额不足”）。
 *
 * <p>位于计费之后，因此须在链的后半段装配。</p>
 */
public final class BalanceHandler extends ValidationHandler {

    @Override
    protected Result check(ConsumeContext context) {
        if (context.paid() > context.card().balance()) {
            return Result.reject("余额不足", context.card().balance());
        }
        return null;
    }
}
