package com.czy.test.listener;

import com.czy.test.config.TestDebugConfig;
import com.utils.minio.config.MinIOConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * @author 13225
 * @date 2025/7/21 11:21
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class StartListener implements ApplicationListener<ApplicationReadyEvent> {

    private final TestDebugConfig testDebugConfig;
    private final MinIOConfig minIOConfig;


    @Override
    public void onApplicationEvent(@NotNull ApplicationReadyEvent event) {
        log.info("服务启动成功: 启动情况: {}",
                Optional.ofNullable(testDebugConfig)
                        .map(TestDebugConfig::toString)
                        .orElse(null)
        );
        log.info("启动成功检查: minio配置情况: {}",
                Optional.ofNullable(minIOConfig)
                        .map(MinIOConfig::toString)
                        .orElse(null)
        );
    }
}
