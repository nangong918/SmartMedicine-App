package com.czy.api.domain.dto.socket.request;

import com.czy.api.constant.netty.NettyOptionEnum;
import com.czy.api.constant.netty.RequestMessageType;
import com.czy.api.domain.dto.base.NettyOptionRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author 13225
 * @date 2025/4/23 11:00
 * 收藏帖子
 * senderId:user
 * receiverId:service
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PostCollectRequest extends NettyOptionRequest {

    // postId
    public Long postId;
    // 收藏夹Id
    public Long folderId;
    // 新的收藏夹id
    public Long newFolderId;
    // option
    public int optionCode = NettyOptionEnum.NULL.getCode();

    public PostCollectRequest(Long postId, Long folderId){
        super.setType(RequestMessageType.Post.COLLECT_POST);
        this.postId = postId;
        if (folderId != null){
            this.folderId = folderId;
        }
        else {
            this.folderId = 0L;
        }
    }
}
