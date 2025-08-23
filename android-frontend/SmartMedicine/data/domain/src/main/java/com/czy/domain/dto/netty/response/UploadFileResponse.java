package com.czy.domain.dto.netty.response;


import com.czy.baseUtilLib.json.BaseBean;
import com.czy.domain.dto.netty.base.BaseResponseData;


/**
 * @author 13225
 * @date 2025/4/29 18:19
 */
public class UploadFileResponse extends BaseResponseData implements BaseBean {
    public Long fileId;
    public String messageId;
    // 因为receiver现在是sender，所以receiverId不能用来记录接收消息的用户
    public Long receiveMyMessageUserId;
    public String receiverAccount;
    public String receiverName;
}
