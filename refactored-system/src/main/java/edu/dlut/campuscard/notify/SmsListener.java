package edu.dlut.campuscard.notify;

/** 短信通知订阅者（模拟发送）。 */
public class SmsListener implements CardEventListener {

    @Override
    public void onEvent(CardEvent event) {
        // 模拟发送短信，内容：event.message()
    }
}
