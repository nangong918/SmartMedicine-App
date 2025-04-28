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
public class PostFolderRequest extends NettyOptionRequest {

    // id
    public Long folderId;
    // name
    public String name;
    // newName
    public String newName;
    // option
    public int optionCode = NettyOptionEnum.NULL.getCode();

    public PostFolderRequest(String name){
        super.setType(RequestMessageType.Post.COLLECT_FOLDER);
        this.name = name;
    }
}
