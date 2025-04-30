package com.czy.api.domain.dto.socket.request;

import com.czy.api.constant.netty.RequestMessageType;
import com.czy.api.domain.dto.base.NettyOptionRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author 13225
 * @date 2025/4/23 11:00
 * 点赞帖子
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PostLikeRequest extends NettyOptionRequest {

    public Long postId;

    public PostLikeRequest(Long postId){
        super.setType(RequestMessageType.Post.LIKE_POST);
        this.postId = postId;
    }
}
