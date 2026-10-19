package edu.dlut.campuscard.gateway;

/** 微信适配器：把 {@link WechatThirdParty} 适配为统一的 {@link PaymentGateway}。 */
public class WechatGatewayAdapter implements PaymentGateway {

    private final WechatThirdParty thirdParty = new WechatThirdParty();

    @Override
    public String pay(String cardId, long amount) {
        return thirdParty.wxPayRequest(cardId, amount);
    }
}
