package com.czy.api.domain.ao.feature;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @author 13225
 * @date 2025/5/9 15:39
 */
@Data
public class UserCityLocationInfoAo {
    @NotEmpty(message = "城市名称不能为空")
    private String cityName;
    // 经度 null able
    private Double longitude;
    // 纬度 null able
    private Double latitude;
    // userId
    private Long userId;
}
