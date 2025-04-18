package com.czy.api.domain.dto.base;



import json.BaseBean;
import lombok.Data;

/**
 * @author 13225
 * @date 2025/2/8 19:04
 */
@Data
//@Slf4j    // 对象类型禁止使用@Slf4j 创建Logger会消耗很多资源
public class BaseRequestData implements BaseBean {

    public String senderId;
    public String receiverId;
    public String type;
    public String timestamp;

    public BaseRequestData(){

    }

    public void setBaseRequestData(String senderId, String receiverId, String type, String timestamp){
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.type = type;
        this.timestamp = timestamp;
    }

    public void setBaseRequestData(BaseRequestData baseRequestData){
        this.setBaseRequestData(
                baseRequestData.getSenderId(),
                baseRequestData.getReceiverId(),
                baseRequestData.getType(),
                baseRequestData.getTimestamp()
        );
    }
}
