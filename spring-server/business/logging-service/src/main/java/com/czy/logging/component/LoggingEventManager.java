package com.czy.logging.component;

import com.czy.api.converter.base.BaseRequestConverter;
import com.czy.logging.handler.LoggingHandler;
import com.czy.springUtils.debug.DebugConfig;
import com.utils.mvc.component.EventManager;
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
    private final DebugConfig debugConfig;
    private final BaseRequestConverter baseRequestConverter;

    private void initEventManager(){
        List<Object> handlerBeans = new ArrayList<>();
        handlerBeans.add(loggingHandler);
        super.initEventManager(handlerBeans, debugConfig, baseRequestConverter);
    }

    @PostConstruct
    public void init() {
        initEventManager();
    }

}
