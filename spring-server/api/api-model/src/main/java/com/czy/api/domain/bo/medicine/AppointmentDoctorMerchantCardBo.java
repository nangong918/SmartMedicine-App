package com.czy.api.domain.bo.medicine;

import lombok.Data;

import java.time.LocalDateTime;

/**
  SELECT   dt.avatarFileId as doctorAvatarFileId,
           dt.name as doctorName,
           dt.title as doctorTitle,

           ht.name as hospitalName,
           ht.level as hospitalLevel,
           ht.province as locationProvince,
           ht.city as locationCity,
           ht.region as locationRegion,
           ht.longitude as longitude,
           ht.latitude as latitude,

           drat.remainCount as remainCount,
           drat.cost as cost,
           drat.beginDate as beginDate,
           drat.endDate as endDate,
           drat.status as status
   FROM doctor_register_appointment AS drat
   LEFT JOIN doctor AS dt ON drat.doctorId = dt.id
   LEFT JOIN hospital AS ht ON drat.hospitalId = ht.id
   WHERE drat.doctorId in (item.doctorId)
 */
@Data
public class AppointmentDoctorMerchantCardBo {
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

    /// 后续计算填充
    private String distance;

    ///  data
    private Long doctorMerchantId;
}
