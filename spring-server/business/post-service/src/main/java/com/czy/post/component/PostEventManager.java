package com.czy.post.component;

import com.czy.api.converter.base.BaseRequestConverter;
import com.czy.post.handler.PostHandler;
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
public class PostEventManager<T> extends EventManager<T> {

    private final PostHandler postHandler;
    private final BaseRequestConverter baseRequestConverter;

    private void initEventManager(){
        List<Object> handlerBeans = new ArrayList<>();
        handlerBeans.add(postHandler);
        super.initEventManager(handlerBeans, baseRequestConverter);
    }

    @PostConstruct
    public void init() {
        initEventManager();
    }

}
