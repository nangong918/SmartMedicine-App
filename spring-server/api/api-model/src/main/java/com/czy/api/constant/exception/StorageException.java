package com.czy.api.constant.exception;

import exception.AppException;
import lombok.Getter;
import lombok.Setter;

/**
 * @author 13225
 * @date 2025/4/18 21:13
 */
@Getter
@Setter
public class StorageException extends AppException {
    public StorageException(String errMsg) {
        super(errMsg);
    }

    public StorageException(String errMsg, Throwable e) {
        super(errMsg, e);
    }

    public StorageException(String errCode, String errMsg) {
        super(errCode, errMsg);
    }

    public StorageException(String errCode, int statusCode, String errMsg) {
        super(errCode, statusCode, errMsg);
    }

    public StorageException(String errCode, int statusCode, String errMsg, Throwable cause) {
        super(errCode, statusCode, errMsg, cause);
    }
}
