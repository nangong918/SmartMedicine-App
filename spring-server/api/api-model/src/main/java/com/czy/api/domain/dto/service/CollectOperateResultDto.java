package com.czy.api.domain.dto.service;

import com.czy.api.constant.netty.NettyOptionEnum;
import lombok.Data;

/**
 * @author 13225
 * @date 2025/7/28 18:31
 */
@Data
public class CollectOperateResultDto {
    private boolean isSuccess = false;
    private Long collectFolderId;
    private NettyOptionEnum optionEnum = NettyOptionEnum.NULL;

    public CollectOperateResultDto(){

    }
    public CollectOperateResultDto(NettyOptionEnum optionEnum){
        this.optionEnum = optionEnum;
    }

    public CollectOperateResultDto(NettyOptionEnum optionEnum, boolean isSuccess){
        this.optionEnum = optionEnum;
        this.isSuccess = isSuccess;
    }

    public CollectOperateResultDto(Long collectFolderId){
        this.isSuccess = true;
        this.collectFolderId = collectFolderId;
        this.optionEnum = NettyOptionEnum.ADD;
    }
}
