package com.czy.api.domain.dto.socket.response;


import com.czy.api.constant.netty.ResponseMessageType;
import com.czy.api.domain.dto.base.BaseResponseData;
import json.BaseBean;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

/**
 * @author 13225
 * @date 2025/2/8 19:03
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserImageResponse extends BaseResponseData implements BaseBean {
    public String title;
    public String imageUrl;
    public String senderName;
    // 当值不为空才更新
    public Long avatarFileId;
    public Long messageId;

    @Override
    public Map<String, String> toDataMap() {
        Map<String, String> map = super.toDataMap();
        map.put("title", title);
        map.put("imageUrl", imageUrl);
        map.put("senderName", senderName);
        map.put("avatarFileId", String.valueOf(avatarFileId));
        map.put("messageId", String.valueOf(messageId));
        return map;
    }

    public UserImageResponse(){
        super.setType(ResponseMessageType.Chat.RECEIVE_USER_IMAGE_MESSAGE);
    }
}
