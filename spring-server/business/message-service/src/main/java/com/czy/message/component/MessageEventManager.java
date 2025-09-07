package com.czy.message.component;

import com.czy.api.converter.base.BaseRequestConverter;
import com.czy.message.handler.ChatHandler;
import com.czy.message.handler.ConnectHandler;
import com.czy.message.handler.ToServerHandler;
import com.utils.rabbitmq.component.EventManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class MessageEventManager <T> extends EventManager<T> {

    private final ChatHandler chatHandler;
    private final ConnectHandler connectHandler;
    private final ToServerHandler toServerHandler;

    private final BaseRequestConverter baseRequestConverter;

    private void initEventManager(){
        List<Object> handlerBeans = new ArrayList<>();
        handlerBeans.add(chatHandler);
        handlerBeans.add(connectHandler);
        handlerBeans.add(toServerHandler);
        super.initEventManager(handlerBeans, baseRequestConverter);
    }

    @PostConstruct
    public void init() {
        initEventManager();
    }
}
