package com.czy.api.domain.Do.relationship;

import com.czy.api.constant.relationship.newUserGroup.ApplyStatusEnum;
import com.czy.api.constant.relationship.newUserGroup.HandleStatusEnum;
import json.BaseBean;
import lombok.Data;

/**
 * @author 13225
 * @date 2025/2/25 11:53
 * @see ApplyStatusEnum 关于applyStatus
 * @see HandleStatusEnum 关于handleStatus
 */

@Data
public class FriendApplyDo implements BaseBean {
    // 主键
    public Long id;
    // 申请用户ID
    public Long applyUserId;
    // 处理用户ID
    public Long handleUserId;
    // 申请时间 (时间戳)
    public Long applyTime;
    // 处理时间 (时间戳，可以为空)
    public Long handleTime;
    // 申请来源 (根据需要添加)
    public Integer source;
    // 聊天列表 (JSON 格式)
    public String chatList;
    // 申请状态 (0:未申请 1:申请中 2:已处理)
    public Integer applyStatus;
    // 处理状态 (0:未处理 1:同意 2:拒绝 3:拉黑)
    public Integer handleStatus;
    // 是否被拉黑
    public boolean isBlack;
}
