package com.czy.dal.constant;

public enum ListAddOrDeleteStatusEnum {

    // 新增
    ADD(1),

    // 删除此条
    DELETE(2),

    // 更新此条
    UPDATE(3)
    ;

    public final int code;

    ListAddOrDeleteStatusEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    // code -> o
    public static ListAddOrDeleteStatusEnum getListAddOrDeleteStatusEnum(int code) {
        for (ListAddOrDeleteStatusEnum listAddOrDeleteStatusEnum : ListAddOrDeleteStatusEnum.values()) {
            if (listAddOrDeleteStatusEnum.code == code) {
                return listAddOrDeleteStatusEnum;
            }
        }
        return null;
    }
}
