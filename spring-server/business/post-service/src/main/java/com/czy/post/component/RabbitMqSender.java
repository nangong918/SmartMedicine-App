package com.czy.post.component;


import com.czy.api.constant.mq.PostMqConstant;
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

    public void pushToOss(OssTask ossTask){
        rabbitTemplate.convertAndSend(PostMqConstant.SERVICE_TO_OSS_QUEUE, ossTask);
    }
    
}
