package com.czy.purchase.service.transactional.impl;

import com.api.mapper.medicine.mybatis.bo.AppointmentOrderStatusBoMapper;
import com.api.mapper.purchase.mybatis.UserWalletMapper;
import com.api.mapper.purchase.redis.PayRedisMapper;
import com.czy.api.constant.UserOrderStatusEnum;
import com.czy.api.constant.purchase.PayResultEnum;
import com.czy.api.domain.Do.purchase.UserWalletDo;
import com.czy.api.domain.ao.purchase.OrderStatusAo;
import com.czy.api.domain.bo.medicine.AppointmentOrderStatusBo;
import com.czy.api.exception.PurchaseExceptions;
import com.czy.purchase.service.transactional.PayTransactionalService;
import exception.AppException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

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
    private final UserWalletMapper userWalletMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public PayResultEnum payAppointmentOrder(long userId, long orderId) throws AppException {
        log.info("[Appointment订单支付事务开始][user: {}][order: {}]", userId, orderId);

        /// 1.获取订单待支付金额, 订单状态检查 (error1: 已下架; error2: 订单过期)
        // 需要的bo数据： （订单id， 商户id，userId，user订单状态，商户的定价金额，预约的开始时间）
        AppointmentOrderStatusBo orderStatusBo = appointmentOrderStatusBoMapper.fetchAndLockBoByOrderId(orderId);
        if (orderStatusBo == null || orderStatusBo.getOrderId() == null){
            // 订单不存在
            throw new AppException(PurchaseExceptions.ORDER_NOT_EXIST);
        }

        // 检查订单是否过期 [乐观锁1]
        OrderStatusAo orderStatusAo = payRedisMapper.getOrderStatus(userId, orderId);
        if (orderStatusAo == null){
            throw new AppException(PurchaseExceptions.ORDER_EXPIRED);
        }
        if (orderStatusAo.getCustomerStatus() == UserOrderStatusEnum.CANCELED.getCode()){
            // 订单超时异常,
            return PayResultEnum.ORDER_EXPIRED;
        }
        if (orderStatusAo.getCustomerStatus() != UserOrderStatusEnum.WAITING_PAYMENT.getCode()){
            // 订单状态错误
            log.warn("订单状态错误: {}", orderStatusAo.getCustomerStatus());
            throw new AppException(PurchaseExceptions.ORDER_NOT_WAIT_PAY);
        }

        // 检查订单
        // 是否是待支付
        UserOrderStatusEnum orderStatusEnum = UserOrderStatusEnum.getByCode(
                orderStatusBo.getUserOrderStatus()
        );
        if (!UserOrderStatusEnum.WAITING_PAYMENT.equals(orderStatusEnum)){
            log.warn("[订单支付]订单状态错误: {}", orderStatusEnum);
            return PayResultEnum.ORDER_STATUS_ERROR;
        }
        // 预约商家是否已经开始不可支付
        if (LocalDateTime.now().isAfter(orderStatusBo.getBeginDate())){
            log.warn("[订单支付]预约商家已开始不可支付");
            return PayResultEnum.LIMITED;
        }

        /// 2.锁行检查用户的金额 + 扣减制定金额
        UserWalletDo userWalletDo = userWalletMapper.getUserWalletAndLockByUserId(userId);
        // 钱包都没有
        if (userWalletDo == null || userWalletDo.getId() == null){
            return PayResultEnum.INSUFFICIENT_BALANCE;
        }
        // 钱不够
        if (userWalletDo.getBalance().compareTo(orderStatusBo.getMerchantPrice()) < 0){
            return PayResultEnum.INSUFFICIENT_BALANCE;
        }

        /// 3.状态检查:
        // 3.1 再次检查是否订单超时 [乐观锁2]
        OrderStatusAo orderStatusAo2 = payRedisMapper.getOrderStatus(userId, orderId);
        if (orderStatusAo2 == null){
            throw new AppException(PurchaseExceptions.ORDER_EXPIRED);
        }
        if (orderStatusAo2.getCustomerStatus() == UserOrderStatusEnum.CANCELED.getCode()){
            // 抛出订单超时异常, 事务回滚
            throw new AppException(PurchaseExceptions.ORDER_TIMEOUT);
        }
        if (orderStatusAo2.getCustomerStatus() != UserOrderStatusEnum.WAITING_PAYMENT.getCode()){
            // 订单状态错误
            log.warn("订单状态错误: {}", orderStatusAo.getCustomerStatus());
            throw new AppException(PurchaseExceptions.ORDER_NOT_WAIT_PAY);
        }

        // 3.2成功: 更新订单状态
        // 成功之后标记, 死信队列中的消息被处理的时候就不会回调handleOutTimeOrder
        payRedisMapper.updateOrderStatus(
                userId, orderId,
                UserOrderStatusEnum.WAITING_USE.getCode(),
                null
        );

        /// 4.缓存更新
        // 订单状态缓存：在medicine-service中更新

        return PayResultEnum.SUCCESS;
    }
}
