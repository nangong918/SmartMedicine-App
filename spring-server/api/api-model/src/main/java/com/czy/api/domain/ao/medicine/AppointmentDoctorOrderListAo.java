package com.czy.api.domain.ao.medicine;

import com.czy.api.domain.vo.medicine.AppointmentDoctorOrderListVo;
import date.DateUtils;
import location.GeoUtils;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * @author 13225
 * @date 2025/8/21 11:03
 */
@Data
public class AppointmentDoctorOrderListAo implements Cloneable, Serializable {
    public AppointmentDoctorOrderListVo listVo;
    public Long orderId;
    public Long doctorMerchantId;

    public LocalDateTime getBeginDate(){
        return Optional.ofNullable(this.listVo)
                .map(vo -> vo.beginDate)
                .map(bdateStr -> {
                    try {
                        return DateUtils.getLocalDateTime(bdateStr, DateUtils.yyyyMMddHHmmss);
                    } catch (Exception e) {
                        return LocalDateTime.MIN;
                    }
                })
                .orElse(LocalDateTime.MIN);
    }

    public double getDistance(double userLongitude, double userLatitude){
        double latitude = Optional.ofNullable(listVo)
                .map(vo -> vo.hospitalAo)
                .map(ao -> ao.latitude)
                .orElse(0.0);
        double longitude = Optional.ofNullable(listVo)
                .map(vo -> vo.hospitalAo)
                .map(ao -> ao.longitude)
                .orElse(0.0);

        return GeoUtils.calculateDistance(
                userLatitude, userLongitude,
                latitude,
                longitude
        );
    }

    public BigDecimal getCost(){
        try {
            return Optional.ofNullable(listVo)
                    .map(vo -> vo.cost)
                    .map(BigDecimal::new)
                    .orElse(null);
        } catch (Exception e){
            return null;
        }
    }

    @Override
    public AppointmentDoctorOrderListAo clone() throws CloneNotSupportedException{
        AppointmentDoctorOrderListAo ao = (AppointmentDoctorOrderListAo) super.clone();
        ao.listVo = listVo.clone();
        return ao;
    }
}
