package com.czy.api.converter.base;

import com.czy.api.domain.dto.base.BaseRequestData;
import com.czy.api.domain.entity.model.RequestBodyProto;
import org.junit.jupiter.api.Test;

import java.util.Map;


/**
 * @author 13225
 * @date 2025/4/10 14:22
 */
class BaseRequestConverterTest {

    @Test
    void getBaseRequestData() {
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
        long startTime = System.currentTimeMillis();
        BaseRequestData requestData = BaseRequestConverter.INSTANCE.getBaseRequestData(requestBody);
        long endTime = System.currentTimeMillis();
        System.out.println("耗时：" + (endTime - startTime));
        System.out.println(requestData.toJsonString());
    }

    // 证明反射存在问题的方法
    @Test
    void getDataMap() {
        BaseRequestData baseRequestData = new BaseRequestData();
        baseRequestData.setSenderId("senderId");
        baseRequestData.setReceiverId("receiverId");
        baseRequestData.setType("type");
        baseRequestData.setTimestamp(String.valueOf(System.currentTimeMillis()));

        long startTime = System.currentTimeMillis();
        Map<String, String> headers = BaseRequestConverter.INSTANCE.dataMap(baseRequestData);
        long endTime = System.currentTimeMillis();

        System.out.println("耗时：" + (endTime - startTime) + "ms");
        System.out.println(headers);
    }
}