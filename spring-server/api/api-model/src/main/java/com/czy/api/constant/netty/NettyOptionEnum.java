package com.czy.api.constant.netty;

import lombok.Getter;

/**
 * @author 13225
 * @date 2025/4/28 16:07
 */
@Getter
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

    NettyOptionEnum(int code, String value) {
        this.code = code;
        this.value = value;
    }

    // code -> o
    public static NettyOptionEnum getByCode(int code) {
        for (NettyOptionEnum value : NettyOptionEnum.values()) {
            if (value.code == code) {
                return value;
            }
        }
        return NULL;
    }

}
