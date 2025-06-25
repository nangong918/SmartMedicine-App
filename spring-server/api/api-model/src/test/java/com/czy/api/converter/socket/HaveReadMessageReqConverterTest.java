package com.czy.api.converter.socket;

import com.czy.api.converter.socket.request.HaveReadMessageReqConverter;
import com.czy.api.domain.dto.socket.request.HaveReadMessageRequest;
import com.czy.api.domain.entity.model.RequestBodyProto;
import org.junit.jupiter.api.Test;

/**
 * @author 13225
 * @date 2025/4/10 14:05
 */
class HaveReadMessageReqConverterTest {

    @Test
    void getRequest() {
        RequestBodyProto.RequestBody requestBody = RequestBodyProto.RequestBody.newBuilder()
                .setSenderId(1L)
                .setReceiverId(2L)
                .setType("type")
                .putData("receiverUserAccount", "receiverUserAccount")
                .setTimestamp(System.currentTimeMillis())
                .build();
        long startTime = System.currentTimeMillis();
        HaveReadMessageRequest request = HaveReadMessageReqConverter.INSTANCE.getRequest(requestBody);
        long endTime = System.currentTimeMillis();
        System.out.println("HaveReadMessageConverter.getRequest cost time: " + (endTime - startTime));
        System.out.println(request.toJsonString());
    }
}