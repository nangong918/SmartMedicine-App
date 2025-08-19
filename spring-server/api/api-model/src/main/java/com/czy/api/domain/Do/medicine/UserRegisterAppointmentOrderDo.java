package com.czy.api.domain.Do.medicine;

import com.czy.api.constant.medicine.AppointmentStatusEnum;
import lombok.Data;
import org.springframework.data.annotation.Id;

/**
 * @author 13225
 * @date 2025/8/18 15:37
 * user挂号预约订单记录
 */
@Data
public class UserRegisterAppointmentOrderDo {
    @Id
    private Long id;
    // userId
    private Long userId;
    // 预约记录id
    private Long registerAppointmentRecordId;
    // 医生id：预约记录表获取
    // 医院id：预约记录表获取

    // 预约时间戳
    private Long timestamp;

    /**
     * 预约状态
     * @see AppointmentStatusEnum
     */
    private Integer status = AppointmentStatusEnum.AVAILABLE.getCode();
}
