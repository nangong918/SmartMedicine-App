package com.czy.purchase.service.impl;

import com.czy.api.constant.purchase.PayResultEnum;
import com.czy.purchase.mq.PayMqSender;
import com.czy.purchase.service.PayService;
import com.czy.purchase.service.transactional.PayTransactionalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

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

    // todo 事务处理 + 回调medicine + 测试
    @NotNull
    public Integer payAppointmentOrder(Long userId, Long orderId){
        // 1. 执行事务: 1.1 扣减用户余额 (成功: 通知, 不删除rabbitmq中的延迟消息,等待过期[FIFO无法删除]; 失败: 不归还库存, 等待用户取消或者订单过期)
        payTransactionalService.payAppointmentOrder(userId, orderId);

        // 2. mq通知medicine服务: 状态更新
        return PayResultEnum.NULL.getCode();
    }
}
