package com.czy.api.domain.dto.http.base;


import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import com.czy.api.domain.entity.event.Message;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 13225
 * @date 2025/2/19 17:25
 */
@Slf4j
@Data
public class BaseNettyResponse {
    public String code = "200";
    public String message = "";
    public String senderId = "";
    public String receiverId = "";
    public String type = "";
    public Long timestamp = System.currentTimeMillis();
    public Map<String, String> data = new HashMap<>();

    public BaseNettyResponse() {
    }

    public BaseNettyResponse(String senderId, String receiverId, String type) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.type = type;
    }

    public BaseNettyResponse(String code, String message, String senderId, String receiverId, String type) {
        this.code = code;
        this.message = message;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.type = type;
    }

    private void populateData() {
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (!"data".equals(field.getName()) &&
                    !"type".equals(field.getName()) &&
                    !"timestamp".equals(field.getName()) &&
                    !"code".equals(field.getName()) &&
                    !"message".equals(field.getName())) {
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
