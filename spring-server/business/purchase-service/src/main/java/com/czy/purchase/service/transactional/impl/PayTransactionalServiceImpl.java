package com.czy.purchase.service.transactional.impl;

import com.api.mapper.medicine.mybatis.bo.AppointmentOrderStatusBoMapper;
import com.api.mapper.purchase.redis.PayRedisMapper;
import com.czy.api.domain.bo.medicine.AppointmentOrderStatusBo;
import com.czy.purchase.service.transactional.PayTransactionalService;
import exception.AppException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author 13225
 * @date 2025/8/27 11:39
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class PayTransactionalServiceImpl implements PayTransactionalService {

    private final PayRedisMapper payRedisMapper;
    private final AppointmentOrderStatusBoMapper appointmentOrderStatusBoMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void payAppointmentOrder(long userI, long orderId) throws AppException {
        log.info("[Appointment订单支付事务开始][user: {}][order: {}]", userI, orderId);

        /// 1.获取订单待支付金额, 订单状态检查 (error1: 已下架; error2: 订单过期)
        // 需要的bo数据： （订单id， 商户id，userId，user订单状态，商户的定价金额，预约的开始时间）
        AppointmentOrderStatusBo orderStatusBo = appointmentOrderStatusBoMapper.fetchAndLockBoByOrderId(orderId);


        /// 2.锁行检查用户的金额 + 扣减制定金额

        /// 3.状态检查:
        // 3.1成功: 更新订单状态
        // 成功之后标记, 死信队列中的消息被处理的时候就不会回调handleOutTimeOrder
        payRedisMapper.saveOrderWaitPayStatus(orderId);

        // 3.2失败: error1: 余额不足; error2: 支付受限; 更新订单状态

        /// 4.缓存更新
    }
}
