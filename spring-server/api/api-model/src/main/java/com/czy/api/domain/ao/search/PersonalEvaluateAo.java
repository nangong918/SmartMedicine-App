package com.czy.api.domain.ao.search;

import com.czy.api.constant.search.result.PersonalResultIntent;
import lombok.Data;

/**
 * @author 13225
 * @date 2025/5/9 14:15
 */
@Data
public class PersonalEvaluateAo {
    private Integer intent = PersonalResultIntent.UNRECOGNIZED.getType();
    // 心脏病可能
    private Double heartDisease = null;
    // 糖尿病可能
    private Double diabetes = null;
}
