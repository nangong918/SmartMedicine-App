package com.czy.api.domain.vo.medicine;

import com.czy.api.domain.ao.medicine.HospitalAo;
import lombok.Data;

/**
 * @author 13225
 * @date 2025/8/18 14:40
 */
@Data
public class RegisterAppointmentDoctorCardVo {
    /// 医生视图
    public DoctorVo doctorVo;
    /// 医院视图 + data
    public HospitalAo hospitalAo;

    /// data
    // 剩余数量
    public Integer RemainCount;
    // 费用 BigDecimal
    public String cost;

    /// 计算填充值
    // 医院距离
    public String distance;
}
