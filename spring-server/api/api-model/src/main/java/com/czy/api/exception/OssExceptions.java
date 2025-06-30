package com.czy.api.exception;

import lombok.Getter;

/**
 * @author 13225
 * @date 2025/6/26 17:22
 */
@Getter
public enum OssExceptions implements ExceptionEnums{

    // 文件不存在
    FILE_NOT_EXIST("O_10001", "文件不存在"),
    ;

    private final String code;
    private final String message;

    OssExceptions(String code, String message) {
        this.code = code;
        this.message = message;
    }

    // code -> o
    public static OssExceptions getByCode(String code) {
        for (OssExceptions value : OssExceptions.values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        return null;
    }
}
