package com.czy.api.domain.bo.medicine;

import com.czy.api.constant.UserOrderStatusEnum;
import com.czy.api.constant.medicine.AppointmentMerchantStatusEnum;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author 13225
 * @date 2025/8/25 13:47
 */
@Data
public class UserAppointmentOrderBo {
    // Doctor
    private Long doctorAvatarFileId;
    private String doctorName;
    private String doctorTitle;
    // Hospital
    private String hospitalName;
    private String hospitalLevel;
    // location
    private String locationProvince;
    private String locationCity;
    private String locationRegion;
    // 经纬度
    private Double longitude;
    private Double latitude;
    // 剩余数量
    private Integer remainCount;
    // 费用
    private String cost;
    // 预约时间区间
    private LocalDateTime beginDate;
    private LocalDateTime endDate;

    /// data
    // 商户
    private Long doctorMerchantId;
    // orderId
    private Long orderId;
    // user订单状态
    private Integer customerStatus = UserOrderStatusEnum.NOT_ORDERED.getCode();
    // 商户状态需要计算:需要计算
    private Integer merchantStatus = AppointmentMerchantStatusEnum.NO_AVAILABLE.getCode();
}
