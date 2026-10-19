package edu.dlut.campuscard.notify;

/** 卡片事件监听者（观察者模式中的 Observer）。 */
@FunctionalInterface
public interface CardEventListener {

    /** 收到事件时的回调。 */
    void onEvent(CardEvent event);
}
