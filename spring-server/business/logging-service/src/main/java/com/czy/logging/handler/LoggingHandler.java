package com.czy.logging.handler;

import com.czy.api.api.user_relationship.UserService;
import com.czy.api.constant.netty.RequestMessageType;
import com.czy.api.domain.Do.user.UserDo;
import com.czy.api.domain.ao.feature.UserCityLocationInfoAo;
import com.czy.api.domain.dto.socket.request.UserBrowseTimeRequest;
import com.czy.api.domain.dto.socket.request.UserCityLocationRequest;
import com.czy.api.domain.dto.socket.request.UserClickPostRequest;
import com.czy.logging.handler.api.LoggingApi;
import com.czy.logging.service.UserActionRecordService;
import com.czy.springUtils.annotation.HandlerType;
import com.czy.springUtils.debug.DebugConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * @author 13225
 * @date 2025/5/23 16:51
 * TODO 改为存储hadoop hive; 最后再测试此部分，因为要改为hadoop hive
 */

@HandlerType(RequestMessageType.Logging.root)
@Slf4j
@RequiredArgsConstructor
@Component
public class LoggingHandler implements LoggingApi {

    @Reference(protocol = "dubbo", version = "1.0.0", check = false)
    private UserService userService;
    private final UserActionRecordService userActionRecordService;
    private final DebugConfig debugConfig;

    @Override
    public void uploadCityLocation(@Validated UserCityLocationRequest request) {
        if (!debugConfig.isRecordUserAccount()){
            return;
        }
        Long senderId = request.getSenderId();

        if (isUserNotExist(senderId)){
            log.warn("埋单事件失败::用户:{} 不存在", senderId);
            return;
        }

        UserCityLocationInfoAo ao = new UserCityLocationInfoAo();
        ao.setUserId(senderId);
        ao.setCityName(request.getCityName());
        ao.setLatitude(request.getLatitude());
        ao.setLongitude(request.getLongitude());
        try {
            Long timestamp = Long.valueOf(request.getTimestamp());
            userActionRecordService.uploadUserInfo(ao, timestamp);
        } catch (NumberFormatException e){
            log.error("上传城市信息埋点事件失败，时间戳格式错误：", e);
        }
    }

    @Override
    public void uploadClickEvent(@Validated UserClickPostRequest request) {
        if (!debugConfig.isRecordUserAccount()){
            return;
        }
        Long senderId = request.getSenderId();

        if (isUserNotExist(senderId)){
            log.warn("埋单事件失败::用户:{} 不存在", senderId);
            return;
        }
        try {
            Long timestamp = Long.valueOf(request.getTimestamp());
            userActionRecordService.clickPost(senderId, request.getPostId(), timestamp, timestamp);
        } catch (NumberFormatException e){
            log.error("上传点击事件埋点事件失败，时间戳格式错误：", e);
        }
    }

    @Override
    public void uploadBrowseEvent(@Validated UserBrowseTimeRequest request) {
        if (!debugConfig.isRecordUserAccount()){
            return;
        }
        Long senderId = request.getSenderId();

        if (isUserNotExist(senderId)){
            log.warn("埋单事件失败::用户:{} 不存在", senderId);
            return;
        }
        try {
            Long timestamp = Long.valueOf(request.getTimestamp());
            userActionRecordService.uploadClickPostAndBrowseTime(senderId, request.getPostId(), request.getBrowseDuration(), timestamp);
        } catch (NumberFormatException e){
            log.error("上传点击事件埋点事件失败，时间戳格式错误：", e);
        }
    }

    private boolean isUserNotExist(Long userId) {
        UserDo userDo = userService.getUserById(userId);
        return userDo == null || userDo.getId() == null;
    }

}
