package com.czy.domain.dto.netty.request;

import com.czy.baseUtilsLib.json.BaseBean;
import com.czy.domain.constant.NettyConstants;
import com.czy.domain.dto.netty.base.BaseRequestData;

public class HaveReadMessageRequest extends BaseRequestData implements BaseBean {
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
