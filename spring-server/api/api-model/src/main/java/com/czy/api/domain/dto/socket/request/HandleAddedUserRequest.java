package com.czy.api.domain.dto.socket.request;



import com.czy.api.domain.dto.base.BaseRequestData;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

@EqualsAndHashCode(callSuper = true)
@Data
public class HandleAddedUserRequest extends BaseRequestData {
    @NotBlank(message = "处理类型不能为空")
    private Integer handleType;
    // 附加消息
    @NotBlank(message = "附加内容不能为空")
    private String additionalContent;
}
