package edu.dlut.campuscard.service;

import edu.dlut.campuscard.model.Card;
import edu.dlut.campuscard.model.Result;
import edu.dlut.campuscard.model.Transaction;
import edu.dlut.campuscard.notify.CardEvent;
import edu.dlut.campuscard.pricing.PriceRequest;
import edu.dlut.campuscard.validation.ConsumeContext;

/**
 * 标准消费流程：为 {@link ConsumeFlow} 的骨架提供具体步骤实现。
 *
 * <p>各步骤仅做编排，具体算法委托给对应模式组件：
 * 校验链（Chain）、计费器（Strategy + Decorator）、网关（Adapter/Factory）、
 * 台账与商户账户（Composite/Visitor）、通知（Observer）。</p>
 */
public final class StandardConsumeFlow extends ConsumeFlow {

    private final CardService service;

    public StandardConsumeFlow(CardService service) {
        this.service = service;
    }

    @Override
    protected void resolve(ConsumeContext context) {
        context.setCard(service.card(context.cardId()));
        context.setMerchant(service.merchant(context.merchantId()));
    }

    @Override
    protected Result preCheck(ConsumeContext context) {
        return service.validationChain().preConsumeChain().handle(context);
    }

    @Override
    protected void price(ConsumeContext context) {
        PriceRequest request = new PriceRequest(context.amount(), context.hour(), context.card().vip());
        context.setPaid(service.priceCalculator().calculate(context.merchant(), request));
    }

    @Override
    protected Result postCheck(ConsumeContext context) {
        return service.validationChain().postConsumeChain().handle(context);
    }

    @Override
    protected void settle(ConsumeContext context) {
        Card card = context.card();
        long paid = context.paid();
        String merchantId = context.merchantId();

        // 扣款与当日累计
        card.decreaseBalance(paid);
        card.addTodaySpent(paid);

        // 支付渠道（Adapter + 工厂）
        String channel = service.gatewayFactory()
                .gatewayFor(card.type())
                .pay(card.id(), context.amount());

        // 记账
        String txnId = "T" + service.nextTxnSeq();
        service.transactions().add(new Transaction(
                txnId, card.id(), merchantId, context.amount(), paid, channel, "消费"));

        // 台账与商户账户
        service.ledger().addConsumed(paid);
        service.merchantAccount(merchantId).record(paid);

        // 通知（Observer）
        service.publisher().publish(new CardEvent(
                CardEvent.EventType.CONSUME, card.id(), card.holder(),
                "消费 " + card.id() + " 金额 " + paid));
    }
}
