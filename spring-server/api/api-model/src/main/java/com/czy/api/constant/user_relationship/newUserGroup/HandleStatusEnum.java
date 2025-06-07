package com.czy.api.constant.user_relationship.newUserGroup;


public enum HandleStatusEnum {
    /**
     * 1. 未处理（按钮：同意，拒绝）
     * 2. 已同意（按钮：无：已同意）
     * 3. 已拒绝（按钮：无：已拒绝）
     */
    // 未处理 （默认: 0）
    NOT_HANDLE("未处理", 0),
    // 已同意
    AGREE("同意", 1),
    // 已拒绝
    REFUSED("拒绝", 2),
    // 拉黑
    BLACK("拉黑", 3),
    ;

    // 黑名单最后加，作为通用按钮



    public final String name;
    public final int code;

    HandleStatusEnum(String name, int code) {
        this.name = name;
        this.code = code;
    }

    // code -> o
    public static HandleStatusEnum getByCode(int code) {
        for (HandleStatusEnum addUserIsAgreeStateEnum : HandleStatusEnum.values()) {
            if (addUserIsAgreeStateEnum.code == code) {
                return addUserIsAgreeStateEnum;
            }
        }
        return null;
    }

    // name -> o
    public static HandleStatusEnum getByName(String name) {
        for (HandleStatusEnum addUserIsAgreeStateEnum : HandleStatusEnum.values()) {
            if (addUserIsAgreeStateEnum.name.equals(name)) {
                return addUserIsAgreeStateEnum;
            }
        }
        return null;
    }
}
