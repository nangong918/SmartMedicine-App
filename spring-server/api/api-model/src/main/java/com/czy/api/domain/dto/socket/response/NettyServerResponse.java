package com.czy.api.domain.dto.socket.response;

import com.czy.api.constant.netty.NettyResponseStatuesEnum;
import com.czy.api.constant.netty.ResponseMessageType;
import com.czy.api.domain.dto.base.BaseResponseData;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.NotNull;

/**
 * @author 13225
 * @date 2025/4/23 10:51
 */
@Slf4j
@EqualsAndHashCode(callSuper = true)
@Data
public class NettyServerResponse extends BaseResponseData {

    public NettyServerResponse(@NotNull NettyResponseStatuesEnum responseStatuesEnum){
        super.setType(responseStatuesEnum.getCode());
        super.setMessage(responseStatuesEnum.getMessage());
    }


}
