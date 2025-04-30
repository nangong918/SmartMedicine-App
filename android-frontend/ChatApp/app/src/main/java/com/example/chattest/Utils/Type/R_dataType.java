package com.example.chattest.Utils.Type;


import com.alibaba.fastjson.JSONObject;

public class R_dataType {
    private Boolean flag = false;
    private Object data;


    public R_dataType(Object data) {
        this.flag = true;
        this.data = data;
    }

    public Object GetKeywordData(){
        JSONObject jsonData = new JSONObject((JSONObject)this.data);
        return jsonData.getJSONObject("data");
    }

    public R_dataType(JSONObject receivedJson){
        this.flag = receivedJson.getBoolean("flag");
        this.data = receivedJson;
    }

    public Boolean getFlag() {
        return flag;
    }

    public void setFlag(Boolean flag) {
        this.flag = flag;
    }

    public Object getData() {
        return data;
    }

    public void getByJson(JSONObject receivedJson){
        this.flag = receivedJson.getBoolean("flag");
        this.data = receivedJson.remove("flag");
    }

    public void setData(Object data) {
        this.data = data;
    }

    public R_dataType(Boolean flag) {
        this.flag = flag;
    }

}
