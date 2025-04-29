package com.czy.api.domain.dto.http.response;



import com.czy.api.domain.dto.http.base.BaseNettyResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author 13225
 * @date 2025/2/19 17:34
 * 添加好友的响应 -> 被添加的用户
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AddUserToTargetUserResponse extends BaseNettyResponse {
    public String AppliedUserAccount;
    public String AppliedUserName;
    // 头像uri
    public String AppliedUseravatarFileId;
    // 添加附加内容
    public String AppliedUserAddContent;
    // 添加时间
    public Long AppliedUserAddTime;
    public Integer AppliedUserSource;
    public String AppliedUserChatList;
    public Integer AppliedUserApplyStatus;
}
