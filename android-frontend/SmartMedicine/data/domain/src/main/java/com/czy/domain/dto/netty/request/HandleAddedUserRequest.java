package com.czy.domain.dto.netty.request;

import com.czy.baseUtilLib.json.BaseBean;
import com.czy.domain.dto.netty.base.BaseRequestData;

public class HandleAddedUserRequest extends BaseRequestData implements BaseBean {
    // 处理类型
    public Integer handleType;
    // 附加消息
    public String additionalContent;
}
