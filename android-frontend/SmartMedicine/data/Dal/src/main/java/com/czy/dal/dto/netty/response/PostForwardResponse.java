package com.czy.dal.dto.netty.response;


import com.czy.dal.constant.netty.ResponseMessageType;
import com.czy.dal.dto.netty.base.NettyOptionResponse;


/**
 * @author 13225
 * @date 2025/4/23 11:00
 * 转发帖子
 */
public class PostForwardResponse extends NettyOptionResponse {

    public Long postId;
    // 其实是，receiverId因为就是通知发送者
    public Long senderId;
    // 转发附带的话
    public String content;

    public PostForwardResponse(Long postId){
        super.setType(ResponseMessageType.Post.FORWARD_POST);
        this.postId = postId;
    }

}
