package com.czy.purchase.service.impl;

import com.api.mapper.purchase.redis.PayRedisMapper;
import com.czy.api.constant.UserOrderStatusEnum;
import com.czy.api.constant.purchase.PayResultEnum;
import com.czy.api.constant.purchase.PurchaseConstant;
import com.czy.api.converter.domain.purchase.AppointmentPayConverter;
import com.czy.api.domain.dto.mq.AppointmentOrderDto;
import com.czy.api.domain.dto.mq.AppointmentPayResultDto;
import com.czy.purchase.mq.PayMqSender;
import com.czy.purchase.service.OrderService;
import com.utils.redisson.service.RedissonClusterLock;
import com.utils.redisson.service.RedissonService;
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

    private final RedissonService redissonService;
    private final PayMqSender payMqSender;
    private final AppointmentPayConverter appointmentPayConverter;
    private final PayRedisMapper payRedisMapper;

    private static final String CREATE_ORDER_LOCK = "createOrder";

    // 创建待支付的订单放入死信队列，设置过期时间; 分布式锁key规则: serviceName + className + methodName
    @Override
    public void createOrder(@NotNull AppointmentOrderDto dto) {
        // redisson订单上锁, 保证订单幂等性
        String mappingPath = PurchaseConstant.serviceName +
                OrderService.class.getName() +
                CREATE_ORDER_LOCK;
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

    @Override
    public void handleOutTimeOrder(@NotNull AppointmentOrderDto dto){
        // redisson订单上锁, 保证订单幂等性
        String mappingPath = PurchaseConstant.serviceName +
                OrderService.class.getName() +
                CREATE_ORDER_LOCK;
        RedissonClusterLock appointmentLock = new RedissonClusterLock(
                dto.getDoctorMerchantAppointmentId().toString(),
                mappingPath
        );

        // 待支付 -> 已取消
        dto.setOrderStatusEnum(UserOrderStatusEnum.CANCELED);

        // 此处只将消息传递给Medicine-service, 是否传递给前端由订单服务自行判断
        try {
            AppointmentPayResultDto resultDto = appointmentPayConverter.orderToPayResult(
                    dto,
                    LocalDateTime.now()
            );
            payMqSender.sendAppointmentPayResult(resultDto);
            log.info("purchase通知medicine-service支付预约订单失败, 发送消息通知medicine-service, 消息内容: {}", resultDto);
        } catch (Exception e) {
            log.error("取消订单失败", e);
        } finally {
            // 处理超时未支付订单无论成功还是失败都需要将订单分布式锁解除
            // 看看解除分布式锁是放在medicine-service ?
            redissonService.unlock(appointmentLock);
        }
    }

    @NotNull
    public Integer payAppointmentOrder(Long userId, Long orderId){
        // 1. 上游消息队列缓存

        // 2. 执行事务: 2.1 扣减用户余额 (成功: 通知, 删除rabbitmq中的延迟消息; 失败: 归还库存)
        // 成功之后标记, 死信队列中的消息被处理的时候就不会回调handleOutTimeOrder
        payRedisMapper.saveOrderWaitPayStatus(orderId);

        // 3. mq通知medicine服务: 状态更新
        return PayResultEnum.NULL.getCode();
    }
}
