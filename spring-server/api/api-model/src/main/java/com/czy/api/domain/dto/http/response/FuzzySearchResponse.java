package com.czy.api.domain.dto.http.response;

import json.BaseBean;
import lombok.Data;

import java.util.Map;

/**
 * @author 13225
 * @date 2025/4/30 18:29
 */
@Data
public class FuzzySearchResponse implements BaseBean {
    // 结果类型
    public Integer type;
    // 数据；由于有多种数据源，所以用Map存储
    public Map<String, String> data;
}
