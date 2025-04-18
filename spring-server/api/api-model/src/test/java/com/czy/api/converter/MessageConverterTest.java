package com.czy.api.converter;

import com.czy.api.converter.base.MessageConverter;
import com.czy.api.domain.dto.base.BaseRequestData;
import com.czy.api.domain.dto.socket.request.AddUserRequest;
import com.czy.api.domain.entity.event.Message;
import com.czy.api.domain.entity.model.RequestBodyProto;
import com.czy.api.domain.entity.model.ResponseBodyProto;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 13225
 * @date 2025/4/10 10:54
 */
class MessageConverterTest {

    @Test
    void requestBodyToMessage() {
        // 创建测试数据
        RequestBodyProto.RequestBody requestBody = RequestBodyProto.RequestBody.newBuilder()
                .setSenderId("user1")
                .setReceiverId("user2")
                .setType("text")
                .setTimestamp(System.currentTimeMillis())
                .putData("key1", "value1")
                .putData("key2", "value2")
                .build();
        long start = System.currentTimeMillis();
        Message message = MessageConverter.INSTANCE.requestBodyToMessage(requestBody);
        long end = System.currentTimeMillis();
        System.out.println("耗时：" + (end - start) + "ms");
        System.out.println(message.toJsonString());
    }

    // 耗时 3ms
    @Test
    void responseBodyToMessage() {
        ResponseBodyProto.ResponseBody.Builder builder = ResponseBodyProto.ResponseBody.newBuilder();
        builder.setCode("200");
        builder.setSenderId("user1");
        builder.setReceiverId("user2");
        builder.setType("text");
        builder.setTimestamp(System.currentTimeMillis());
        builder.putData("key1", "value1");
        builder.putData("key2", "value2");
        ResponseBodyProto.ResponseBody responseBody = builder.build();
        long start = System.currentTimeMillis();
        Message message = MessageConverter.INSTANCE.responseBodyToMessage(responseBody);
        long end = System.currentTimeMillis();
        System.out.println("耗时：" + (end - start) + "ms");
        System.out.println(message.toJsonString());
    }

    @Test
    void messageToResponseBody() {
        Message message = new Message();
        message.setSenderId("user1");
        message.setReceiverId("user2");
        message.setType("text");
        message.setTimestamp(System.currentTimeMillis());
        Map<String, String> data = new HashMap<>();
        for (int i = 0; i < 100; i++){
            data.put("key" + i, "value" + i);
        }
        message.setData(data);
        long start = System.currentTimeMillis();
        ResponseBodyProto.ResponseBody responseBody = MessageConverter.INSTANCE.messageToResponseBody(message);
        long end = System.currentTimeMillis();
        System.out.println("耗时：" + (end - start) + "ms");
        System.out.println(responseBody);
    }

    @Test
    void createObject(){
        long start = System.currentTimeMillis();
        Message message = new Message();
        long end = System.currentTimeMillis();
        System.out.println("message 耗时：" + (end - start) + "ms");
        long start1 = System.currentTimeMillis();
        BaseRequestData baseRequestData = new BaseRequestData();
        long end1 = System.currentTimeMillis();
        System.out.println("baseRequestData 耗时：" + (end1 - start1) + "ms");
        long start2 = System.currentTimeMillis();
        AddUserRequest addUserRequest = new AddUserRequest();
        long end2 = System.currentTimeMillis();
        System.out.println("addUserRequest 耗时：" + (end2 - start2) + "ms");
    }
}