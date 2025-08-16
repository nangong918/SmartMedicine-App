package com.czy.api.domain.dto.http.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * @author 13225
 * @date 2025/8/16 16:36
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class GetComment2Request extends GetComment1Request{
    @NotNull(message = "level1commentId不能为空")
    public Long level1commentId;
}
