package edu.dlut.campuscard.validation;

import edu.dlut.campuscard.config.AppConfig;

/**
 * 校验链装配（Chain of Responsibility 模式的组装点）。
 *
 * <p>把消费校验显式切成两段，顺序严格复刻重构前语义：</p>
 * <ol>
 *   <li>计费前：卡存在 → 商户存在 → 卡状态 → 金额合法性</li>
 *   <li>计费后：余额充足 → 单日限额（含跨日重置）</li>
 * </ol>
 */
public final class ValidationChain {

    private final AppConfig config;

    public ValidationChain(AppConfig config) {
        this.config = config;
    }

    /** 计费前校验链。 */
    public ValidationHandler preConsumeChain() {
        ValidationHandler head = new CardExistsHandler();
        head.setNext(new MerchantExistsHandler())
                .setNext(new CardStatusHandler())
                .setNext(new AmountHandler());
        return head;
    }

    /** 计费后校验链。 */
    public ValidationHandler postConsumeChain() {
        ValidationHandler head = new BalanceHandler();
        head.setNext(new DailyLimitHandler(config));
        return head;
    }
}
