package edu.dlut.campuscard.gateway;

/**
 * 模拟的支付宝第三方 SDK（接口风格与业务需求不一致，需要被适配）。
 */
public class AlipayThirdParty {

    /** 支付宝接口：以 uid + 分为单位调用。 */
    public String doAlipay(String uid, long fen) {
        return "ALIPAY";
    }
}
