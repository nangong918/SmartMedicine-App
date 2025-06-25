package com.czy.api.domain.dto.socket.response;


import com.czy.api.domain.dto.base.BaseResponseData;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

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
    // user名称
    public String handlerName;
    // 用户头像
    public Long handlerAvatarFileId;

    @Override
    public Map<String, String> toDataMap() {
        Map<String, String> map = super.toDataMap();
        map.put("applyStatus", String.valueOf(applyStatus));
        map.put("handleStatus", String.valueOf(handleStatus));
        map.put("isBlack", String.valueOf(isBlack));
        map.put("applyAccount", applyAccount);
        map.put("handlerAccount", handlerAccount);

        map.put("additionalContent", additionalContent);
        map.put("handlerName", handlerName);
        map.put("handlerAvatarFileId", String.valueOf(handlerAvatarFileId));
        return map;
    }
}
