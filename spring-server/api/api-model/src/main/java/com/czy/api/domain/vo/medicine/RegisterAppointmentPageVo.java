package com.czy.api.domain.vo.medicine;

import json.BaseBean;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 13225
 * @date 2025/8/18 16:38
 */
@Data
public class RegisterAppointmentPageVo implements BaseBean {
    // 日期vo
    private RegisterAppointmentDataVo dataVo;
    // 医生卡片voList
    private List<RegisterAppointmentDoctorCardVo> cardVos = new ArrayList<>();
}
