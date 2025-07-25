package com.czy.dal.dto.netty.response;

import com.czy.baseUtilsLib.json.BaseBean;
import com.czy.dal.constant.Constants;
import com.czy.dal.dto.netty.base.BaseResponseData;

public class HaveReadMessageResponse extends BaseResponseData implements BaseBean {
    // 我发送了消息，接收者已经把我消息读取了；receiverAccount是读取我消息的人
    public String receiverAccount;
    public HaveReadMessageResponse(){
        super();
        this.senderId = Constants.SERVER_ID;
    }
    public HaveReadMessageResponse(String receiverAccount, String timeStamp){
        super();
        this.senderId = Constants.SERVER_ID;
        this.receiverAccount = receiverAccount;
        this.timestamp = timeStamp;
    }
}
