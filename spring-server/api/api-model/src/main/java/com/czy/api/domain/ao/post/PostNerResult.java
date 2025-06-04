package com.czy.api.domain.ao.post;

import json.BaseBean;
import lombok.Data;

/**
 * @author 13225
 * @date 2025/5/5 16:25
 */
@Data
public class PostNerResult implements BaseBean {
    private String keyWord;
    private String nerType;

    public boolean isEmpty() {
        return keyWord == null || keyWord.isEmpty() || nerType == null || nerType.isEmpty();
    }

    @Override
    public String toString() {
        return "PostNerResult{" +
                "keyWord='" + keyWord + '\'' +
                ", nerType='" + nerType + '\'' +
                '}';
    }
}
