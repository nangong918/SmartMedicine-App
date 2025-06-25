package com.czy.api.domain.dto.base;


import json.BaseBean;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author 13225
 * @date 2025/2/8 19:04
 */
@Data
//@Slf4j    // 对象类型禁止使用@Slf4j 创建Logger会消耗很多资源
public class BaseRequestData implements BaseBean {

    @NotNull(message = "发送者账号不能为空")
    public Long senderId;
    public Long receiverId;
    @NotNull(message = "请求类型不能为空")
    public String type;
    @NotEmpty(message = "时间戳不能为空")
    public String timestamp;

    // 提供给Json的无参构造器
    public BaseRequestData(){

    }

    public void setBaseRequestData(Long senderId, Long receiverId, String type, String timestamp){
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
