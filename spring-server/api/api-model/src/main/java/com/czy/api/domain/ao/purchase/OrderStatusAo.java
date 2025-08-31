package com.czy.api.domain.ao.purchase;

import lombok.Data;

/**
 * @author 13225
 * @date 2025/8/30 15:37
 */
@Data
public class OrderStatusAo {
    private Long userId;
    private Long orderId;
    /**
     * 订单状态
     * @see com.czy.api.constant.UserOrderStatusEnum
     */
    private Integer customerStatus;
    /**
     * 商户状态
     * @see com.czy.api.constant.medicine.AppointmentMerchantStatusEnum
     */
    private Integer merchantStatus;
}
