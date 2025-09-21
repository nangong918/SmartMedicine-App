package com.czy.domain.dto.http.response;

/**
 * @author 13225
 * @date 2025/8/26 16:09
 */
public class PayAppointmentResponse {
    private Long orderId;
    /**
     * 支付状态
     * @see com.czy.domain.constant.purchase.PayResultEnum
     */
    public Integer payResult;
}
