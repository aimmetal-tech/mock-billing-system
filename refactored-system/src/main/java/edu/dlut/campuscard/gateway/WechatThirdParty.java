package edu.dlut.campuscard.gateway;

/**
 * 模拟的微信第三方 SDK（接口命名、参数与支付宝完全不同，需要被适配）。
 */
public class WechatThirdParty {

    /** 微信接口：以 openid + 分为单位请求。 */
    public String wxPayRequest(String openid, long cent) {
        return "WECHAT";
    }
}
