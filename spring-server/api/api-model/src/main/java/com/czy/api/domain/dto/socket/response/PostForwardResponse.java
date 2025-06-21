package com.czy.api.domain.dto.socket.response;

import com.czy.api.constant.netty.ResponseMessageType;
import com.czy.api.domain.dto.base.NettyOptionResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

/**
 * @author 13225
 * @date 2025/4/23 11:00
 * 转发帖子
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PostForwardResponse extends NettyOptionResponse {

    public Long postId;
    // receiverId (是Account不是id，因为前端为id无感知)
    public String senderAccount;
    // 转发附带的话
    public String content;

    public PostForwardResponse(Long postId){
        super.setType(ResponseMessageType.Post.FORWARD_POST);
        this.postId = postId;
    }

    @Override
    public Map<String, String> toDataMap() {
        Map<String, String> map = super.toDataMap();
        map.put("postId", String.valueOf(postId));
        map.put("senderAccount", senderAccount);
        map.put("content", content);
        return map;
    }
}
