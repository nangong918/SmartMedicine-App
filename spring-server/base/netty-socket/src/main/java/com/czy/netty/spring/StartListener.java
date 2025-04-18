package com.czy.netty.spring;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * @author 13225
 * @date 2025/4/3 9:50
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class StartListener {

    @EventListener
    public void onApplicationReady(ApplicationReadyEvent event) {
    }
}
