package com.czy.gateway.listener;

import com.czy.springUtils.debug.DebugConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * @author 13225
 * @date 2025/7/22 11:49
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class StartListener implements ApplicationListener<ApplicationReadyEvent> {

    private final DebugConfig debugConfig;

    @Override
    public void onApplicationEvent(@NotNull ApplicationReadyEvent event) {
        log.info("gateway服务启动成功: debug配置情况: {}",
                Optional.ofNullable(debugConfig)
                        .map(DebugConfig::toString)
                        .orElse(null)
        );
    }
}
