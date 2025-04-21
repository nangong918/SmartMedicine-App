package com.czy.oss.component;


import com.czy.api.constant.mq.OssMqConstant;
import com.czy.api.constant.mq.PostMqConstant;
import com.czy.api.domain.entity.event.OssResponse;
import com.czy.api.domain.entity.event.OssTask;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * @author 13225
 * @date 2025/4/1 13:59
 */

@Slf4j
@RequiredArgsConstructor
@Component
public class RabbitMqSender {

    private final RabbitTemplate rabbitTemplate;

    public void pushToOss(OssResponse ossResponse){
        rabbitTemplate.convertAndSend(OssMqConstant.OSS_TO_SERVICE_QUEUE, ossResponse);
    }
    
}
