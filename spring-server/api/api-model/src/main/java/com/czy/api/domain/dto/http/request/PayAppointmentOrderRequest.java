package com.czy.api.domain.dto.http.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author 13225
 * @date 2025/8/26 16:07
 */
@Data
public class PayAppointmentOrderRequest {
    @NotNull(message = "用户id不能为空")
    private Long userId;
    @NotNull(message = "订单id不能为空")
    private Long orderId;
}
