package com.czy.medicine.service.transactional.impl;

import com.api.mapper.medicine.mybatis.DoctorMerchantAppointmentMapper;
import com.api.mapper.medicine.mybatis.UserCustomerAppointmentOrderMapper;
import com.api.mapper.medicine.redis.RegisterAppointmentRedisMapper;
import com.czy.api.domain.Do.medicine.DoctorMerchantAppointmentDo;
import com.czy.api.domain.Do.medicine.UserCustomerAppointmentDo;
import com.czy.api.exception.MedicineExceptions;
import com.czy.api.exception.PurchaseExceptions;
import com.czy.medicine.service.transactional.AppointmentTransactionalService;
import com.czy.medicine.utils.UserOrderStatusUtils;
import exception.AppException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public void createAppointmentOrder(long orderId, long doctorMerchantId, long userId,
                                       long recordTimestamp) throws AppException {
        /// 1.锁行查询剩余数量（确保后续操作在同一个事务中）
        int remainCount = doctorMerchantAppointmentMapper.getRemainCountWithLock(doctorMerchantId);
        if (remainCount <= 0){
            log.warn("[商户{}]已无剩余可预约, 暂不可申请", doctorMerchantId);
            throw new AppException(MedicineExceptions.NO_AVAILABLE_MERCHANT);
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
        userCustomerAppointmentDo.setRecordTimestamp(recordTimestamp);

        userCustomerAppointmentOrderMapper.insert(
                userCustomerAppointmentDo
        );

        /// 5.删除/更新redis缓存
        // redis的库存扣减
        DoctorMerchantAppointmentDo doctorMerchantAppointmentDo = doctorMerchantAppointmentMapper.getById(doctorMerchantId);
        registerAppointmentRedisMapper.saveDoctorMerchantAppointmentDo(doctorMerchantAppointmentDo);
    }

}
