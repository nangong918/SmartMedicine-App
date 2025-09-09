package com.czy.domain.vo.entity.medicine;


import androidx.annotation.NonNull;

import com.czy.domain.ao.medicine.HospitalAo;

import java.io.Serializable;

/**
 * @author 13225
 * @date 2025/8/18 14:40
 * 挂号医生卡片视图
 */
public class AppointmentDoctorMerchantCardVo implements Cloneable , Serializable {
    /// 医生视图
    public DoctorVo doctorVo;
    /// 医院视图 + data
    public HospitalAo hospitalAo;

    /// data
    // 剩余数量
    public Integer remainCount;
    // 费用 BigDecimal
    public String cost;
    // 预约时间区间 yyyy-MM-dd HH:mm:ss -洗数据-> HH:mm（只保留此部分） 他妈的前端自己去洗，自己没手吗妈了个逼的
    public String beginDate;
    public String endDate;

    /// 计算填充值
    // 医院距离 (用double因为需要排序)
    public Double distance;

    // 记录本身状态: 可预约，已结束，售罄，等待开放
    // 用户预约状态: 待支付，待使用，待评价，退款中，退款失败，已取消

    @NonNull
    @Override
    public AppointmentDoctorMerchantCardVo clone() throws CloneNotSupportedException {
        return (AppointmentDoctorMerchantCardVo) super.clone();
    }
}
