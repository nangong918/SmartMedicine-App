package com.czy.api.converter.socket;

import com.czy.api.converter.socket.request.AddUserReqConverter;
import com.czy.api.domain.dto.socket.request.AddUserRequest;
import com.czy.api.domain.entity.model.RequestBodyProto;
import org.junit.jupiter.api.Test;

/**
 * @author 13225
 * @date 2025/4/10 11:37
 */
class AddUserConverterTest {

    @Test
    void getRequest() {
        RequestBodyProto.RequestBody requestBody = RequestBodyProto.RequestBody.newBuilder()
                .setSenderId("senderId")
                .setReceiverId("receiverId")
                .setType("type")
                .putData("addUserAccount", "addUserAccount")
                .putData("myAccount", "myAccount")
                .putData("myName", "myName")
                .putData("addContent", "addContent")
                .putData("source", "1")
                .putData("applyType", "1")
                .setTimestamp(System.currentTimeMillis())
                .build();
        long start = System.currentTimeMillis();
        AddUserRequest addUserRequest = AddUserReqConverter.INSTANCE.getRequest(requestBody);
        long end = System.currentTimeMillis();
        System.out.println("耗时：" + (end - start) + "ms");
        System.out.println(addUserRequest.toJsonString());
    }
}