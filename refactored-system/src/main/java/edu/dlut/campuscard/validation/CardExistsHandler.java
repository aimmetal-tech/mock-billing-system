package edu.dlut.campuscard.validation;

import edu.dlut.campuscard.model.Result;

/** 校验：卡片必须存在（原因“卡不存在”）。 */
public final class CardExistsHandler extends ValidationHandler {

    @Override
    protected Result check(ConsumeContext context) {
        if (context.card() == null) {
            return Result.reject("卡不存在", 0);
        }
        return null;
    }
}
