package com.czy.api.domain.dto.http.base;


import com.czy.api.domain.entity.event.Message;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 13225
 */
@Slf4j
@EqualsAndHashCode(callSuper = true)
@Data
public class BaseHttpRequest extends BaseRequest {

    @NotEmpty(message = "发送者 Account 不能为空")
    public Long senderId = -1L;
    public Long receiverId = -1L;
    public String type = "";
    @NotNull(message = "时间戳不能为空")
    @Positive(message = "时间戳必须大于零")
    public Long timestamp = System.currentTimeMillis();
    public Map<String, String> data = new HashMap<>();

    public BaseHttpRequest(){

    }

    public BaseHttpRequest(Long senderId, Long receiverId, String type){
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.type = type;
    }

    private void populateData() {
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (!"data".equals(field.getName()) &&
                    !"type".equals(field.getName()) &&
                    !"timestamp".equals(field.getName())) {
                try {
                    field.setAccessible(true);
                    Object value = field.get(this);
                    if (value != null) {
                        data.put(field.getName(), value.toString());
                    }
                } catch (Exception e) {
                    log.error("字段访问失败: {}", field.getName(), e);
                }
            }
        }
    }

    public Message getToMessage() {
        populateData();
        Message message = new Message();
        message.setSenderId(getSenderId());
        message.setReceiverId(getReceiverId());
        message.setType(getType());
        message.setTimestamp(getTimestamp());
        message.setData(getData());
        return message;
    }
}
