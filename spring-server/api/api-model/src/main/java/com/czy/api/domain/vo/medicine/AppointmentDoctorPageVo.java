package com.czy.api.domain.vo.medicine;

import com.czy.api.domain.ao.medicine.RegisterAppointmentDoctorCardAo;
import json.BaseBean;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 13225
 * @date 2025/8/18 16:38
 * 挂号预约页面
 */
@Data
public class AppointmentDoctorPageVo implements BaseBean {
    // 日期vo
    private AppointmentDoctorDataVo dataVo;
    // 医生卡片voList
    private List<RegisterAppointmentDoctorCardAo> cardAos = new ArrayList<>();
}
