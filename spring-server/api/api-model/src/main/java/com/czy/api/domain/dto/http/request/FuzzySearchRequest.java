package com.czy.api.domain.dto.http.request;

import json.BaseBean;
import lombok.Data;

/**
 * @author 13225
 * @date 2025/4/30 18:27
 */
@Data
public class FuzzySearchRequest implements BaseBean {
    // 用户account，用于搜索的context特征上下文
    public String userAccount;
    // 搜索的句子
    public String sentence;
}
