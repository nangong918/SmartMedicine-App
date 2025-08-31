package com.czy.spring.eventListener;


import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JvmStartListener implements ApplicationListener<ApplicationReadyEvent> {

    @Override
    public void onApplicationEvent(@NotNull ApplicationReadyEvent event) {
        // 获取和输出 JVM 参数
        long initialHeapSize = Runtime.getRuntime().totalMemory();
        long maxHeapSize = Runtime.getRuntime().maxMemory();

        // Edit Configuration -> Modify Options -> Add VM Options
        // -Xms256m -Xmx512m
        log.info("Jvm检查： -Xms Initial Heap Size: {} MB", initialHeapSize / (1024 * 1024));
        log.info("Jvm检查： -Xmx Max Heap Size: {} MB", maxHeapSize / (1024 * 1024));
    }

}
