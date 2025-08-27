package com.czy.purchase.service.transactional;

import com.czy.api.constant.purchase.PayResultEnum;
import exception.AppException;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author 13225
 * @date 2025/8/27 11:38
 */
public interface PayTransactionalService {
    @Transactional(rollbackFor = Exception.class)
    PayResultEnum payAppointmentOrder(long userId, long orderId) throws AppException;
}
