package edu.dlut.campuscard.validation;

import edu.dlut.campuscard.model.Result;

/** 校验：金额必须为正（原因“金额非法”）。 */
public final class AmountHandler extends ValidationHandler {

    @Override
    protected Result check(ConsumeContext context) {
        if (context.amount() <= 0) {
            return Result.reject("金额非法", context.card().balance());
        }
        return null;
    }
}
