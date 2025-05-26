package com.czy.dal.dto.netty.request;

import com.czy.baseUtilsLib.json.BaseBean;
import com.czy.dal.dto.netty.base.BaseRequestData;

public class HandleAddedUserRequest extends BaseRequestData implements BaseBean {
    // 处理类型
    public Integer handleType;
    // 附加消息
    public String additionalContent;
}
