package com.czy.api.domain.dto.mq;

import com.czy.api.constant.UserOrderStatusEnum;
import com.czy.api.constant.medicine.MedicineRedisKey;
import lombok.Data;

import java.io.Serializable;

/**
 * @author 13225
 * @date 2025/8/25 17:49
 */
@Data
public class AppointmentOrderDto implements Serializable {
    private Long doctorMerchantAppointmentId;
    private Long userId;
    private Long orderId;
    private UserOrderStatusEnum orderStatusEnum;
    // 有效时长
    private Long effectiveTime = MedicineRedisKey.Appointment.appointmentOrder_EXPIRE_TIME;

    /// 日志
    private Long currentTime = System.currentTimeMillis();
}
