package com.czy.purchase.service.impl;

import com.czy.api.constant.UserOrderStatusEnum;
import com.czy.api.constant.purchase.PayResultEnum;
import com.czy.api.constant.purchase.RechargeEnum;
import com.czy.api.domain.dto.http.response.RechargeMoneyResponse;
import com.czy.api.domain.dto.mq.AppointmentPayResultDto;
import com.czy.purchase.mq.PayMqSender;
import com.czy.purchase.service.PayService;
import com.czy.purchase.service.transactional.PayTransactionalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * @author 13225
 * @date 2025/8/27 11:33
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class PayServiceImpl implements PayService {

    private final PayMqSender payMqSender;
    private final PayTransactionalService payTransactionalService;

    // 事务处理 + 回调medicine + 测试
    @Override
    public int payAppointmentOrder(Long userId, Long orderId){
        // 1. 执行事务: 1.1 扣减用户余额 (成功: 通知, 不删除rabbitmq中的延迟消息,等待过期[FIFO无法删除]; 失败: 不归还库存, 等待用户取消或者订单过期)
        PayResultEnum payResultEnum = payTransactionalService.payAppointmentOrder(userId, orderId);

        if (payResultEnum == PayResultEnum.SUCCESS){
            // 1.1.1 扣减成功: 订单支付成功, 通知medicine服务
            // 1.1.2 扣减失败: 订单支付失败, 不做操作rabbitmq中的延迟消息自动执行删除
            AppointmentPayResultDto dto = new AppointmentPayResultDto();
            dto.setUserId(userId);
            dto.setOrderId(orderId);
            // 成功: 待支付 -> 待使用
            dto.setOrderStatusEnum(UserOrderStatusEnum.WAITING_USE);
            dto.setHandleTime(LocalDateTime.now());
            // 将消息发送给medicine-service
            payMqSender.sendAppointmentPayResult(dto);
        }

        // 2. mq通知medicine服务: 状态更新
        return payResultEnum.getCode();
    }

    @NotNull
    @Override
    public RechargeMoneyResponse testRecharge(@NotNull Long userId, @NotNull RechargeEnum rechargeEnum){
        // todo
        return null;
    }
}
