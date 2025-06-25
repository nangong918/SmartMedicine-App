package com.czy.api.domain.dto.socket.response;


import com.czy.api.constant.netty.NettyConstants;
import com.czy.api.domain.dto.base.BaseResponseData;
import json.BaseBean;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
public class HaveReadMessageResponse extends BaseResponseData implements BaseBean {
    // 我发送了消息，接收者已经把我消息读取了；receiverAccount是读取我消息的人
    public String receiverAccount;
    public HaveReadMessageResponse(){
        super();
        this.senderId = NettyConstants.SERVER_ID;
    }

    @Override
    public Map<String, String> toDataMap() {
        Map<String, String> map = super.toDataMap();
        map.put("receiverAccount", receiverAccount);
        return map;
    }
}
