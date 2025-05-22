package com.czy.feature.rule;

import com.czy.api.constant.feature.CommentEmotionTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author 13225
 * @date 2025/5/13 11:13
 */
@Slf4j
@Component
public class RuleCommentPost {

    // 基本评论常数 double 0.1
    private final static double BASE_COMMENT_CONSTANT = 0.1;

    public double execute(Integer commentEmotionType, Double confidenceLevel) {
        // 评论情感类型为未知，则不进行评论加权
        if (commentEmotionType == null ||
                commentEmotionType.equals(CommentEmotionTypeEnum.UNKNOWN.getCode()) ||
                commentEmotionType.equals(CommentEmotionTypeEnum.NEUTRAL.getCode())
        ){
            return 0.0;
        }
        else if (commentEmotionType >= CommentEmotionTypeEnum.POSITIVE.getCode()){
            return BASE_COMMENT_CONSTANT * confidenceLevel;
        }
        else if (commentEmotionType >= CommentEmotionTypeEnum.NEGATIVE.getCode()){
            return -BASE_COMMENT_CONSTANT * confidenceLevel;
        }
        else {
            return 0.0;
        }
    }

}
