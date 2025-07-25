package com.czy.dal.ao.search;


import com.czy.dal.constant.search.PersonalResultIntent;

/**
 * @author 13225
 * @date 2025/5/9 14:15
 */
public class PersonalEvaluateAo {
    public Integer intent = PersonalResultIntent.UNRECOGNIZED.getType();
    // 心脏病可能
    public Double heartDisease = null;
    // 糖尿病可能
    public Double diabetes = null;
}
