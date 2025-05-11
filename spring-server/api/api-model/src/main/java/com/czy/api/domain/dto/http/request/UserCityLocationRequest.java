package com.czy.api.domain.dto.http.request;

import com.czy.api.domain.ao.feature.UserCityLocationInfoAo;
import com.czy.api.domain.dto.base.UserActionBaseRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * @author 13225
 * @date 2025/5/9 15:52
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserCityLocationRequest extends UserActionBaseRequest {
    @Valid
    @NotNull(message = "用户城市定位信息不能为空")
    private UserCityLocationInfoAo ao;
}
