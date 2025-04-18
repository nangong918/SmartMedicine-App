package com.czy.api.domain.ao.relationship;




import com.czy.api.constant.relationship.newUserGroup.ApplyStatusEnum;
import com.czy.api.constant.relationship.newUserGroup.HandleStatusEnum;
import lombok.Data;
import org.springframework.util.StringUtils;

@Data
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
        if (StringUtils.isEmpty(myUserAccount)){
            return false;
        }
        // applyAccount和本account相同 -> view是被添加
        return myUserAccount.equals(applyAccount);
    }
}
