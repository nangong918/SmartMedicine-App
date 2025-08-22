package com.czy.medicine.service.transactional.impl;

import com.api.mapper.medicine.mybatis.DoctorMerchantAppointmentMapper;
import com.api.mapper.medicine.mybatis.UserCustomerAppointmentOrderMapper;
import com.api.mapper.medicine.redis.RegisterAppointmentRedisMapper;
import com.czy.api.constant.UserOrderStatusEnum;
import com.czy.api.constant.medicine.AppointmentMerchantStatusEnum;
import com.czy.api.constant.medicine.MedicineConstant;
import com.czy.api.domain.Do.medicine.DoctorMerchantAppointmentDo;
import com.czy.api.domain.Do.medicine.UserCustomerAppointmentDo;
import com.czy.api.exception.CommonExceptions;
import com.czy.api.exception.MedicineExceptions;
import com.czy.api.exception.PurchaseExceptions;
import com.czy.medicine.service.transactional.AppointmentTransactionalService;
import com.czy.medicine.utils.AppointmentMerchantStatusCalculator;
import com.czy.medicine.utils.UserOrderStatusUtils;
import exception.AppException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
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
    private final RegisterAppointmentRedisMapper registerAppointmentRedisMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void createAppointmentOrder(long orderId, long doctorMerchantId, long userId) throws AppException {
        /// 1.检查是否存在商户 + 锁行（确保后续操作在同一个事务中）
        DoctorMerchantAppointmentDo doctorRegisterAppointmentDo = doctorMerchantAppointmentMapper.getByIdForUpdate(doctorMerchantId);
        if (doctorRegisterAppointmentDo == null || doctorRegisterAppointmentDo.getId() == null){
            log.warn("预约医生商户{} 不存在", doctorMerchantId);
            throw new AppException(MedicineExceptions.DOCTOR_MERCHANT_NOT_EXIST);
        }

        /// 2.检查当前状态是否是可预约
        // 当前时间；在此处获取，因为业务可能呗等待，入参的时间应该是错误的
        LocalDateTime now = LocalDateTime.now();
        long timestamp = now.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        AppointmentMerchantStatusEnum status = AppointmentMerchantStatusCalculator.calculate(
                doctorRegisterAppointmentDo.getRemainCount(),
                now,
                MedicineConstant.APPOINTMENT_OPEN_DAYS,
                doctorRegisterAppointmentDo.getBeginDate(),
                doctorRegisterAppointmentDo.getEndDate()
        );

        // 异常
        switch (status){
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

        /// 2.检查是否存在订单, 顺便查询订单状态
        List<UserCustomerAppointmentDo> orderDos = userCustomerAppointmentOrderMapper.getDosByUserIdAndMerchantId(
                userId, doctorMerchantId
        );

        // 不可预约申请
        if (!UserOrderStatusUtils.checkAppointment(orderDos)){
            // 同商户已存在订单, 暂不可申请
            log.warn("[用户{}]在同[商户{}]已存在订单, 暂不可申请", userId, doctorMerchantId);
            throw new AppException(PurchaseExceptions.EXIST_ORDER_LOCK);
        }

        /// 3.减少库存 (事务中)
        int updatedRows = doctorMerchantAppointmentMapper.decrementWithPessimisticLock(doctorMerchantId);
        if (updatedRows == 0) {
            // 理论上不会走到这里，因为前面已经检查过了
            log.warn("[商户{}]库存减少失败", doctorMerchantId);
            throw new AppException(MedicineExceptions.NO_AVAILABLE_MERCHANT);
        }

        /// 4.创建订单并插入数据库
        UserCustomerAppointmentDo userCustomerAppointmentDo = new UserCustomerAppointmentDo();
        userCustomerAppointmentDo.setId(orderId);
        userCustomerAppointmentDo.setDoctorMerchantAppointmentId(doctorMerchantId);
        userCustomerAppointmentDo.setUserId(userId);
        userCustomerAppointmentDo.setRecordTimestamp(timestamp);
        // 设置为审核中
        userCustomerAppointmentDo.setUserOrderStatus(UserOrderStatusEnum.WAITING_AUDIT.getCode());

        int insertResult = userCustomerAppointmentOrderMapper.insert(userCustomerAppointmentDo);
        if (insertResult <= 0){
            throw new AppException(CommonExceptions.SYSTEM_SQL_ERROR);
        }

        /// 5.删除/更新redis缓存
        // redis的库存扣减
        DoctorMerchantAppointmentDo doctorMerchantAppointmentDo = doctorMerchantAppointmentMapper.getById(doctorMerchantId);
        if (!registerAppointmentRedisMapper.saveDoctorMerchantAppointmentDo(doctorMerchantAppointmentDo)){
            // 缓存更新失败的话不需要回滚事务, 直接删除缓存就好, 等待之后查询发现没缓存就来查询数据了
            log.warn("缓存更新失败, 删除缓存");
            boolean result = registerAppointmentRedisMapper.deleteDoctorMerchantAppointmentDo(doctorMerchantId);
            if (!result){
                // 如果删除都删除失败了, 就要用error日志, 去排查问题, 估计是redis挂了
                log.error("删除缓存也删除失败了失败");
            }
        }
    }

}
