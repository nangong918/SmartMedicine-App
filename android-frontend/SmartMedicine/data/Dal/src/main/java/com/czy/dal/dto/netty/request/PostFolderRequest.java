package com.czy.dal.dto.netty.request;


import com.czy.dal.constant.netty.NettyOptionEnum;
import com.czy.dal.constant.netty.RequestMessageType;
import com.czy.dal.dto.netty.base.NettyOptionRequest;

/**
 * @author 13225
 * @date 2025/4/23 11:00
 * 收藏帖子
 * senderId:user
 * receiverId:service
 */

public class PostFolderRequest extends NettyOptionRequest {

    // id
    public Long folderId;
    // name
    public String name;
    // newName
    public String newName;
    // option
    public int optionCode = NettyOptionEnum.NULL.getCode();

    public PostFolderRequest(String name, Integer optionCode){
        super(optionCode);
        super.setType(RequestMessageType.Post.COLLECT_FOLDER);
        this.name = name;
    }
}
