package com.czy.domain.constant;


import com.czy.baseutil.json.BaseBean;

import java.io.Serializable;

/**
 * @author 13225
 * @date 2025/8/18 13:32
 * 基本枚举类型
 */
public abstract class BaseEnum implements BaseBean, Serializable {
    public String name;
    public int code;

    public BaseEnum() {
    }

    public BaseEnum(String name, int code) {
        this.name = name;
        this.code = code;
    }

    public abstract String getName();

    public abstract int getCode();
}
