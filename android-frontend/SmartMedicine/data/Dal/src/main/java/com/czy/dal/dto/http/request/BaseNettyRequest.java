package com.czy.dal.dto.http.request;

import android.util.Log;

import com.czy.dal.constant.Constants;

public class BaseNettyRequest extends BaseRequest{
    public String senderId = "";
    public String receiverId = Constants.SERVER_ID;
    public String type = "";
    public Long timestamp = System.currentTimeMillis();

    public BaseNettyRequest(){

    }

    public BaseNettyRequest(String senderId){
        this.senderId = senderId;
        Log.d("Intercept", "BaseNettyRequest: senderId: " + senderId);
    }

    public void setValueByOther(BaseNettyRequest request){
        this.senderId = request.senderId;
        this.receiverId = request.receiverId;
        this.type = request.type;
        this.timestamp = request.timestamp;
    }
}
