package com.czy.medicine.service.transactional.impl;

import com.api.mapper.medicine.mybatis.DoctorMerchantAppointmentMapper;
import com.api.mapper.medicine.mybatis.UserCustomerAppointmentOrderMapper;
import com.api.mapper.medicine.mybatis.bo.DoctorMerchantBoMapper;
import com.api.mapper.medicine.redis.AppointmentDoctorOrderRedisMapper;
import com.api.mapper.purchase.redis.PayRedisMapper;
import com.czy.api.constant.UserOrderStatusEnum;
import com.czy.api.constant.medicine.AppointmentMerchantStatusEnum;
import com.czy.api.constant.medicine.MedicineConstant;
import com.czy.api.converter.domain.medicine.AppointmentDoctorOrderConverter;
import com.czy.api.domain.Do.medicine.DoctorMerchantAppointmentDo;
import com.czy.api.domain.Do.medicine.UserCustomerAppointmentDo;
import com.czy.api.domain.ao.medicine.AppointmentDoctorOrderListAo;
import com.czy.api.domain.ao.purchase.OrderStatusAo;
import com.czy.api.domain.bo.medicine.UserAppointmentOrderBo;
import com.czy.api.exception.CommonExceptions;
import com.czy.api.exception.MedicineExceptions;
import com.czy.api.exception.PurchaseExceptions;
import com.czy.medicine.service.transactional.AppointmentTransactionalService;
import com.czy.medicine.utils.AppointmentMerchantStatusCalculator;
import com.czy.medicine.utils.UserOrderStatusUtils;
import date.DateUtils;
import exception.AppException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 13225
 * @date 2025/8/22 14:00
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class AppointmentTransactionalServiceImpl implements AppointmentTransactionalService {

    private final DoctorMerchantAppointmentMapper doctorMerchantAppointmentMapper;
    private final UserCustomerAppointmentOrderMapper userCustomerAppointmentOrderMapper;
    private final AppointmentDoctorOrderRedisMapper appointmentDoctorOrderRedisMapper;
    private final DoctorMerchantBoMapper doctorMerchantBoMapper;
    private final AppointmentDoctorOrderConverter appointmentDoctorOrderConverter;
    private final PayRedisMapper payRedisMapper;

    // todo 升级方向: 取消数控直接扣减, 而是成功之后更新redis的信号量
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void createAppointmentOrder(long orderId, long doctorMerchantId, long userId) throws AppException {
        /// 1.检查是否存在商户 + 锁行（确保后续操作在同一个事务中）
        log.info("[审核订单][检查是否存在商户 + 锁行]");
        DoctorMerchantAppointmentDo doctorRegisterAppointmentDo = doctorMerchantAppointmentMapper.getByIdForUpdate(doctorMerchantId);
        if (doctorRegisterAppointmentDo == null || doctorRegisterAppointmentDo.getId() == null){
            log.warn("[审核订单]预约医生商户{} 不存在", doctorMerchantId);
            throw new AppException(MedicineExceptions.DOCTOR_MERCHANT_NOT_EXIST);
        }

        /// 2.检查当前状态是否是可预约
        // 当前时间；在此处获取，因为业务可能呗等待，入参的时间应该是错误的
        LocalDateTime now = LocalDateTime.now();
        long timestamp = now.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        AppointmentMerchantStatusEnum merchantStatus = AppointmentMerchantStatusCalculator.calculate(
                doctorRegisterAppointmentDo.getRemainCount(),
                now,
                MedicineConstant.APPOINTMENT_OPEN_DAYS,
                doctorRegisterAppointmentDo.getBeginDate(),
                doctorRegisterAppointmentDo.getEndDate()
        );
        log.info("[审核订单]检查当前状态是否是可预约: {}, [参数 RemainCount: {}, BeginDate: {}, EndDate: {}]",
                merchantStatus, doctorRegisterAppointmentDo.getRemainCount(),
                doctorRegisterAppointmentDo.getBeginDate(), doctorRegisterAppointmentDo.getEndDate());

        // 异常
        switch (merchantStatus){
            case AVAILABLE:
                break;
            case EXPIRED:
                throw new AppException(MedicineExceptions.MERCHANT_INFO_EXPIRED);
            case NO_AVAILABLE:
                log.warn("[商户{}]已无剩余可预约, 暂不可申请", doctorMerchantId);
                throw new AppException(MedicineExceptions.NO_AVAILABLE_MERCHANT);
            case WAITING_OPEN:
                throw new AppException(MedicineExceptions.WAITING_OPEN);
        }

        /// 3.检查是否存在订单, 顺便查询订单状态
        log.info("[审核预约][检查是否存在订单]");
        List<UserCustomerAppointmentDo> orderDos = userCustomerAppointmentOrderMapper.getDosByUserIdAndMerchantId(
                userId, doctorMerchantId
        );

        // 不可预约申请
        if (!UserOrderStatusUtils.checkAppointment(orderDos)){
            // 同商户已存在订单, 暂不可申请
            log.warn("[用户{}]在同[商户{}]已存在订单, 暂不可申请", userId, doctorMerchantId);
            throw new AppException(PurchaseExceptions.EXIST_ORDER_LOCK);
        }

        /// 4.减少库存 (事务中)
        log.info("[审核预约][尝试扣减库存][商户{}]", doctorMerchantId);
        int updatedRows = doctorMerchantAppointmentMapper.decrementWithPessimisticLock(doctorMerchantId);
        if (updatedRows == 0) {
            // 理论上不会走到这里，因为前面已经检查过了
            log.warn("[审核预约][商户{}]库存减少失败", doctorMerchantId);
            throw new AppException(MedicineExceptions.NO_AVAILABLE_MERCHANT);
        }
        else {
            log.info("[审核预约][商户{}]库存减少成功", doctorMerchantId);
        }

        /// 5.创建订单并插入数据库
        UserCustomerAppointmentDo userCustomerAppointmentDo = new UserCustomerAppointmentDo();
        userCustomerAppointmentDo.setId(orderId);
        userCustomerAppointmentDo.setDoctorMerchantAppointmentId(doctorMerchantId);
        userCustomerAppointmentDo.setUserId(userId);
        userCustomerAppointmentDo.setRecordTimestamp(timestamp);
        // 设置为审核中
        userCustomerAppointmentDo.setUserOrderStatus(UserOrderStatusEnum.WAITING_AUDIT.getCode());

        log.info("[审核预约][审核成功, 开始将订单插入数据库][创建用户预约此商户的订单]");
        int insertResult = userCustomerAppointmentOrderMapper.insert(userCustomerAppointmentDo);
        if (insertResult <= 0){
            log.warn("[审核预约][插入数据库失败][商户: {}][用户: {}][订单: {}]",
                    doctorMerchantId, userId, orderId);
            throw new AppException(CommonExceptions.SYSTEM_SQL_ERROR);
        }

        /// 6.异步更新缓存
        uploadRedis(userCustomerAppointmentDo,
                userId, orderId, merchantStatus,
                doctorRegisterAppointmentDo
        );
    }


    /**
     * 异步更新缓存:
     * 1. AppointmentDoctorOrderListAo 用户订单状态
     * 2. 创建支付缓存
     * @param userCustomerAppointmentDo     订单do
     * @param userId                        用户id
     * @param orderId                       订单id
     * @param merchantStatus                商户状态
     * @param doctorRegisterAppointmentDo   商户do
     */
    @Async
    public void uploadRedis(UserCustomerAppointmentDo userCustomerAppointmentDo, Long userId, Long orderId,
                            AppointmentMerchantStatusEnum merchantStatus, DoctorMerchantAppointmentDo doctorRegisterAppointmentDo){
        log.info("[审核预约成功][异步更新缓存]");
        // 订单插入userOrderList
        List<UserCustomerAppointmentDo> userOrderList = new ArrayList<>();
        userOrderList.add(userCustomerAppointmentDo);
        List<UserAppointmentOrderBo> bos = doctorMerchantBoMapper.getDoctorCardBosByUserCustomerAppointmentDos(userOrderList);
        if (CollectionUtils.isEmpty(bos)){
            log.warn("[审核预约成功][异步更新缓存] 缓存用户预约order失败: bo为空");
            return;
        }

        log.info("[审核预约成功][异步更新缓存] 开始数据计算填充: MerchantStatus");
        // 数据计算填充: MerchantStatus
        AppointmentMerchantStatusCalculator.calculateFillUserAppointmentOrderBos(
                bos
        );

        LocalDateTime registerDate = LocalDateTime.now();
        String dateStr = DateUtils.yyyyMMddHHmmssToString(registerDate);
        /// 6.1 订单系统的查询view视图更新 ; 更新之前的缓存: 之前的缓存数据不完善, 现在更新之前的缓存  AppointmentDoctorOrderListAo 用户订单状态
        AppointmentDoctorOrderListAo ao = appointmentDoctorOrderConverter.getAoByBo(bos.get(0), dateStr);
        log.info("[审核预约成功][异步更新缓存] 开始更新 用户订单状态 的缓存, [AppointmentDoctorOrderListAo: {}]", ao);
        boolean orderViewResult = appointmentDoctorOrderRedisMapper.updateSingleAppointmentDoctorOrderListAo(
                userId,
                ao
        );
        if (!orderViewResult){
            log.warn("缓存用户预约orderViewResult失败, redis存储失败");
        }
        /// 6.2 支付系统的状态创建/更新  创建支付缓存
        log.info("[审核预约成功][异步更新缓存] 开始创建 待支付订单 的缓存");
        payRedisMapper.updateOrderStatus(
                userId,
                orderId,
                UserOrderStatusEnum.WAITING_PAYMENT.getCode(),
                merchantStatus.getCode(),
                // 结束时间: 预约开始时间
                doctorRegisterAppointmentDo.getBeginDate(),
                doctorRegisterAppointmentDo.getCost()
        );
        // 检查
        OrderStatusAo orderStatusAo = payRedisMapper.getOrderStatus(userId, orderId);
        log.info("创建订单成功, 订单状态为: {}", orderStatusAo);
    }

}
