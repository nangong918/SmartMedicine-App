package com.czy.api.constant.relationship.newUserGroup;

public enum ApplyButtonStatusEnum {

    // 申请添加（未申请）
    APPLY_ADD("申请添加", 0),
    // 取消申请（已申请）
    CANCEL_APPLY("取消申请", 1),
    // 已通过（被处理）
    ADDED("已添加", 2),
    // 已被拒绝（被处理）
    BE_REFUSED("已被拒绝", 3),
    // 已被拉黑（被处理）
    BE_BLACK("被拉黑", 4),
    ;

    public final String name;
    public final int code;

    ApplyButtonStatusEnum(String name, int code) {
        this.name = name;
        this.code = code;
    }

    // code -> o
    public static ApplyButtonStatusEnum getByName(String name) {
        for (ApplyButtonStatusEnum o : ApplyButtonStatusEnum.values()) {
            if (o.name.equals(name)) {
                return o;
            }
        }
        return null;
    }

    // name -> o
    public static ApplyButtonStatusEnum getByCode(int code) {
        for (ApplyButtonStatusEnum o : ApplyButtonStatusEnum.values()) {
            if (o.code == code) {
                return o;
            }
        }
        return null;
    }

}
