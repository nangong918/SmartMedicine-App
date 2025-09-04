package com.czy.api.domain.dto.mq;

import com.czy.api.constant.UserOrderStatusEnum;
import lombok.Data;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;

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
    /**
     * 创建时间
     * 不使用 LocalDateTime
     * 否则出现RabbitMq反序列化异常:
     * Caused by: com.fasterxml.jackson.databind.exc.InvalidDefinitionException: Cannot construct instance of `java.time.LocalDateTime` (no Creators, like default constructor, exist): cannot deserialize from Object value (no delegate- or property-based Creator)
     */
    private String handleTimeStr;
}
