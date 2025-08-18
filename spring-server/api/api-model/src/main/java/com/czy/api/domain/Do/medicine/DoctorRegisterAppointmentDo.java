package com.czy.api.domain.Do.medicine;

import com.czy.api.constant.medicine.DoctorRegisterAppointmentStatusEnum;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @author 13225
 * @date 2025/8/18 15:32
 * 医生的可挂号预约记录 list(下拉列表list，等待预约)
 */
@Data
public class DoctorRegisterAppointmentDo {
    @Id
    private Long id;
    // 医生id
    private Long doctorId;
    // 医院id
    private Long hospitalId;

    // 部门id
    private Long departmentId;
    // 科室id
    private Long subjectId;

    // 价格
    private BigDecimal cost;
    // 剩余
    private Integer remainCount;

    // 预约时间区间 yyyy-MM-dd HH:mm:ss
    private LocalDate beginDate;
    private LocalDate endDate;

    /**
     * 状态
     * @see DoctorRegisterAppointmentStatusEnum
     */
    private Integer status = DoctorRegisterAppointmentStatusEnum.WAITING_OPEN.getCode();
}
