package com.czy.api.domain.dto.socket.response;

import com.czy.api.constant.netty.ResponseMessageType;
import com.czy.api.domain.dto.base.BaseResponseData;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

/**
 * @author 13225
 * @date 2025/4/23 10:51
 */
@Slf4j
@EqualsAndHashCode(callSuper = true)
@Data
public class NettyServerResponse extends BaseResponseData {

    public NettyServerResponse(String responseType){
        if (isResponseType(responseType)){
            return;
        }
        super.setType(responseType);
    }

    public NettyServerResponse(String responseType, String code, String message){
        if (isResponseType(responseType)){
            return;
        }
        super.setType(responseType);
        super.setCode(code);
        super.setMessage(message);
    }

    private boolean isResponseType(String responseType){
        boolean isResponseType = false;
        for (String responseTypes : ResponseMessageType.responseTypes){
            if (responseTypes.equals(responseType)){
                isResponseType = true;
                break;
            }
        }
        if (!isResponseType){
            log.warn("NettyServerResponse构建失败！responseType不存在");
        }
        return isResponseType;
    }

}
