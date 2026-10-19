package edu.dlut.campuscard.validation;

import edu.dlut.campuscard.config.AppConfig;
import edu.dlut.campuscard.model.Card;
import edu.dlut.campuscard.model.Result;

/**
 * 校验：当日累计消费不得超过单日限额（原因“超出单日限额”）。
 *
 * <p>同时负责“跨日重置”：当日标识变化时把当日累计清零，
 * 与重构前 {@code todaySpent} 的语义保持一致。</p>
 */
public final class DailyLimitHandler extends ValidationHandler {

    private final AppConfig config;

    public DailyLimitHandler(AppConfig config) {
        this.config = config;
    }

    @Override
    protected Result check(ConsumeContext context) {
        Card card = context.card();
        String today = context.hour() + "-day";
        if (!today.equals(card.todayDate())) {
            card.resetToday(today);
        }
        if (card.todaySpent() + context.paid() > config.dailyLimit()) {
            return Result.reject("超出单日限额", card.balance());
        }
        return null;
    }
}
