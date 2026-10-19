package edu.dlut.campuscard.notify;

/** App 推送通知订阅者（模拟发送）。 */
public class AppPushListener implements CardEventListener {

    @Override
    public void onEvent(CardEvent event) {
        // 模拟 App 推送，内容：event.message()
    }
}
