package com.czy.api.domain.dto.http.response;

import json.BaseBean;
import lombok.Data;

import java.io.Serializable;

/**
 * @author 13225
 * @date 2025/4/21 11:32
 */
@Data
public class PostPublishResponse implements BaseBean, Serializable {
    // 雪花id
    public Long snowflakeId;
}
