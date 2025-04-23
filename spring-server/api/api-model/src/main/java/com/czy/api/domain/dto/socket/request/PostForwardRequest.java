package com.czy.api.domain.dto.socket.request;

import com.czy.api.constant.netty.RequestMessageType;
import com.czy.api.domain.dto.base.BaseRequestData;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author 13225
 * @date 2025/4/23 11:00
 * 转发帖子
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PostForwardRequest extends BaseRequestData {

    public Long postId;
    // receiverId (是Account不是id，因为前端为id无感知)
    public String toUserAccount;
    // 转发附带的话

    public PostForwardRequest(Long postId){
        super.setType(RequestMessageType.Post.LIKE_POST);
        this.postId = postId;
    }
}
