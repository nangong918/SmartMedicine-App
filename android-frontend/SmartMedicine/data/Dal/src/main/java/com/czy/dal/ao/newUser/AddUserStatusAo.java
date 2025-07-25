package com.czy.dal.ao.newUser;

import android.text.TextUtils;
import android.util.Log;

import com.czy.dal.constant.newUserGroup.ApplyStatusEnum;
import com.czy.dal.constant.newUserGroup.HandleStatusEnum;

public class AddUserStatusAo {
    // 申请状态
    public int applyStatus;
    // 处理状态
    public int handleStatus;
    // 是否拉黑
    public boolean isBlack = false;
    // applyAccount + handlerAccount -> applyAccount是否是本号主 -> 判断此View是否是被添加
    // applyAccount
    public String applyAccount;
    // handlerAccount
    public String handlerAccount;

    public AddUserStatusAo() {
        applyStatus = ApplyStatusEnum.NOT_APPLY.code;
        handleStatus = HandleStatusEnum.NOT_HANDLE.code;
        isBlack = false;
    }

    public boolean isBeAdd(String myUserAccount){
        if (TextUtils.isEmpty(myUserAccount)){
            return false;
        }
        // applyAccount和本account相同 -> view是被添加
        Log.i(AddUserStatusAo.class.getName(), "isBeAdd: applyAccount=" +
                applyAccount + ", myUserAccount=" +
                myUserAccount +
                " \n isBeAdd: " + TextUtils.equals(myUserAccount, applyAccount));
        return TextUtils.equals(myUserAccount, applyAccount);
    }
}
