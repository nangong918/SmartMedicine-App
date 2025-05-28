package com.czy.api.domain.dto.socket.request;

import com.czy.api.domain.dto.base.BaseRequestData;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;

/**
 * @author 13225
 * @date 2025/5/23 17:04
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserClickPostRequest extends BaseRequestData {
    @NotEmpty(message = "发送者账号不能为空")
    private Long postId;
}
