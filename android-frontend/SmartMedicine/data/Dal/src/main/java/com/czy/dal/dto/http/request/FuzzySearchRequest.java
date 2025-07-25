package com.czy.dal.dto.http.request;


import com.czy.baseUtilsLib.json.BaseBean;


/**
 * @author 13225
 * @date 2025/4/30 18:27
 */
public class FuzzySearchRequest implements BaseBean {
    // 用户account，用于搜索的context特征上下文
//    @NotNull(message = "发送者Id不能为空")
    public Long userId;
    // 搜索的句子 最大长度16，最小长度2
/*    @NotEmpty(message = "搜索的句子不能为空")
    @Size(
            min = SearchConstant.SEARCH_MIN_WORLDS,
            max = SearchConstant.SEARCH_MAX_WORLDS,
            message = "搜索的句子长度必须在2-15之间"
    )*/
    public String sentence;
}
