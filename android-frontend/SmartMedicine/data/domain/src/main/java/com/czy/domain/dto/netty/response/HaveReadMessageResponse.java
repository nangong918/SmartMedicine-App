package com.czy.domain.dto.netty.response;

import com.czy.baseUtilsLib.json.BaseBean;
import com.czy.domain.constant.NettyConstants;
import com.czy.domain.dto.netty.base.BaseResponseData;

public class HaveReadMessageResponse extends BaseResponseData implements BaseBean {
    // 读取的人的id
    public Long haveReadUserId;
    public HaveReadMessageResponse(){
        super();
        this.senderId = NettyConstants.SERVER_ID;
    }
}
