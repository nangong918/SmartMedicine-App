package com.czy.dal.dto.netty.request;

import com.czy.baseUtilsLib.json.BaseBean;
import com.czy.dal.constant.Constants;
import com.czy.dal.dto.netty.base.BaseTransferData;

public class HaveReadMessageRequest extends BaseTransferData implements BaseBean {
    public String receiverUserAccount;
    public HaveReadMessageRequest(){
        super();
        this.timestamp = String.valueOf(System.currentTimeMillis());
        this.receiverId = Constants.SERVER_ID;
    }

    public HaveReadMessageRequest(String haveBeenReadAccount){
        super();
        this.timestamp = String.valueOf(System.currentTimeMillis());
        this.receiverId = Constants.SERVER_ID;
        this.receiverUserAccount = haveBeenReadAccount;
    }
}
