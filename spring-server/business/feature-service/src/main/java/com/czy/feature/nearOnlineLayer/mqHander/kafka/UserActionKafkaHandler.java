package com.czy.feature.nearOnlineLayer.mqHander.kafka;

import com.czy.api.constant.feature.FeatureKafkaConstant;
import com.czy.api.domain.entity.kafkaMessage.UserActionCommentPost;
import com.czy.api.domain.entity.kafkaMessage.UserActionOperatePost;
import com.czy.api.domain.entity.kafkaMessage.UserActionSearchPost;
import com.czy.feature.nearOnlineLayer.service.UserActionRecordService;
import com.czy.springUtils.debug.DebugConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * @author 13225
 * @date 2025/5/21 15:41
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class UserActionKafkaHandler {

    private final UserActionRecordService userActionRecordService;
    private final DebugConfig debugConfig;

    @KafkaListener(topics = UserActionCommentPost.TOPIC,
    groupId = FeatureKafkaConstant.GROUP_ID + UserActionCommentPost.TOPIC)
    public void handleUserActionCommentPost(UserActionCommentPost message) {
        if (!debugConfig.isRecordUserAccount()){
            return;
        }
        userActionRecordService.commentPost(
                message.getUserId(),
                message.getPostId(),
                message.getCommentEmotionType(),
                message.getConfidenceLevel(),
                message.getTimestamp()
        );
    }

    @KafkaListener(topics = UserActionOperatePost.TOPIC,
    groupId = FeatureKafkaConstant.GROUP_ID + UserActionOperatePost.TOPIC)
    public void handleUserActionOperatePost(UserActionOperatePost message) {
        if (!debugConfig.isRecordUserAccount()){
            return;
        }
        userActionRecordService.operatePost(
                message.getUserId(),
                message.getPostId(),
                message.getOperateType(),
                message.getTimestamp()
        );
    }

    @KafkaListener(topics = UserActionSearchPost.TOPIC,
    groupId = FeatureKafkaConstant.GROUP_ID + UserActionSearchPost.TOPIC)
    public void handleUserActionSearchPost(UserActionSearchPost message) {
        if (!debugConfig.isRecordUserAccount()){
            return;
        }
        userActionRecordService.searchPost(
                message.getUserId(),
                message.getLevelsPostIdMap(),
                message.getLevelsPostEntityScoreMap(),
                message.getLevelsPostLabelScoreMap(),
                message.getTimestamp()
        );
    }

}
