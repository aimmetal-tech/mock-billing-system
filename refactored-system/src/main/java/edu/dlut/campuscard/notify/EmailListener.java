package edu.dlut.campuscard.notify;

/** 邮件通知订阅者（模拟发送）。 */
public class EmailListener implements CardEventListener {

    @Override
    public void onEvent(CardEvent event) {
        // 模拟发送邮件，内容：event.message()
    }
}
