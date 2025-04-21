package com.czy.post.mqHander;

import com.czy.api.constant.mq.OssMqConstant;
import com.czy.api.constant.post.PostConstant;
import com.czy.api.domain.entity.event.OssResponse;
import com.czy.api.domain.entity.event.event.OssResponseEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.validation.Valid;

/**
 * @author 13225
 * @date 2025/4/18 23:49
 */

@Slf4j
@RequiredArgsConstructor
@Component
@RabbitListener(queues = OssMqConstant.OSS_TO_SERVICE_QUEUE)
public class OssMqHandler {

    private final ApplicationContext applicationContext;

    @RabbitHandler
    public void handleMessage(@Valid OssResponse ossResponse) {
        if (ossResponse != null &&
                // 属于本服务的事件
                PostConstant.serviceName.equals(ossResponse.serviceId)){
            applicationContext.publishEvent(new OssResponseEvent(ossResponse));
        }
    }

}
