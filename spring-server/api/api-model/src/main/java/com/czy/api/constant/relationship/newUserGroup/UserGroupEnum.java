package com.czy.api.constant.relationship.newUserGroup;

public enum UserGroupEnum {

    // user
    USER(0, "user"),
    // group
    GROUP(1, "group");

    public final int code;
    public final String name;


    UserGroupEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static final String INTENT_EXTRA_NAME = "UserGroupEnum";

    // code -> UserGroupEnum
    public static UserGroupEnum getUserGroupEnum(int code) {
        for (UserGroupEnum userGroupEnum : UserGroupEnum.values()) {
            if (userGroupEnum.code == code) {
                return userGroupEnum;
            }
        }
        return null;
    }

    // name -> UserGroupEnum
    public static UserGroupEnum getUserGroupEnum(String name) {
        for (UserGroupEnum userGroupEnum : UserGroupEnum.values()) {
            if (userGroupEnum.name.equals(name)) {
                return userGroupEnum;
            }
        }
        return null;
    }
}
