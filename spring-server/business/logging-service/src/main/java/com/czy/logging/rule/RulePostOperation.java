package com.czy.logging.rule;

import com.czy.api.constant.feature.PostOperateTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author 13225
 * @date 2025/5/13 9:45
 */
@Slf4j
@Component
public class RulePostOperation {

    private static final Double[] SCORE_MAP = new Double[]{
            // 未知
            0.0,
            // 点赞 点赞不会放在收藏夹，权重低
            0.02,
            // 收藏，表示真的喜欢，权重高
            0.25,
            // 转发，权重中
            0.1,
            // 取消点赞 表示确实不喜欢，比点赞扣分更多
            -0.025,
            // 取消收藏，表示不想再看，权重很高
            -0.03
    };

    /**
     * 帖子操作的评分规则
     * @param operateType   操作类型
     * @see com.czy.api.constant.feature.PostOperateTypeEnum
     * @return  评分
     */
    public double execute(Integer operateType){
        if (operateType == null || operateType.equals(PostOperateTypeEnum.UNKNOWN.getCode())){
            return 0.0;
        }
        else if (operateType >= PostOperateTypeEnum.LIKE.getCode() &&
        operateType <= PostOperateTypeEnum.CANCEL_COLLECT.getCode()){
            return SCORE_MAP[operateType];
        }
        else {
            return 0.0;
        }
    }

}
