package edu.dlut.campuscard.gateway;

import edu.dlut.campuscard.model.CardType;

/**
 * 支付网关工厂（Abstract Factory 思路）。
 *
 * <p>把“持卡人类型 → 支付渠道”的决策收敛到一处，业务不再出现
 * {@code if (type.equals("TEACHER")) ... else ...} 的硬编码。渠道差异由适配器吸收。</p>
 *
 * <p>映射与重构前一致：教师走微信，其余（含学生、教职工）走支付宝。</p>
 */
public class PaymentGatewayFactory {

    private final PaymentGateway alipay = new AlipayGatewayAdapter();
    private final PaymentGateway wechat = new WechatGatewayAdapter();

    /** 按持卡人类型选择支付网关。 */
    public PaymentGateway gatewayFor(CardType type) {
        if (type == CardType.TEACHER) {
            return wechat;
        }
        return alipay;
    }
}
