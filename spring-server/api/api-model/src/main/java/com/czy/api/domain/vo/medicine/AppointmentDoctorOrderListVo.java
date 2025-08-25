package com.czy.api.domain.vo.medicine;

import com.czy.api.domain.ao.medicine.HospitalAo;
import lombok.Data;

import java.io.Serializable;

/**
 * @author 13225
 * @date 2025/8/21 10:49
 */
@Data
public class AppointmentDoctorOrderListVo implements Serializable {
    /// 医生视图
    public DoctorVo doctorVo;
    /// 医院视图 + data
    public HospitalAo hospitalAo;

    /// data
/*    // 剩余数量 (无论剩余多少都跟订单没有关系了)
    public Integer remainCount;*/
    // 费用 BigDecimal
    public String cost;
    // 预约时间区间 yyyy-MM-dd HH:mm:ss -洗数据-> HH:mm（只保留此部分） 他妈的前端自己去洗，自己没手吗妈了个逼的
    public String beginDate;
    public String endDate;
    // 用户预约之后审批结果时间
    public String approveDate;

    /// 计算填充值
/*    // 医院距离 (预约成功之后距离就跟用户没关系了，用户定位是变化的，（你他妈的想要刻舟求剑吗）)
    public Double distance;*/
    // merchantStatus + customerStatus 由前端去整合未orderStatus；当然如果你要后端处理的话，其实也是吧Android的代码复制过来。交给前端去搞简洁一点，反正前端也是我自己写代码
    /**
     * 记录本身状态: 可预约，已结束，售罄，等待开放
     * @see com.czy.api.constant.medicine.AppointmentMerchantStatusEnum
     */
    public Integer merchantStatus;
    /**
     * 用户预约状态: 待支付，待使用，待评价，退款中，退款失败，已取消
     * @see com.czy.api.constant.UserOrderStatusEnum
     */
    public Integer customerStatus;
}
