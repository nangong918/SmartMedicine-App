package com.czy.api.domain.dto.socket.response;


import com.czy.api.domain.dto.base.BaseResponseData;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author 13225
 * @date 2025/3/3 19:08
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class HandleAddUserResponse extends BaseResponseData {
    // 添加状态 AddUserStatusAo
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


    // 附加消息
    public String additionalContent;
    // user账号
    public String userAccount;
    // user名称
    public String userName;
    // 用户头像
    public String avatarUrl;
}
