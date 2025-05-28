package com.czy.logging.mqHander.kafka;

import com.czy.api.api.feature.NlpService;
import com.czy.api.constant.feature.FeatureKafkaConstant;
import com.czy.api.constant.feature.PostOperation;
import com.czy.api.domain.ao.feature.CommentEmotionAo;
import com.czy.api.domain.entity.kafkaMessage.UserActionCommentPost;
import com.czy.api.domain.entity.kafkaMessage.UserActionOperatePost;
import com.czy.api.domain.entity.kafkaMessage.UserActionSearchPost;
import com.czy.logging.service.UserActionRecordService;
import com.czy.springUtils.debug.DebugConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

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
    @Reference(protocol = "dubbo", version = "1.0.0", check = false)
    private NlpService nlpService;

    @KafkaListener(topics = UserActionCommentPost.TOPIC,
    groupId = FeatureKafkaConstant.GROUP_ID + UserActionCommentPost.TOPIC)
    public void handleUserActionCommentPost(UserActionCommentPost message) {
        if (!debugConfig.isRecordUserAccount()){
            return;
        }
        if (!StringUtils.hasText(message.getComment())){
            return;
        }
        CommentEmotionAo result = nlpService.getCommentEmotion(message.getComment());
        userActionRecordService.commentPost(
                message.getUserId(),
                message.getPostId(),
                result.getCommentEmotionType(),
                result.getConfidenceLevel(),
                message.getTimestamp()
        );
    }

    @KafkaListener(topics = UserActionOperatePost.TOPIC,
    groupId = FeatureKafkaConstant.GROUP_ID + UserActionOperatePost.TOPIC)
    public void handleUserActionOperatePost(UserActionOperatePost message) {
        if (!debugConfig.isRecordUserAccount()){
            return;
        }
        if (PostOperation.NULL.getCode().equals(message.getOperateType())){
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
