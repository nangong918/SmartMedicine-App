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
    public String messageId;
    // 因为receiver现在是sender，所以receiverId不能用来记录接收消息的用户
    public Long receiveMyMessageUserId;
    public String receiverAccount;
    public String receiverName;

    @Override
    public Map<String, String> toDataMap(){
        Map<String, String> map = super.toDataMap();
        map.put("fileId", String.valueOf(fileId));
        map.put("messageId", messageId);
        map.put("receiverId", String.valueOf(receiverId));
        map.put("receiveMyMessageUserId", String.valueOf(receiveMyMessageUserId));
        map.put("receiverName", receiverName);
        return map;
    }
}
