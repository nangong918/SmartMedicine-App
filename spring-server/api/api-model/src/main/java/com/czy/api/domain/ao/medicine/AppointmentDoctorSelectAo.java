package com.czy.api.domain.ao.medicine;

import com.czy.api.constant.medicine.DepartmentEnum;
import com.czy.api.constant.medicine.SubjectEnum;
import com.czy.api.domain.ao.LocationAo;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author 13225
 * @date 2025/8/18 10:34
 * 挂号预约的参数
 */
@Data
public class AppointmentDoctorSelectAo {
    @Valid
    @NotNull(message = "挂号地点不能为空")
    public LocationAo registerLocation;
    // 9月19日：yyyy-MM-dd格式
    @NotEmpty(message = "挂号时间不能为空")
    public String registerTime;
    /**
     * 挂号部门code
     * @see DepartmentEnum
     */
    @NotNull(message = "部门不能为空")
    public Integer registerDepartmentCode;
    /**
     * 挂号科目code
     * @see SubjectEnum
     */
    @NotNull(message = "科目不能为空")
    public Integer registerSubjectCode;
    /// 经纬度
    public Double longitude;
    public Double latitude;
}
