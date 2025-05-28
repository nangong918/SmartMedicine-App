package com.czy.appcore.network.netty.event;


import com.czy.dal.model.ResponseBodyProto;

public interface OnReceiveMessage {
    void onReceiveMessage(ResponseBodyProto.ResponseBody message);
}
