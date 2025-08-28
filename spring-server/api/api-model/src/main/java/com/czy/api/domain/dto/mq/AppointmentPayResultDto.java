package com.czy.api.domain.dto.mq;

import com.czy.api.constant.UserOrderStatusEnum;
import lombok.Data;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author 13225
 * @date 2025/8/26 15:50
 */
@Data
public class AppointmentPayResultDto implements Serializable {
    @Nullable
    private Long doctorMerchantAppointmentId;
    private Long userId;
    private Long orderId;
    private UserOrderStatusEnum orderStatusEnum;
    private LocalDateTime handleTime;
}
