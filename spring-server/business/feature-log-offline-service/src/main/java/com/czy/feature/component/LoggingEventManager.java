package com.czy.feature.component;

import com.czy.api.converter.base.BaseRequestConverter;
import com.czy.feature.handler.LoggingHandler;
import com.utils.rabbitmq.component.EventManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 13225
 * @date 2025/5/23 16:49
 */

@Slf4j
@RequiredArgsConstructor
@Component
public class LoggingEventManager<T> extends EventManager<T> {

    private final LoggingHandler loggingHandler;
    private final BaseRequestConverter baseRequestConverter;

    private void initEventManager(){
        List<Object> handlerBeans = new ArrayList<>();
        handlerBeans.add(loggingHandler);
        super.initEventManager(handlerBeans, baseRequestConverter);
    }

    @PostConstruct
    public void init() {
        initEventManager();
    }

}
