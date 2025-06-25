package com.czy.api.converter.socket.response;

import com.czy.api.domain.dto.base.BaseResponseData;
import com.czy.api.domain.dto.socket.response.DeleteUserResponse;
import com.czy.api.domain.entity.event.Message;
import org.junit.jupiter.api.Test;


/**
 * @author 13225
 * @date 2025/4/10 14:30
 */
class DeleteUserResConverterTest {

    @Test
    void getMessage() {
        BaseResponseData baseResponseData = new BaseResponseData();
        baseResponseData.setSenderId(1L);
        baseResponseData.setReceiverId(2L);
        baseResponseData.setType("type");
        baseResponseData.setTimestamp(String.valueOf(System.currentTimeMillis()));
        baseResponseData.setCode("200");
        baseResponseData.setMessage("message");

        DeleteUserResponse deleteUserResponse = new DeleteUserResponse();
        deleteUserResponse.setBaseResponseData(baseResponseData);

        long startTime = System.currentTimeMillis();
        Message message = DeleteUserResConverter.INSTANCE.getMessage(deleteUserResponse);
        long endTime = System.currentTimeMillis();
        System.out.println("耗时：" + (endTime - startTime) + "ms");
        System.out.println(message.toJsonString());
    }
}