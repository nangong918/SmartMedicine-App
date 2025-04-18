package com.czy.api.domain.entity.event.event;


import com.czy.api.domain.entity.event.Message;
import org.springframework.context.ApplicationEvent;

public class MessageRouteEvent extends ApplicationEvent {
    public MessageRouteEvent(Message message) {
        super(message);
    }

    @Override
    public Message getSource() {
        return (Message) source;
    }

}
