package com.czy.oss.listener;

import com.utils.mvc.config.MinIOConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class StartListener implements ApplicationListener<ApplicationReadyEvent> {

    private final MinIOConfig minIOConfig;

    @Override
    public void onApplicationEvent(@NotNull ApplicationReadyEvent event) {
        log.info("oss服务启动成功: oss配置情况: {}",
                Optional.ofNullable(minIOConfig)
                        .map(MinIOConfig::toString)
                        .orElse(null)
        );
    }
}
