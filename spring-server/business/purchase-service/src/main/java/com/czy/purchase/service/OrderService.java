package com.czy.purchase.service;

import com.czy.api.domain.dto.mq.AppointmentOrderDto;
import org.jetbrains.annotations.NotNull;

/**
 * @author 13225
 * @date 2025/8/26 9:35
 */
public interface OrderService {
    // 创建待支付的订单放入死信队列，设置过期时间; 分布式锁key规则: serviceName + className + methodName
    void createOrder(@NotNull AppointmentOrderDto dto);

    void handleOutTimeOrder(@NotNull AppointmentOrderDto dto);
}
