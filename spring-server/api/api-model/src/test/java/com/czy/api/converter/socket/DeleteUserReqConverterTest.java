package com.czy.api.converter.socket;

import com.czy.api.converter.socket.request.DeleteUserReqConverter;
import com.czy.api.domain.dto.socket.request.DeleteUserRequest;
import com.czy.api.domain.entity.model.RequestBodyProto;
import org.junit.jupiter.api.Test;

/**
 * @author 13225
 * @date 2025/4/10 14:00
 */
class DeleteUserReqConverterTest {

    @Test
    void getRequest() {
        RequestBodyProto.RequestBody requestBody = RequestBodyProto.RequestBody.newBuilder()
                .setSenderId(1L)
                .setReceiverId(2L)
                .setType("type")
                .putData("addUserAccount", "addUserAccount")
                .putData("myAccount", "myAccount")
                .putData("myName", "myName")
                .putData("addContent", "addContent")
                .putData("source", "1")
                .putData("applyType", "1")
                .setTimestamp(System.currentTimeMillis())
                .build();
        long startTime = System.currentTimeMillis();
        DeleteUserRequest request = DeleteUserReqConverter.INSTANCE.getRequest(requestBody);
        long endTime = System.currentTimeMillis();
        System.out.println("耗时：" + (endTime - startTime));
        System.out.println(request.toJsonString());
    }
}