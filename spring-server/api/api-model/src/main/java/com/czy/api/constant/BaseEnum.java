package com.czy.api.constant;

import json.BaseBean;
import lombok.Data;

import java.io.Serializable;

/**
 * @author 13225
 * @date 2025/8/18 13:32
 * 基本枚举类型
 */
@Data
public class BaseEnum implements BaseBean, Serializable {
    public String name;
    public int code;

    public BaseEnum() {
    }

    public BaseEnum(String name, int code) {
        this.name = name;
        this.code = code;
    }
}
