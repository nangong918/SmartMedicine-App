package com.czy.baseUtilsLib.network;


import com.czy.baseUtilsLib.json.BaseBean;

import java.io.Serializable;


/**
 * @author 13225
 * 基本响应体
 */
public class BaseResponse<T> implements BaseBean, Serializable {

    // 泛型字段，用于存放具体的数据
    protected Integer code;
    protected String message;
    protected T data;

    public BaseResponse() {
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
