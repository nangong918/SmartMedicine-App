package com.czy.appcore.network.netty.event;


import com.czy.domain.model.ResponseBodyProto;

public interface OnReceiveMessage {
    void onReceiveMessage(ResponseBodyProto.ResponseBody message);
}
