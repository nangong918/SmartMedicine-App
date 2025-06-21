package com.czy.api.domain.dto.socket.response;

import com.czy.api.constant.netty.ResponseMessageType;
import com.czy.api.domain.dto.base.NettyOptionResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

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

    @Override
    public Map<String, String> toDataMap() {
        Map<String, String> dataMap = super.toDataMap();
        dataMap.put("postId", String.valueOf(postId));
        dataMap.put("likeUserAccount", likeUserAccount);
        return dataMap;
    }
}
