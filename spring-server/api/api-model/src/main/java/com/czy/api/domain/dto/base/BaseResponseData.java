package com.czy.api.domain.dto.base;

import com.czy.api.constant.netty.NettyResponseStatuesEnum;
import com.czy.api.converter.base.BaseResponseConverter;
import com.czy.api.domain.entity.event.Message;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * @author 13225
 * @date 2025/2/11 23:33
 */

@EqualsAndHashCode(callSuper = true)
@Data
public class BaseResponseData extends BaseRequestData {
    public String code = NettyResponseStatuesEnum.SUCCESS.getCode();
    public String message = NettyResponseStatuesEnum.SUCCESS.getMessage();

    public BaseResponseData(){

    }

    public BaseResponseData(BaseResponseData baseResponseData){
        this.setBaseResponseData(
                baseResponseData.getCode(),
                baseResponseData.getMessage(),
                baseResponseData.getSenderId(),
                baseResponseData.getReceiverId(),
                baseResponseData.getType(),
                baseResponseData.getTimestamp()
        );
    }

    public void setBaseResponseData(BaseResponseData baseResponseData){
        this.setBaseResponseData(
                baseResponseData.getCode(),
                baseResponseData.getMessage(),
                baseResponseData.getSenderId(),
                baseResponseData.getReceiverId(),
                baseResponseData.getType(),
                baseResponseData.getTimestamp()
        );
    }

    public void setBaseResponseData(String code, String message,
                            String senderId, String receiverId, String type, String timestamp){
        this.code = Optional.ofNullable(code).orElse("");
        this.message = Optional.ofNullable(message).orElse("");
        this.setSenderId(senderId);
        this.setReceiverId(receiverId);
        this.setType(type);
        this.setTimestamp(timestamp);
    }

    public void initResponseByRequest(@NotNull BaseRequestData request){
        this.setSenderId(request.getSenderId());
        this.setReceiverId(request.getReceiverId());
        // 转化在Push中转化
        this.setType(request.getType());
        this.setTimestamp(request.getTimestamp());
    }

    // 导出为Message的方法，需要JsonIgnore，否则会序列化错误
    @JsonIgnore
    public Message getMessageByResponse() {
        return BaseResponseConverter.INSTANCE.getMessage(this);
    }
}
