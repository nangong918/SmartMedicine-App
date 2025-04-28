package com.czy.api.domain.dto.socket.response;

import com.czy.api.constant.netty.RequestMessageType;
import com.czy.api.constant.netty.ResponseMessageType;
import com.czy.api.domain.dto.base.NettyOptionRequest;
import com.czy.api.domain.dto.base.NettyOptionResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author 13225
 * @date 2025/4/23 11:00
 * 点赞帖子
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PostLikeResponse extends NettyOptionResponse {

    public Long postId;
    public String likeUserAccount;

    public PostLikeResponse(Long postId){
        super.setType(ResponseMessageType.Post.LIKE_POST);
        this.postId = postId;
    }
}
