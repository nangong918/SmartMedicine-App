package com.czy.api.domain.dto.socket.response;


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
public class UserTextDataResponse extends BaseResponseData implements BaseBean {
    private String title;
    private String content;
    public String senderName;
    public String avatarUrls;

    @Override
    public Map<String, String> toDataMap() {
        Map<String, String> data = super.toDataMap();
        data.put("title", title);
        data.put("content", content);
        data.put("senderName", senderName);
        data.put("avatarFileId", avatarUrls);
        return data;
    }
}
