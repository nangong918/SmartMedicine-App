package com.czy.search.listener;

import com.czy.search.config.SearchTestConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * @author 13225
 * @date 2025/6/7 11:25
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class StartListener implements ApplicationListener<ApplicationReadyEvent> {

    private final SearchTestConfig searchTestConfig;

    @Override
    public void onApplicationEvent(@NotNull ApplicationReadyEvent event) {
        log.info("启动成功，debug config情况：{}",  searchTestConfig.isDebug);
    }
}
