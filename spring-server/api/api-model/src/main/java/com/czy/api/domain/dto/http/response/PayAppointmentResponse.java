package com.czy.api.domain.dto.http.response;

import lombok.Data;

/**
 * @author 13225
 * @date 2025/8/26 16:09
 */
@Data
public class PayAppointmentResponse {
    private Long orderId;
    /**
     * 支付状态
     * @see com.czy.api.constant.purchase.PayResultEnum
     */
    private Integer payResult;
}
