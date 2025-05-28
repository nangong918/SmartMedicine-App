package com.czy.dal.constant.newUserGroup;

public enum HandleButtonStatusEnum {

    // 同意（未处理）
    AGREE("同意", 0),
    // 拒绝（未处理）
    REFUSED("拒绝", 1),
    // 拉黑（未处理）
    BLACK("拉黑", 2),
    // 已同意（同意）
    HAVE_AGREED("已同意", 3),
    // 已拒绝（拒绝）
    HAVE_REFUSED("已拒绝", 4),
    // 解除拉黑（已拉黑）
    UN_BLACK("解除拉黑", 5),
    // 已取消（对方已取消）
    BE_CANCELED("对方已取消", 6),
    ;

    public final String name;
    public final int code;

    HandleButtonStatusEnum(String name, int code) {
        this.name = name;
        this.code = code;
    }

    // code -> o
    public static HandleButtonStatusEnum getByCode(int code) {
        for (HandleButtonStatusEnum addUserIsAgreeStateEnum : HandleButtonStatusEnum.values()) {
            if (addUserIsAgreeStateEnum.code == code) {
                return addUserIsAgreeStateEnum;
            }
        }
        return null;
    }

    // name -> o
    public static HandleButtonStatusEnum getByName(String name) {
        for (HandleButtonStatusEnum addUserIsAgreeStateEnum : HandleButtonStatusEnum.values()) {
            if (addUserIsAgreeStateEnum.name.equals(name)) {
                return addUserIsAgreeStateEnum;
            }
        }
        return null;
    }
}
