package com.czy.api.constant.relationship.newUserGroup;


/**
 * 1.申请好友走通
 * 2.新朋友消息，新朋友消息处理
 * 3.发送消息走通
 * 4.消息前后端持久化
 * 5.图片压缩与发送
 * 6.视频通话与推流（surfaceView独立线程显示）
 */
public enum ApplyStatusEnum {
    /**
     * 1. 未申请（按钮：申请；点击之后：取消申请）
     * 2. 申请中（按钮：取消申请；点击之后：申请）
     */
    // 未申请 （默认: 0）
    NOT_APPLY("未申请", 0),
    // 申请中
    APPLYING("申请中", 1),
    // 已被处理
    HANDLED("已处理", 2),
    // 主动删除
    DELETED("删除", 3);

    // 黑名单最后加，作为通用按钮


    public final String name;
    public final int code;


    ApplyStatusEnum(String name, int code) {
        this.name = name;
        this.code = code;
    }

    // code -> o
    public static ApplyStatusEnum getByCode(int code) {
        for (ApplyStatusEnum addUserIsAgreeStateEnum : ApplyStatusEnum.values()) {
            if (addUserIsAgreeStateEnum.code == code) {
                return addUserIsAgreeStateEnum;
            }
        }
        return null;
    }

    // name -> o
    public static ApplyStatusEnum getByName(String name) {
        for (ApplyStatusEnum addUserIsAgreeStateEnum : ApplyStatusEnum.values()) {
            if (addUserIsAgreeStateEnum.name.equals(name)) {
                return addUserIsAgreeStateEnum;
            }
        }
        return null;
    }
}
