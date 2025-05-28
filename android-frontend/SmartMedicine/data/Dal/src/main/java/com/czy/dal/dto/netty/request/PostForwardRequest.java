package com.czy.dal.dto.netty.request;


import com.czy.dal.constant.netty.RequestMessageType;
import com.czy.dal.dto.netty.base.NettyOptionRequest;

/**
 * @author 13225
 * @date 2025/4/23 11:00
 * 转发帖子
 */

public class PostForwardRequest extends NettyOptionRequest {

    public Long postId;
    // receiverId (是Account不是id，因为前端为id无感知)
    // 对父的receiverId不信任因为其可能被传递为SERVER_ID所以要求前端再次传递。
    public String toUserAccount;
    // 转发附带的话
    public String content;

    public PostForwardRequest(Long postId, Integer optionCode){
        super(optionCode);
        super.setType(RequestMessageType.Post.FORWARD_POST);
        this.postId = postId;
    }
}
