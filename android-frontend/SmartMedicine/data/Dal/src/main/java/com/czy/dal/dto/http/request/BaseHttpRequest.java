package com.czy.dal.dto.http.request;

import android.util.Log;

import com.czy.dal.constant.Constants;

public class BaseHttpRequest {
    public Long senderId = Constants.ERROR_ID;
    public Long receiverId = Constants.SERVER_ID;
    public String type = "";
    public Long timestamp = System.currentTimeMillis();

    public BaseHttpRequest(){

    }

    public BaseHttpRequest(Long senderId){
        this.senderId = senderId;
        Log.d("Intercept", "BaseNettyRequest: senderId: " + senderId);
    }

    public void setValueByOther(BaseHttpRequest request){
        this.senderId = request.senderId;
        this.receiverId = request.receiverId;
        this.type = request.type;
        this.timestamp = request.timestamp;
    }
}
