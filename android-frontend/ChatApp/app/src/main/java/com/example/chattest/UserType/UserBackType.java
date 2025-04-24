package com.example.chattest.UserType;

import com.alibaba.fastjson.JSONObject;
import com.example.chattest.Utils.Type.R_dataType;

public class UserBackType {
    public int id;
    public byte[] imageBytes;
    public String name;
    public UserBackType(int id, byte[] imageBytes, String name) {
        this.id = id;
        this.imageBytes = imageBytes;
        this.name = name;
    }

    public UserBackType(){}

    public void getByRData(R_dataType rData){
        JSONObject ReceivedJson = (JSONObject) rData.getData();
        JSONObject dataObject = ReceivedJson.getJSONObject("data");
        this.imageBytes = dataObject.getBytes("imageBytes");
        this.id = dataObject.getInteger("id");
        this.name = dataObject.getString("name");
    }
}
