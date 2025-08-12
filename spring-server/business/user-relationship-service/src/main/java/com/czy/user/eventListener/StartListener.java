package com.czy.user.eventListener;

import com.czy.springUtils.debug.DebugConfig;
import com.czy.user.constant.SmsConstant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * @author 13225
 * @date 2025/8/12 16:22
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class StartListener implements ApplicationListener<ApplicationReadyEvent> {

    private final DebugConfig debugConfig;
    private final SmsConstant smsConstant;

    @Override
    public void onApplicationEvent(@NotNull ApplicationReadyEvent event) {
        log.info("user-service启动成功，debug config情况：{}\n sms config情况：{}",  debugConfig.toString(), smsConstant.toString());
    }
}
