package com.czy.api.domain.dto.base;

import com.czy.api.constant.netty.NettyOptionEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 13225
 * @date 2025/4/28 16:20
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class NettyOptionResponse extends BaseResponseData{
    public int optionCode = NettyOptionEnum.NULL.getCode();

    @Override
    public Map<String, String> toDataMap() {
        Map<String, String> map = new HashMap<>();
        map.put("optionCode", String.valueOf(optionCode));
        return map;
    }
}
