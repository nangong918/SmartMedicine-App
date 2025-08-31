package com.czy.purchase.service.impl;

import com.api.mapper.purchase.redis.PayRedisMapper;
import com.czy.api.constant.UserOrderStatusEnum;
import com.czy.api.converter.domain.purchase.AppointmentPayConverter;
import com.czy.api.domain.dto.mq.AppointmentOrderDto;
import com.czy.api.domain.dto.mq.AppointmentPayResultDto;
import com.czy.purchase.mq.PayMqSender;
import com.czy.purchase.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * @author 13225
 * @date 2025/8/26 9:36
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {

    private final PayMqSender payMqSender;
    private final AppointmentPayConverter appointmentPayConverter;
    private final PayRedisMapper payRedisMapper;


    @Override
    public void handleOutTimeOrder(@NotNull AppointmentOrderDto dto){
        // 待支付 -> 已取消
        dto.setOrderStatusEnum(UserOrderStatusEnum.CANCELED);

        // 此处只将消息传递给Medicine-service, 是否传递给前端由订单服务自行判断
        try {
            // 设置缓存
            payRedisMapper.updateOrderStatus(
                    dto.getUserId(), dto.getOrderId(),
                    dto.getOrderStatusEnum().getCode(),
                    null
            );

            AppointmentPayResultDto resultDto = appointmentPayConverter.orderToPayResult(
                    dto,
                    LocalDateTime.now()
            );

            // 发送给[订单-预约系统]
            payMqSender.sendAppointmentPayResult(resultDto);
            log.info("purchase通知medicine-service支付预约订单失败, 发送消息通知medicine-service, 消息内容: {}, " +
                    "\n 预约系统即将执行[归还数据库库存, 解除申请分布式锁, netty通知前端]", resultDto);
        } catch (Exception e) {
            log.error("取消订单失败, 消息: {}", dto, e);
        }
        // 此处是订单结果传递回给预约系统, 关于: [归还数据库库存, 解除申请分布式锁, netty通知前端] 都由预约系统处理, 订单系统无关
    }
}
