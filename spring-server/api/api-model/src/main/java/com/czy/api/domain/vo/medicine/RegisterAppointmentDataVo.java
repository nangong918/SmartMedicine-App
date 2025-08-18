package com.czy.api.domain.vo.medicine;

import lombok.Data;

/**
 * @author 13225
 * @date 2025/8/18 14:39
 */
@Data
public class RegisterAppointmentDataVo {
    // 预约时间：yyyy-MM-dd
    public String data;
    // 剩余可预约数量
    public Integer RemainCount;
    // 最低费用
    public String MinCost;
}
