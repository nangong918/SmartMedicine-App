package com.czy.gateway.exception;

import exception.AppException;

/**
 * @author 13225
 * @date 2025/4/2 16:53
 */
public class InterceptorException extends AppException {
    public InterceptorException(String errMsg) {
        super(errMsg);
    }

    public InterceptorException(String errCode, String errMsg) {
        super(errCode, errMsg);
    }

    public InterceptorException(String errCode, int statusCode, String errMsg) {
        super(errCode, statusCode, errMsg);
    }

    public InterceptorException(String errCode, int statusCode, String errMsg, Throwable cause) {
        super(errCode, statusCode, errMsg, cause);
    }
}
