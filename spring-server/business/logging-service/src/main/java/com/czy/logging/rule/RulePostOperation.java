package com.czy.logging.rule;

import com.czy.api.constant.feature.PostOperation;
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
            // 取消点赞 表示确实不喜欢，比点赞扣分更多
            -0.025,
            // 收藏，表示真的喜欢，权重高
            0.25,
            // 取消收藏，表示不想再看，权重很高
            -0.03,
            // 转发，权重中
            0.1,
            // 不喜欢的推荐，权重高
            -1.0,
            // 取消不喜欢的推荐，权重更高
            1.005,
    };

    /**
     * 帖子操作的评分规则
     * @param operateType   操作类型
     * @see com.czy.api.constant.feature.PostOperation
     * @return  评分
     */
    public double execute(Integer operateType){
        if (operateType == null || operateType.equals(PostOperation.NULL.getCode())){
            return 0.0;
        }
        else if (operateType >= PostOperation.LIKE.getCode() &&
        operateType <= PostOperation.CANCEL_NOT_INTERESTED.getCode()){
            return SCORE_MAP[operateType];
        }
        else {
            return 0.0;
        }
    }

}
