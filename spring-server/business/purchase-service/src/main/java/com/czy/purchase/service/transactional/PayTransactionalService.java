package com.czy.purchase.service.transactional;

import exception.AppException;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author 13225
 * @date 2025/8/27 11:38
 */
public interface PayTransactionalService {
    @Transactional(rollbackFor = Exception.class)
    void payAppointmentOrder(long userI, long orderId) throws AppException;
}
