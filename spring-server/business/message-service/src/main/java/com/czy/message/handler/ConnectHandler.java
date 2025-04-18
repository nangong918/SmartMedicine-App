package com.czy.message.handler;


import com.czy.api.constant.netty.RequestMessageType;
import com.czy.api.converter.http.SessionConverter;
import com.czy.api.domain.dto.http.request.DisconnectRequest;
import com.czy.api.domain.dto.http.request.RegisterRequest;
import com.czy.api.domain.entity.event.Session;
import com.czy.message.annotation.HandlerType;
import com.czy.message.handler.api.ConnectApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author 13225
 * @date 2025/3/10 15:14
 */
@HandlerType(RequestMessageType.Connect.root)
@Slf4j
@RequiredArgsConstructor
@Component
public class ConnectHandler implements ConnectApi {

    private final SessionConverter sessionConverter;

    @Override
    public void connect(RegisterRequest request) {
        Session session = new Session();
        session = sessionConverter.getSession(request);
//        String senderId = request.getSenderId();
//        session.setUid(senderId);
//        session.setDeviceId(request.getDeviceId());
//        session.setDeviceName(request.getDeviceName());
//        session.setAppVersion(request.getAppVersion());
//        session.setOsVersion(request.getOsVersion());
//        session.setLanguage(request.getLanguage());
//        session.setType(request.getType());

        // 只有分布式的情况下才需要存储session，用于多端登录，确定是否已经登录等功能
    }

    @Override
    public void disconnect(DisconnectRequest request) {
        // 取消某端登录的情况
    }
}
