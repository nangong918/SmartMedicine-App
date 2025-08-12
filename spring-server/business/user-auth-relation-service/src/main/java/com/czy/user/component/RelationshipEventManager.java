package com.czy.user.component;


import com.czy.api.converter.base.BaseRequestConverter;
import com.czy.springUtils.debug.DebugConfig;
import com.czy.user.handler.FriendHandler;
import com.utils.mvc.component.EventManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class RelationshipEventManager<T> extends EventManager<T> {

    private final FriendHandler friendHandler;
    private final DebugConfig debugConfig;
    private final BaseRequestConverter baseRequestConverter;


    private void initEventManager(){
        List<Object> handlerBeans = new ArrayList<>();
        handlerBeans.add(friendHandler);
        super.initEventManager(handlerBeans, debugConfig, baseRequestConverter);
    }

    @PostConstruct
    public void init() {
        initEventManager();
    }

}
