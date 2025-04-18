package com.czy.api.domain.entity.event.event;

import com.czy.api.domain.entity.event.Message;
import org.springframework.context.ApplicationEvent;

/**
 * @author 13225
 * @date 2025/4/2 15:01
 */
public class BigDataEvent extends ApplicationEvent {
    public BigDataEvent(Message message) {
        super(message);
    }

    @Override
    public Message getSource() {
        return (Message) source;
    }
}
