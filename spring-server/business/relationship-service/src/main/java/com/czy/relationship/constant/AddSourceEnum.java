package com.czy.relationship.constant;

public enum AddSourceEnum {

    // 添加来源：手机号，名称，账号，扫码，群成员，好友推荐
    // code:0,1,2,3,4,5,6
    PHONE("手机号", 0),
    NAME("名称", 1),
    ACCOUNT("账号", 2),
    SCAN("扫码", 3),
    GROUP_MEMBER("群成员", 4),
    FRIEND_RECOMMEND("好友推荐", 5),
    ADD_ME("加我", 6);

    public final String name;
    public final int code;

    AddSourceEnum(String name, int code) {
        this.name = name;
        this.code = code;
    }

    public static AddSourceEnum getAddsource(String name) {
        for (AddSourceEnum addSourceEnum : AddSourceEnum.values()) {
            if (addSourceEnum.name.equals(name)) {
                return addSourceEnum;
            }
        }
        return null;
    }

    public static AddSourceEnum getAddSource(int code) {
        for (AddSourceEnum addSourceEnum : AddSourceEnum.values()) {
            if (addSourceEnum.code == code) {
                return addSourceEnum;
            }
        }
        return null;
    }

}
