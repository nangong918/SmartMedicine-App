package com.czy.domain.vo.entity.medicine;


import com.czy.baseutil.json.BaseBean;
import com.czy.domain.ao.medicine.RegisterAppointmentDoctorCardAo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 13225
 * @date 2025/8/18 16:38
 * 挂号预约页面
 */
public class AppointmentDoctorPageVo implements Serializable, BaseBean {
    // 日期vo
    public AppointmentDoctorDataVo dataVo;
    // 医生卡片voList
    public List<RegisterAppointmentDoctorCardAo> cardAos = new ArrayList<>();
}
