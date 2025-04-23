package com.czy.api.domain.dto.socket.request;

import com.czy.api.constant.netty.RequestMessageType;
import com.czy.api.domain.dto.base.BaseRequestData;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author 13225
 * @date 2025/4/23 11:00
 * 收藏帖子
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PostCollectRequest extends BaseRequestData {

    // postId
    public Long postId;
    // 收藏夹Id
    public Long folderId;

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
