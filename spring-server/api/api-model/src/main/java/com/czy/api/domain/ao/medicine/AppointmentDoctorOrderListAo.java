package com.czy.api.domain.ao.medicine;

import com.czy.api.domain.vo.medicine.AppointmentDoctorOrderListVo;
import lombok.Data;

/**
 * @author 13225
 * @date 2025/8/21 11:03
 */
@Data
public class AppointmentDoctorOrderListAo {
    public AppointmentDoctorOrderListVo listVo;
    public Long orderId;
    public Long doctorMerchantId;
}
