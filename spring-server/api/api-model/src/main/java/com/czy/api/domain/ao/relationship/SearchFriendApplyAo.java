package com.czy.api.domain.ao.relationship;

import com.czy.api.domain.ao.oss.FileResAo;
import json.BaseBean;
import lombok.Data;

/**
 * @author 13225
 * @date 2025/2/27 22:18
 */
@Data
public class SearchFriendApplyAo implements BaseBean {

    public Long userId;
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
    // 头像FileResAo (核心：url，id)
    public FileResAo fileResAo = new FileResAo();
    // 默认设置为不是好友，所以状态是申请
    public AddUserStatusAo addUserStatusAo = new AddUserStatusAo();

}
