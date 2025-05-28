package com.czy.dal.constant.netty;


/**
 * @author 13225
 * @date 2025/4/28 16:07
 */
public enum NettyOptionEnum {
    /**
     * 增删改查
     */
    NULL(0, "null"),
    ADD(1, "add"),
    DELETE(2, "delete"),
    UPDATE(3, "update"),
    QUERY(4, "query")
    ;
    private final int code;
    private final String value;

    public int getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }

    NettyOptionEnum(int code, String value) {
        this.code = code;
        this.value = value;
    }

}
