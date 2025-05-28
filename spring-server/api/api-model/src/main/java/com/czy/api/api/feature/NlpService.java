package com.czy.api.api.feature;

import com.czy.api.domain.ao.feature.CommentEmotionAo;

/**
 * @author 13225
 * @date 2025/5/21 17:20
 */
public interface NlpService {

    // 文本情感判断 TODO Python代码
    CommentEmotionAo getCommentEmotion(String comment);
}
