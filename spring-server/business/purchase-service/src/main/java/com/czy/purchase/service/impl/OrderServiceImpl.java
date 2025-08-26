package com.czy.purchase.service.impl;

import com.czy.api.constant.UserOrderStatusEnum;
import com.czy.api.constant.purchase.PurchaseConstant;
import com.czy.api.domain.dto.mq.AppointmentOrderDto;
import com.czy.purchase.mq.PayMqSender;
import com.czy.purchase.service.OrderService;
import com.utils.redisson.service.RedissonClusterLock;
import com.utils.redisson.service.RedissonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

/**
 * @author 13225
 * @date 2025/8/26 9:36
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {

    private final RedissonService redissonService;
    private final PayMqSender payMqSender;

    // 创建待支付的订单放入死信队列，设置过期时间; 分布式锁key规则: serviceName + className + methodName
    @Override
    public void createOrder(@NotNull AppointmentOrderDto dto) {
        // redisson订单上锁, 保证订单幂等性
        String mappingPath = PurchaseConstant.serviceName +
                OrderService.class.getName() +
                "createOrder";
        RedissonClusterLock appointmentLock = new RedissonClusterLock(
                dto.getDoctorMerchantAppointmentId().toString(),
                mappingPath,
                PurchaseConstant.MAX_WAIT_PAY_TIMEOUT
        );

        if (!redissonService.tryLock(appointmentLock)) {
            log.warn("[重复生成订单warn][预约挂号驳回][获取分布式锁失败][user: {}][商户: {}]", dto.getUserId(), dto.getDoctorMerchantAppointmentId());
            return;
        }

        // 状态改为待支付
        dto.setOrderStatusEnum(UserOrderStatusEnum.WAITING_PAYMENT);
        // 创建待支付订单加入到延迟队列
        payMqSender.sendCreateOrderMessage(dto);
    }

}
