package edu.dlut.campuscard.notify;

import java.util.ArrayList;
import java.util.List;

/**
 * 事件发布者（观察者模式中的 Subject）。
 *
 * <p>取代重构前“在业务方法里依次 {@code new SmsThing()/PushThing()/MailThing()}”的硬编码：
 * 业务只发布事件，具体通知渠道由订阅者决定，新增渠道无需改动业务代码。</p>
 */
public class EventPublisher {

    private final List<CardEventListener> listeners = new ArrayList<>();

    /** 订阅事件。 */
    public void subscribe(CardEventListener listener) {
        listeners.add(listener);
    }

    /** 发布事件，通知所有订阅者。 */
    public void publish(CardEvent event) {
        for (CardEventListener listener : listeners) {
            listener.onEvent(event);
        }
    }
}
