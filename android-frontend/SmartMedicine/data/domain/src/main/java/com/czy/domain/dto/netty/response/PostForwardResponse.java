package com.czy.domain.dto.netty.response;


import com.czy.domain.constant.netty.ResponseMessageType;
import com.czy.domain.dto.netty.base.PostOperationBaseResponse;


/**
 * @author 13225
 * @date 2025/4/23 11:00
 * 转发帖子
 */
public class PostForwardResponse extends PostOperationBaseResponse {

    // 转发附带的话
    public String content;

    public PostForwardResponse(Long postId){
        super.setType(ResponseMessageType.Post.FORWARD_POST);
        this.postId = postId;
    }
}
