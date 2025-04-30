package com.czy.api.domain.dto.http.request;

import com.czy.api.domain.dto.http.base.BaseNettyRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

/**
 * @author 13225
 * @date 2025/4/29 15:22
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SearchUserByNameRequest extends BaseNettyRequest {
    @NotEmpty(message = "搜索的用户名不能为空")
    @Size(min = 3, message = "用户名长度必须大于2")
    public String userName;
}
