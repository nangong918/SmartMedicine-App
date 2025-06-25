package com.czy.api.domain.dto.http.response;



import com.czy.api.constant.user_relationship.newUserGroup.ApplyStatusEnum;
import com.czy.api.domain.dto.http.base.BaseHttpResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CancelAddMeResponse extends BaseHttpResponse {
    public Integer isAgree = ApplyStatusEnum.NOT_APPLY.code;
    // 附加消息
    public String additionalContent;
    // user账号
    public String userAccount;
    // user名称
    public String userName;
    // 用户头像
    public Long avatarFileId;
}
