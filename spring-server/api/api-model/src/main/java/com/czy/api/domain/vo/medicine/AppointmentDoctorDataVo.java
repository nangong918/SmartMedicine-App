package com.czy.api.domain.vo.medicine;

import lombok.Data;

/**
 * @author 13225
 * @date 2025/8/18 14:39
 * 预约日期vo
 */
@Data
public class AppointmentDoctorDataVo {
    // 预约时间：yyyy-MM-dd
    public String date;
    // 剩余可预约数量
    public Integer remainCount;
    // 最低费用
    public String minCost;
}
