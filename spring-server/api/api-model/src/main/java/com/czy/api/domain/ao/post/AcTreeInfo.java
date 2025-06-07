package com.czy.api.domain.ao.post;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 13225
 * @date 2025/5/5 16:51
 */
@Data
public class AcTreeInfo implements Serializable {
    // name
    private String key;
    // key
    private String value;

    public AcTreeInfo() {
    }

    public AcTreeInfo(String key, String value) {
        this.key = key;
        this.value = value;
    }
}
