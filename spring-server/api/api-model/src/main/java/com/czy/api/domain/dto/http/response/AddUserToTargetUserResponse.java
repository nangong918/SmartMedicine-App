package com.czy.api.domain.dto.http.response;



import com.czy.api.domain.dto.http.base.BaseHttpResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author 13225
 * @date 2025/2/19 17:34
 * 添加好友的响应 -> 被添加的用户
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AddUserToTargetUserResponse extends BaseHttpResponse {
    // 申请者名称
    public String appliedUserName;
    // 头像uri
    // 另外的策略，在响应体中设置url
    public String appliedUserAvatarUrl = null;
    // 添加附加内容
    public String appliedUserAddContent;
    // 添加时间
    public Long appliedUserAddTime;
    public Integer appliedUserSource;
    public String appliedUserChatList;
    public Integer appliedUserApplyStatus;
}
