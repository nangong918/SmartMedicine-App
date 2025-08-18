package com.czy.api.domain.ao.medicine;

import com.czy.api.constant.medicine.DepartmentEnum;
import com.czy.api.constant.medicine.SubjectEnum;
import com.czy.api.domain.ao.LocationAo;
import lombok.Data;

/**
 * @author 13225
 * @date 2025/8/18 10:34
 * 挂号预约的参数
 */
@Data
public class RegisterAppointmentSelectAo {
    public LocationAo registerLocation;
    // 9月19日：yyyy-MM-dd格式
    public String registerTime;
    /**
     * 挂号部门code
     * @see DepartmentEnum
     */
    public Integer registerDepartmentCode;
    /**
     * 挂号科目code
     * @see SubjectEnum
     */
    public Integer registerSubjectCode;
}
