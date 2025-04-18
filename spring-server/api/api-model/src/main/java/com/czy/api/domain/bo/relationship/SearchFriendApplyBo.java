package com.czy.api.domain.bo.relationship;

import lombok.Data;

/**
 * @author 13225
 * @date 2025/2/28 21:42
 */
@Data
public class SearchFriendApplyBo {

    public String account;
    public String userName;
    public String phone;
    // 申请时间 (时间戳)
    public Long applyTime;
    // 处理时间 (时间戳，可以为空)
    public Long handleTime;
    // 申请来源 (根据需要添加)
    public Integer source;
    // 聊天列表 (JSON 格式)
    public String chatList;
    // 头像uri
    public String avatarUri;
    // 申请状态
    public int applyStatus;
    // 处理状态
    public int handleStatus;
    // 是否拉黑
    public boolean isBlack = false;
    // 申请人账号
    public String applyAccount;
    // 处理人账号
    public String handlerAccount;
}
