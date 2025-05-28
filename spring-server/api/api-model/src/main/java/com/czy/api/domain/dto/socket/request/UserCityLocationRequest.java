package com.czy.api.domain.dto.socket.request;

import com.czy.api.domain.dto.base.BaseRequestData;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;

/**
 * @author 13225
 * @date 2025/5/23 17:00
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserCityLocationRequest extends BaseRequestData {
    @NotEmpty(message = "城市名称不能为空")
    private String cityName;
    // 经度 null able
    @NotEmpty(message = "经度不能为空")
    private Double longitude;
    // 纬度 null able
    @NotEmpty(message = "纬度不能为空")
    private Double latitude;
}
