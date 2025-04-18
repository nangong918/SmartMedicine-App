package com.czy.api.constant.exception;

import exception.AppException;
import lombok.Getter;
import lombok.Setter;


/**
 * @author 13225
 * @date 2025/4/18 11:16
 */
@Getter
@Setter
public class OssException extends AppException {

    public OssException(String errMsg) {
        super(errMsg);
    }

    public OssException(String errMsg, Throwable e) {
        super(errMsg, e);
    }

    public OssException(String errCode, String errMsg) {
        super(errCode, errMsg);
    }

    public OssException(String errCode, int statusCode, String errMsg) {
        super(errCode, statusCode, errMsg);
    }

    public OssException(String errCode, int statusCode, String errMsg, Throwable cause) {
        super(errCode, statusCode, errMsg, cause);
    }
}
