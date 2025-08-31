package com.czy.api.domain.vo.medicine;

import lombok.Data;

/**
 * @author 13225
 * @date 2025/8/21 10:59
 * DetailsVo就是ListVo的扩展；
 * list展示不下的东西就放在这里，所以才需要详情页面
 */
@Data
public class AppointmentDoctorOrderDetailsVo {
    // list中的数据
    private AppointmentDoctorOrderListVo listVo;
    // 后续如果有单独的details数据则在此处添加 （当然目前是没有的）
}
