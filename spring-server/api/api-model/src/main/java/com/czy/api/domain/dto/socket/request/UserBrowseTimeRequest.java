package com.czy.api.domain.dto.socket.request;

import com.czy.api.domain.dto.base.BaseRequestData;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

/**
 * @author 13225
 * @date 2025/5/23 17:05
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserBrowseTimeRequest extends BaseRequestData {
    @NotEmpty(message = "浏览时长不能为空")
    @Min(value = 0, message = "浏览时长不能小于0")
    private Long browseDuration;
    @NotEmpty(message = "用户账号不能为空")
    private Long postId;
}
