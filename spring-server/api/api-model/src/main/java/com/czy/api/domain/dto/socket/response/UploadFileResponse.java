package com.czy.api.domain.dto.socket.response;

import com.czy.api.domain.dto.base.BaseResponseData;
import json.BaseBean;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

/**
 * @author 13225
 * @date 2025/4/29 18:19
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UploadFileResponse extends BaseResponseData implements BaseBean {
    public Long fileId;
    public Long messageId;
    public String receiverAccount;

    @Override
    public Map<String, String> toDataMap(){
        Map<String, String> map = super.toDataMap();
        map.put("fileId", String.valueOf(fileId));
        map.put("messageId", String.valueOf(messageId));
        map.put("receiverAccount", receiverAccount);
        return map;
    }
}
