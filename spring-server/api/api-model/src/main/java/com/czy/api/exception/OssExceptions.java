package com.czy.api.exception;

import exception.ExceptionEnums;
import lombok.Getter;

/**
 * @author 13225
 * @date 2025/6/26 17:22
 */
@Getter
public enum OssExceptions implements ExceptionEnums {

    // 文件不存在
    FILE_NOT_EXIST("O_10001", "文件不存在"),
    // 上传文件不能为空
    UPLOAD_FILE_IS_EMPTY("O_10002", "上传文件不能为空"),
    // 请检查文件id正确性
    CHECK_FILE_ID_IS_RIGHT("O_10003", "请检查文件id正确性"),
    // fileUrl解析失败
    FILE_URL_PARSE_ERROR("O_10004", "fileUrl解析失败"),
    // 未查询到已上传的文件
    FILE_NOT_FOUND("O_10005", "未查询到已上传的文件"),
    // 上传文件数据到数据库失败
    UPLOAD_FILE_RECORD_ERROR("O_10006", "上传文件数据到数据库失败"),
    // 上传文件到oss失败
    UPLOAD_FILE_TO_OSS_ERROR("O_10007", "上传文件到oss失败"),
    // 上传文件失败
    UPLOAD_FILE_ERROR("O_10008", "上传文件失败"),
    // 正在修改请勿频繁点击
    UPDATE_POST_FREQUENTLY("O_10009", "正在修改请勿频繁点击"),
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
