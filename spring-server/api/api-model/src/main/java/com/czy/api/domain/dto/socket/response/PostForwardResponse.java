package com.czy.api.domain.dto.socket.response;

import com.czy.api.constant.netty.ResponseMessageType;
import com.czy.api.domain.dto.base.BaseResponseData;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author 13225
 * @date 2025/4/23 11:00
 * 转发帖子
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PostForwardResponse extends BaseResponseData {

    public Long postId;
    // receiverId (是Account不是id，因为前端为id无感知)
    public String senderAccount;
    // 转发附带的话

    public PostForwardResponse(Long postId){
        super.setType(ResponseMessageType.Post.FORWARD_POST);
        this.postId = postId;
    }
}
