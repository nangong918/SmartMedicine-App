package com.czy.dal.dto.netty.response;


import com.czy.dal.constant.netty.ResponseMessageType;
import com.czy.dal.dto.netty.base.PostOperationBaseResponse;


/**
 * @author 13225
 * @date 2025/4/23 11:00
 * 点赞帖子
 */
public class PostLikeResponse extends PostOperationBaseResponse {

    public PostLikeResponse(Long postId){
        super.setType(ResponseMessageType.Post.LIKE_POST);
        this.postId = postId;
    }
}
