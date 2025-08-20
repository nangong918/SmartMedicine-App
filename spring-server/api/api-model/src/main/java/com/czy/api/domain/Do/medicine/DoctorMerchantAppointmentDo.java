package com.czy.api.domain.Do.medicine;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author 13225
 * @date 2025/8/18 15:32
 * 医生商家记录
 * 医生的可挂号预约记录 list(下拉列表list，等待预约)
 */
@Data
public class DoctorMerchantAppointmentDo {
    @Id
    private Long id;
    // 医生id
    private Long doctorId;
    // 医院id
    private Long hospitalId;

    // 部门id(code; 是int)
    private Integer departmentId;
    // 科室id(code; 是int)
    private Integer subjectId;

    // 价格
    private BigDecimal cost;
    // 剩余
    private Integer remainCount;

    // 预约时间区间 yyyy-MM-dd HH:mm:ss
    private LocalDateTime beginDate;
    private LocalDateTime endDate;
}
