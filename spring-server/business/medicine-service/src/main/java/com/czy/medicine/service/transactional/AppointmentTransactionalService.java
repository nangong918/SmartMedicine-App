package com.czy.medicine.service.transactional;

import exception.AppException;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author 13225
 * @date 2025/8/22 13:59
 */
public interface AppointmentTransactionalService {
    @Transactional(rollbackFor = Exception.class)
    void createAppointmentOrder(long orderId, long doctorMerchantId, long userId,
                                long recordTimestamp) throws AppException;
}
