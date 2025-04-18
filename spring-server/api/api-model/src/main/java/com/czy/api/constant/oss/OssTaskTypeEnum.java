package com.czy.api.constant.oss;

/**
 * @author 13225
 * @date 2025/4/18 23:46
 */
public enum OssTaskTypeEnum {
    // 增
    // 删
    // 改
    // 查
    // int code；String desc
    NULL(0, "未知"),
    ADD(1, "新增"),
    DELETE(2, "删除"),
    UPDATE(3, "修改"),
    FIND(4, "查询");

    private final int code;
    private final String desc;

    OssTaskTypeEnum(int code, String desc) {
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
