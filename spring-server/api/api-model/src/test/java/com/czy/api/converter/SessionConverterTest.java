package com.czy.api.converter;

import com.czy.api.converter.http.SessionConverter;
import com.czy.api.domain.dto.http.request.RegisterRequest;
import com.czy.api.domain.entity.event.Session;
import org.junit.jupiter.api.Test;

/**
 * @author 13225
 * @date 2025/4/10 11:14
 */
class SessionConverterTest {

    @Test
    void getSession() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setBaseRequestData("13225", "13225", "connect", String.valueOf(System.currentTimeMillis()));
        registerRequest.setDeviceId("123456");
        registerRequest.setDeviceName("123456");
        registerRequest.setAppVersion("123456");
        registerRequest.setOsVersion("123456");
        registerRequest.setPackageName("123456");
        registerRequest.setLanguage("123456");
        registerRequest.setUuid("123456");
        long startTime = System.currentTimeMillis();
        Session session = SessionConverter.INSTANCE.getSession(registerRequest);
        long endTime = System.currentTimeMillis();
        System.out.println("耗时：" + (endTime - startTime));
        System.out.println(session);
    }
}