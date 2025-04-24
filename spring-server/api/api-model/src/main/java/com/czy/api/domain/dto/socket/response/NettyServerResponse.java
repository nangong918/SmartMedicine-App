package com.czy.api.domain.dto.socket.response;

import com.czy.api.constant.netty.NettyConstants;
import com.czy.api.constant.netty.NettyResponseStatuesEnum;
import com.czy.api.constant.netty.ResponseMessageType;
import com.czy.api.domain.dto.base.BaseRequestData;
import com.czy.api.domain.dto.base.BaseResponseData;
import com.czy.api.domain.dto.http.base.BaseNettyRequest;
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
        super.setSenderId(NettyConstants.SERVER_ID);
    }
    public NettyServerResponse(@NotNull NettyResponseStatuesEnum responseStatuesEnum, BaseRequestData request){
        super.setType(responseStatuesEnum.getCode());
        super.setMessage(responseStatuesEnum.getMessage());
        super.setSenderId(NettyConstants.SERVER_ID);
        super.setReceiverId(request.getSenderId());
        super.setTimestamp(String.valueOf(request.getTimestamp()));
    }


}
