package com.czy.dal.dto.netty.base;


import com.czy.dal.constant.netty.NettyOptionEnum;

/**
 * @author 13225
 * @date 2025/4/28 16:20
 */
public class NettyOptionRequest extends BaseRequestData{
    public int optionCode = NettyOptionEnum.NULL.getCode();
    public NettyOptionRequest(int optionCode){
        this.optionCode = optionCode;
    }
}
