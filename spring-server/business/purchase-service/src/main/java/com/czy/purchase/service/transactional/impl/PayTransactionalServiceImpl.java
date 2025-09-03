package com.czy.purchase.service.transactional.impl;

import com.api.mapper.purchase.mybatis.UserWalletMapper;
import com.api.mapper.purchase.redis.PayRedisMapper;
import com.czy.api.constant.UserOrderStatusEnum;
import com.czy.api.constant.purchase.PayResultEnum;
import com.czy.api.domain.Do.purchase.UserWalletDo;
import com.czy.api.domain.ao.purchase.OrderStatusAo;
import com.czy.api.exception.PurchaseExceptions;
import com.czy.purchase.service.transactional.PayTransactionalService;
import exception.AppException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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
    // 取消使用预约系统的mapper，支付系统不应该持有订单系统的mapper，数据耦合，应该从payRedisMapper获取
//    private final AppointmentOrderStatusBoMapper appointmentOrderStatusBoMapper;
    private final UserWalletMapper userWalletMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public PayResultEnum payAppointmentOrder(long userId, long orderId) throws AppException {
        log.info("[Appointment订单支付事务开始][user: {}][order: {}]", userId, orderId);

        /// 1. 获取订单待支付金额, 订单状态检查 (error1: 已下架; error2: 订单过期)
        // 需要的bo数据： （订单id， 商户id，userId，user订单状态，商户的定价金额，预约的开始时间）

        OrderStatusAo orderStatusAo = payRedisMapper.getOrderStatus(userId, orderId);
        if (orderStatusAo == null || orderStatusAo.getCustomerStatus() == null){
            throw new AppException(PurchaseExceptions.ORDER_EXPIRED);
        }
        // 1.1 检查订单是否过期 [乐观锁1]
        if (orderStatusAo.getMerchantEndTime() == null || orderStatusAo.getMerchantEndTime().isBefore(LocalDateTime.now())){
            // error1: 已下架
            throw new AppException(PurchaseExceptions.ORDER_OUT_OF_SELLING_TIME);
        }
        // 1.2 超时检查
        if (orderStatusAo.getCustomerStatus() == UserOrderStatusEnum.CANCELED.getCode()){
            // 抛出订单超时异常, 事务回滚 error2: 订单过期
            log.warn("[订单支付]预约商家已开始不可支付, 商家结束时间： {}", orderStatusAo.getMerchantEndTime());
            throw new AppException(PurchaseExceptions.ORDER_TIMEOUT);
        }

        /// 2. 检查订单
        // 2.1 价格不存在 || 价格小于0 (可以等于0, 属于特殊优惠)
        if (orderStatusAo.getTotalPrice() == null || orderStatusAo.getTotalPrice().compareTo(BigDecimal.ZERO) < 0){
            throw new AppException(PurchaseExceptions.ORDER_PRICE_ERROR);
        }
        // 2.2 待支付检查
        if (orderStatusAo.getCustomerStatus() != UserOrderStatusEnum.WAITING_PAYMENT.getCode()){
            // 订单状态错误
            log.warn("订单状态错误: {}", orderStatusAo.getCustomerStatus());
            throw new AppException(PurchaseExceptions.ORDER_NOT_WAIT_PAY);
        }

        /// 3.锁行检查用户的金额 + 扣减制定金额
        UserWalletDo userWalletDo = userWalletMapper.getUserWalletAndLockByUserId(userId);
        // 钱包都没有
        if (userWalletDo == null || userWalletDo.getId() == null){
            return PayResultEnum.INSUFFICIENT_BALANCE;
        }
        // 钱不够
        if (userWalletDo.getBalance().compareTo(orderStatusAo.getTotalPrice()) < 0){
            return PayResultEnum.INSUFFICIENT_BALANCE;
        }

        /// 4.状态检查:
        // 4.1 再次检查是否订单超时 [乐观锁2]
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

        // 4.2成功: 更新订单状态
        // 成功之后标记, 死信队列中的消息被处理的时候就不会回调handleOutTimeOrder
        payRedisMapper.updateOrderStatus(
                userId, orderId,
                UserOrderStatusEnum.WAITING_USE.getCode(),
                null
        );

        /// 5.缓存更新
        // 订单状态缓存：在medicine-service中更新

        return PayResultEnum.SUCCESS;
    }
}
