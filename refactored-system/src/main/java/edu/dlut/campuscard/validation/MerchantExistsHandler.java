package edu.dlut.campuscard.validation;

import edu.dlut.campuscard.model.Result;

/** 校验：商户必须存在（原因“商户不存在”）。 */
public final class MerchantExistsHandler extends ValidationHandler {

    @Override
    protected Result check(ConsumeContext context) {
        if (context.merchant() == null) {
            return Result.reject("商户不存在", context.card().balance());
        }
        return null;
    }
}
