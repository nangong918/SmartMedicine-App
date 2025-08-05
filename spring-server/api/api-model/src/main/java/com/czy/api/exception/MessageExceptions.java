package com.czy.api.exception;

import exception.ExceptionEnums;
import lombok.Getter;

/**
 * @author 13225
 * @date 2025/6/26 17:22
 */
@Getter
public enum MessageExceptions implements ExceptionEnums {

    // 文件类型消息的文件数据存储失败
    FILE_DATA_STORAGE_FAIL("M_10001", "文件类型消息的文件数据存储失败"),
    // 文件类型消息的文件存储失败
    FILE_STORAGE_FAIL("M_10002", "文件类型消息的文件存储失败"),
    ;

    private final String code;
    private final String message;

    MessageExceptions(String code, String message) {
        this.code = code;
        this.message = message;
    }

    // code -> o
    public static MessageExceptions getByCode(String code) {
        for (MessageExceptions value : MessageExceptions.values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        return null;
    }
}
