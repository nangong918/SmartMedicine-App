package com.czy.api.domain.dto.http.response;

import com.czy.api.domain.dto.http.base.BaseHttpResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author 13225
 * @date 2025/4/21 11:32
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PostPublishResponse extends BaseHttpResponse {
    // 雪花id
    public Long snowflakeId;
}
