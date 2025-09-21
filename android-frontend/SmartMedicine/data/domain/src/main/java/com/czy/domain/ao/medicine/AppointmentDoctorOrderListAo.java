package com.czy.domain.ao.medicine;


import androidx.annotation.NonNull;

import com.czy.domain.vo.entity.medicine.AppointmentDoctorOrderListVo;

import java.io.Serializable;

/**
 * @author 13225
 * @date 2025/8/21 11:03
 */
public class AppointmentDoctorOrderListAo implements Cloneable, Serializable {
    public AppointmentDoctorOrderListVo listVo;
    public Long orderId;
    public Long doctorMerchantId;

    @NonNull
    @Override
    public AppointmentDoctorOrderListAo clone() throws CloneNotSupportedException{
        AppointmentDoctorOrderListAo ao = (AppointmentDoctorOrderListAo) super.clone();
        if (listVo != null){
            ao.listVo = listVo.clone();
        }
        return ao;
    }
}
