package com.czy.dal.dto.netty.response;

import com.czy.baseUtilsLib.json.BaseBean;
import com.czy.dal.constant.Constants;
import com.czy.dal.dto.netty.base.BaseResponseData;

public class HaveReadMessageResponse extends BaseResponseData implements BaseBean {
    public String senderAccount;
    public HaveReadMessageResponse(){
        super();
        this.senderId = Constants.SERVER_ID;
    }
    public HaveReadMessageResponse(String senderAccount, String timeStamp){
        super();
        this.senderId = Constants.SERVER_ID;
        this.senderAccount = senderAccount;
        this.timestamp = timeStamp;
    }
}
