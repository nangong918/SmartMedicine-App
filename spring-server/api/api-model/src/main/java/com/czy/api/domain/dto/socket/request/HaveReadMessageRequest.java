package com.czy.api.domain.dto.socket.request;


import com.czy.api.constant.netty.NettyConstants;
import com.czy.api.domain.dto.base.BaseRequestData;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class HaveReadMessageRequest extends BaseRequestData {
    // receiverId是SERVER_ID；receiverUserId是接收USER_ID
    public Long receiverUserId;
    public HaveReadMessageRequest(){
        super();
        this.timestamp = String.valueOf(System.currentTimeMillis());
        this.receiverId = NettyConstants.SERVER_ID;
    }

    public HaveReadMessageRequest(Long haveBeenReadUserId){
        super();
        this.timestamp = String.valueOf(System.currentTimeMillis());
        this.receiverId = NettyConstants.SERVER_ID;
        this.receiverUserId = haveBeenReadUserId;
    }
}
