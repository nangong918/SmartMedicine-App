package com.czy.logging.handler.eventListener;

import com.czy.api.domain.entity.event.Message;
import com.czy.api.domain.entity.event.event.BigDataEvent;
import com.czy.logging.component.LoggingEventManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
//import org.springframework.context.event.EventListener;

/**
 * @author 13225
 * @date 2025/4/2 14:57
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class RelationshipRouteListener implements ApplicationListener<BigDataEvent> {

    private final LoggingEventManager<Message> loggingEventManager;
//    @EventListener // 继承了ApplicationListener就不需要@EventListener
    @Override
    public void onApplicationEvent(@NotNull BigDataEvent event) {
        Message message = event.getSource();
        // 路由处理消息
        loggingEventManager.process(message);
    }
}
