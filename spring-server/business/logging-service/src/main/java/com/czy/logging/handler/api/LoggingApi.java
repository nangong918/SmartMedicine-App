package com.czy.logging.handler.api;


import com.czy.api.constant.netty.RequestMessageType;
import com.czy.api.domain.dto.socket.request.UserBrowseTimeRequest;
import com.czy.api.domain.dto.socket.request.UserCityLocationRequest;
import com.czy.api.domain.dto.socket.request.UserClickPostRequest;
import com.czy.springUtils.annotation.MessageType;


/**
 * @author 13225
 * @date 2025/3/10 17:11
 */
public interface LoggingApi {

    @MessageType(value = RequestMessageType.Logging.LOGGING_LOCATION, desc = "城市地区事件")
    void uploadCityLocation(UserCityLocationRequest request);

    @MessageType(value = RequestMessageType.Logging.LOGGING_CLICK, desc = "点击事件")
    void uploadClickEvent(UserClickPostRequest request);

    @MessageType(value = RequestMessageType.Logging.LOGGING_BROWSE, desc = "浏览事件")
    void uploadBrowseEvent(UserBrowseTimeRequest request);
}
