package com.czy.api.domain.Do.medicine;

import com.czy.api.constant.UserOrderStatusEnum;
import json.BaseBean;
import lombok.Data;
import org.springframework.data.annotation.Id;

/**
 * @author 13225
 * @date 2025/8/20 14:54
 */
@Data
public class UserCustomerAppointmentDo implements BaseBean {
    // 订单id
    @Id
    private Long id;
    // doctor商户id
    private Long doctorMerchantAppointmentId;
    // 用户id
    private Long userId;
    // 记录时间
    private Long recordTimestamp;
    // 订单状态
    private Integer userOrderStatus = UserOrderStatusEnum.NOT_ORDERED.getCode();
}
