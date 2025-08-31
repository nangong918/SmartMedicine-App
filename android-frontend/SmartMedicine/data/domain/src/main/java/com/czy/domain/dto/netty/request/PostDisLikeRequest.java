package com.czy.domain.dto.netty.request;


import com.czy.domain.constant.netty.RequestMessageType;
import com.czy.domain.dto.netty.base.NettyOptionRequest;

/**
 * @author 13225
 * @date 2025/4/23 11:00
 * 点赞帖子
 */

public class PostDisLikeRequest extends NettyOptionRequest {

    public Long postId;

    public PostDisLikeRequest(Long postId, Integer optionCode){
        super(optionCode);
        super.setType(RequestMessageType.Post.LIKE_POST);
        this.postId = postId;
    }
}
