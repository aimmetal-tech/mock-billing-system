package edu.dlut.campuscard.gateway;

/**
 * 统一支付网关接口（Adapter 模式的 Target）。
 *
 * <p>重构前业务代码直接 {@code new AlipayThing().pay(...)}，第三方接口各不相同的签名侵入业务；
 * 重构后业务只面向本统一接口，差异由适配器吸收。</p>
 */
public interface PaymentGateway {

    /**
     * 发起支付。
     *
     * @param cardId 卡号
     * @param amount 金额（分）
     * @return 使用的支付渠道标识（如 ALIPAY / WECHAT）
     */
    String pay(String cardId, long amount);
}
