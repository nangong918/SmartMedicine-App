package com.czy.api.constant.oss;

/**
 * @author 13225
 * @date 2025/4/21 15:28
 */
public enum OssResponseTypeEnum {

    // 成功
    // 失败
    NULL(0, "未知"),
    SUCCESS(1, "成功"),
    FAIL(2, "失败"),
    ;

    private final int code;
    private final String desc;

    OssResponseTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
