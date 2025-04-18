package com.czy.api.domain.bo.relationship;



import com.czy.api.constant.relationship.newUserGroup.AddSourceEnum;
import lombok.Data;

@Data
public class NewUserItemBo {

    // 是否是添加我的请求 / 是否对我添加别人请求的响应
    public boolean isAddMeNotResponse = true;

    // 添加记录对话信息 (JSON)
    public String chatList;

    // 被查询到的user的Account
    public String userAccount;

    // 被查询到的user名称
    public String userName;

    // 用户头像
    public String avatarUrl;

    // 申请时间
    public Long applyTime;

    // 处理时间
    public Long handleTime;

    // 添加来源：手机，账号，扫码，群
    public Integer addSource = AddSourceEnum.PHONE.code;

    // 添加状态
    // 申请状态
    public int applyStatus;
    // 处理状态
    public int handleStatus;
    // 是否拉黑 0.否 1.是
    public int isBlack;
    // applyAccount + handlerAccount -> applyAccount是否是本号主 -> 判断此View是否是被添加
    // applyAccount
    public String applyAccount;
    // handlerAccount
    public String handlerAccount;
}
