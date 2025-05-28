package com.czy.appcore.service;

import com.czy.dal.ao.newUser.AddUserStatusAo;
import com.czy.dal.constant.newUserGroup.ApplyButtonStatusEnum;
import com.czy.dal.constant.newUserGroup.ApplyStatusEnum;
import com.czy.dal.constant.newUserGroup.HandleButtonStatusEnum;
import com.czy.dal.constant.newUserGroup.HandleStatusEnum;

public class AddUserStateHandler {

    public static Integer[] getApplyStateButton(AddUserStatusAo ao){
        if (ao.isBlack){
            return new Integer[] {
                    // 被拉黑了
                    ApplyButtonStatusEnum.BE_BLACK.code,
            };
        }
        else {
            if (ao.handleStatus == HandleStatusEnum.AGREE.code){
                return new Integer[] {
                        // 已添加
                        ApplyButtonStatusEnum.ADDED.code,
                };
            }
            else if (ao.handleStatus == HandleStatusEnum.REFUSED.code){
                return new Integer[] {
                        // 被拒绝
                        ApplyButtonStatusEnum.BE_REFUSED.code,
                };
            }
            // 未处理: NOT_HANDLE
            else {
                if (ao.applyStatus == ApplyStatusEnum.APPLYING.code){
                    return new Integer[] {
                            // 取消申请
                            ApplyButtonStatusEnum.CANCEL_APPLY.code,
                    };
                }
                // NOT_APPLY 未申请；不可能为 HANDLED；因为处理放已经处理了
                else {
                    return new Integer[] {
                            // 申请
                            ApplyButtonStatusEnum.APPLY_ADD.code,
                    };
                }
            }
        }
    }

    public static Integer[] getHandleStateButton(AddUserStatusAo ao){
        // 已经拉黑了
        if (ao.isBlack){
            return new Integer[] {
                    // 已经拉黑了：解除拉黑
                    HandleButtonStatusEnum.UN_BLACK.code,
            };
        }
        // 取消申请了
        if (ao.applyStatus == ApplyStatusEnum.NOT_APPLY.code){
            return new Integer[] {
                    // 已取消
                    HandleButtonStatusEnum.BE_CANCELED.code,
            };
        }
        else {
            // 已处理
            if (ao.handleStatus == HandleStatusEnum.AGREE.code) {
                return new Integer[] {
                        // 已同意
                        HandleButtonStatusEnum.HAVE_AGREED.code,
                };
            }
            else if (ao.handleStatus == HandleStatusEnum.REFUSED.code){
                return new Integer[] {
                        // 已拒绝
                        HandleButtonStatusEnum.HAVE_REFUSED.code,
                };
            }
            // 未处理
            else {
                return new Integer[] {
                        // 同意
                        HandleButtonStatusEnum.AGREE.code,
                        // 拒绝
                        HandleButtonStatusEnum.REFUSED.code,
                        // 拉黑
                        HandleButtonStatusEnum.BLACK.code,
                };
            }
        }
    }

    public static Integer[] getHandleStateButton(AddUserStatusAo ao, boolean isBeAdd){
        // 是被添加的view
        if (isBeAdd){
            return getApplyStateButton(ao);
        }
        else {
            return getHandleStateButton(ao);
        }
    }
}
