package com.czy.dal.dto.netty.base;

import com.czy.dal.constant.netty.NettyOptionEnum;

/**
 * @author 13225
 * @date 2025/4/28 16:20
 */

public class NettyOptionResponse extends BaseResponseData{
    public int optionCode = NettyOptionEnum.NULL.getCode();
}
