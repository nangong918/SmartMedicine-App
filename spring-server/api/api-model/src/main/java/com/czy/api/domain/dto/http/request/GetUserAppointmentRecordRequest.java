package com.czy.api.domain.dto.http.request;

import com.czy.api.constant.medicine.AppointmentSortTypeEnum;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author 13225
 * @date 2025/8/19 16:03
 */
@Data
public class GetUserAppointmentRecordRequest {
    @NotNull(message = "用户id不能为空")
    private Long userId;
    // 排序方式
    @NotNull(message = "排序方式不能为空")
    private Integer sortType = AppointmentSortTypeEnum.DEFAULT.getCode();
}
