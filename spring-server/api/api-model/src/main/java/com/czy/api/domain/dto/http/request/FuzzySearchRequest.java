package com.czy.api.domain.dto.http.request;

import com.czy.api.constant.search.SearchConstant;
import json.BaseBean;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

/**
 * @author 13225
 * @date 2025/4/30 18:27
 */
@Data
public class FuzzySearchRequest implements BaseBean {
    // 用户account，用于搜索的context特征上下文
    @NotEmpty(message = "发送者账号不能为空")
    public String userAccount;
    // 搜索的句子 最大长度16，最小长度2
    @NotEmpty(message = "搜索的句子不能为空")
    @Size(
            min = SearchConstant.SEARCH_MIN_WORLDS,
            max = SearchConstant.SEARCH_MAX_WORLDS,
            message = "搜索的句子长度必须在2-15之间"
    )
    public String sentence;
}
