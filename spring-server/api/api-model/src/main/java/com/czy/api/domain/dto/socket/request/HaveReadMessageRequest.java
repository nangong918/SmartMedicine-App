package com.czy.api.domain.dto.socket.request;


import com.czy.api.constant.netty.NettyConstants;
import com.czy.api.domain.dto.base.BaseRequestData;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class HaveReadMessageRequest extends BaseRequestData {
    public String receiverUserAccount;
    public HaveReadMessageRequest(){
        super();
        this.timestamp = String.valueOf(System.currentTimeMillis());
        this.receiverId = NettyConstants.SERVER_ID;
    }

    public HaveReadMessageRequest(String haveBeenReadAccount){
        super();
        this.timestamp = String.valueOf(System.currentTimeMillis());
        this.receiverId = NettyConstants.SERVER_ID;
        this.receiverUserAccount = haveBeenReadAccount;
    }
}
