package com.czy.dal.ao.newUser;


/**
 * @author 13225
 * @date 2025/2/27 22:18
 */
public class SearchFriendApplyAo {

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
    // 默认设置为不是好友，所以状态是申请
    public AddUserStatusAo addUserStatusAo = new AddUserStatusAo();

}
