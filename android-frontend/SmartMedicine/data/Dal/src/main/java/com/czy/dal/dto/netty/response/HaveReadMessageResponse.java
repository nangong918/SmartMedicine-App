package com.czy.dal.dto.netty.response;

import com.czy.baseUtilsLib.json.BaseBean;
import com.czy.dal.constant.NettyConstants;
import com.czy.dal.dto.netty.base.BaseResponseData;

public class HaveReadMessageResponse extends BaseResponseData implements BaseBean {
    // 读取的人的id
    public Long haveReadUserId;
    public HaveReadMessageResponse(){
        super();
        this.senderId = NettyConstants.SERVER_ID;
    }
}
