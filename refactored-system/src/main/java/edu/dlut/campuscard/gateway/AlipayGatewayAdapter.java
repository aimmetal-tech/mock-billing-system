package edu.dlut.campuscard.gateway;

/** 支付宝适配器：把 {@link AlipayThirdParty} 适配为统一的 {@link PaymentGateway}。 */
public class AlipayGatewayAdapter implements PaymentGateway {

    private final AlipayThirdParty thirdParty = new AlipayThirdParty();

    @Override
    public String pay(String cardId, long amount) {
        return thirdParty.doAlipay(cardId, amount);
    }
}
