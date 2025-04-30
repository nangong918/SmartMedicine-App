package com.czy.api.converter.socket.response;

import com.czy.api.domain.dto.base.BaseResponseData;
import com.czy.api.domain.dto.socket.response.HandleAddUserResponse;
import com.czy.api.domain.entity.event.Message;
import org.junit.jupiter.api.Test;

/**
 * @author 13225
 * @date 2025/4/10 14:42
 */
class HandleAddUserResConverterTest {

    @Test
    void getMessage() {
        BaseResponseData baseResponseData = new BaseResponseData();
        baseResponseData.setSenderId("senderId");
        baseResponseData.setReceiverId("receiverId");
        baseResponseData.setType("type");
        baseResponseData.setTimestamp(String.valueOf(System.currentTimeMillis()));
        baseResponseData.setCode("200");
        baseResponseData.setMessage("message");

        HandleAddUserResponse handleAddUserResponse = new HandleAddUserResponse();
        handleAddUserResponse.setBaseResponseData(baseResponseData);
        handleAddUserResponse.setApplyStatus(1);
        handleAddUserResponse.setHandleStatus(1);
        handleAddUserResponse.setBlack(true);
        handleAddUserResponse.setHandlerAvatarFileId(null);
        handleAddUserResponse.setHandlerName("userName");
        handleAddUserResponse.setAdditionalContent("additionalContent");
        handleAddUserResponse.setApplyAccount("applyAccount");
        handleAddUserResponse.setHandlerAccount("handlerAccount");

        long start = System.currentTimeMillis();
        Message message = HandleAddUserResConverter.INSTANCE.getMessage(handleAddUserResponse);
        long end = System.currentTimeMillis();
        System.out.println("耗时：" + (end - start));
        System.out.println(message.toJsonString());
    }
}