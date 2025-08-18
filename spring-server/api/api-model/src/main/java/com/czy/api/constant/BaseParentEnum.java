package com.czy.api.constant;

import json.BaseBean;
import lombok.Data;

import java.io.Serializable;

/**
 * @author 13225
 * @date 2025/8/18 13:32
 * 父枚举类
 */
@Data
public class BaseParentEnum implements BaseBean, Serializable {
    public String name;
    public int code;
    public BaseEnum[] childEnums;

    public BaseParentEnum() {
    }

    public BaseParentEnum(String name, int code, BaseEnum[] childEnums) {
        this.name = name;
        this.code = code;
        this.childEnums = childEnums;
    }
}
