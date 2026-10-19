package edu.dlut.campuscard.notify;

/**
 * 卡片事件（观察者模式中的“事件/消息”）。
 *
 * @param type    事件类型
 * @param cardId  卡片号
 * @param holder  持卡人
 * @param message 通知正文
 */
public record CardEvent(EventType type, String cardId, String holder, String message) {

    /** 事件类型。 */
    public enum EventType {
        /** 消费成功。 */
        CONSUME,
        /** 挂失。 */
        LOSS
    }
}
