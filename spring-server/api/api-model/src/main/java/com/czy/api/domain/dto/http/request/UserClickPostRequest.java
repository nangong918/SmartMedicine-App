package com.czy.api.domain.dto.http.request;

import com.czy.api.domain.dto.base.UserActionBaseRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author 13225
 * @date 2025/5/9 16:01
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserClickPostRequest extends UserActionBaseRequest {
    private Long postId;
}
