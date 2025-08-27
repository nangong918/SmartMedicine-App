package com.czy.api.domain.bo.medicine;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author 13225
 * @date 2025/8/27 16:08
 */
@Data
public class AppointmentOrderStatusBo {
    // 订单id
    private Long orderId;
    // 商户id
    private Long doctorMerchantId;
    // userId
    private Long userId;
    // user订单状态
    private Integer userOrderStatus;
    // 商户的定价金额
    private Integer merchantPrice;
    // 预约的开始时间
    private LocalDateTime beginDate;
}
