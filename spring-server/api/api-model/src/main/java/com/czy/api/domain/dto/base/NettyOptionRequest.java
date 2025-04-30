package com.czy.api.domain.dto.base;

import com.czy.api.constant.netty.NettyOptionEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author 13225
 * @date 2025/4/28 16:20
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class NettyOptionRequest extends BaseRequestData{
    public int optionCode = NettyOptionEnum.NULL.getCode();
}
