package com.czy.api.domain.dto.http.request;

import lombok.Data;

import javax.validation.constraints.NotEmpty;


@Data
public class UpdateAvatarRequest {
    private Integer id;
    @NotEmpty(message = "用户账号不能为空")
    private String account;
}
