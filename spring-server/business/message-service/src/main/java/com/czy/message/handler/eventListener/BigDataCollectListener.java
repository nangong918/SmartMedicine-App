package com.czy.message.handler.eventListener;

import com.czy.api.domain.entity.event.event.BigDataEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
//import org.springframework.context.event.EventListener;


/**
 * @author 13225
 * @date 2025/4/2 14:57
 * 大数据收集监听者，事件驱动系统种一份事件能被多个监听者收到。
 * 此份监听者是用于监听用户的全部大数据数据，然后交给Kafka存入数据平台做统计用的监听者。
 */

@Slf4j
@RequiredArgsConstructor
@Component
public class BigDataCollectListener implements ApplicationListener<BigDataEvent> {
//    @EventListener
    @Override
    public void onApplicationEvent(@NotNull BigDataEvent event) {
        // 将数据取出，交给Kafka
    }
}
