package com.czy.api.domain.entity.kafkaMessage;

import com.czy.api.domain.ao.post.PostNerResult;
import com.czy.api.domain.entity.kafkaMessage.base.UserActionMessage;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 13225
 * @date 2025/5/21 15:45
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserActionSearchPost extends UserActionMessage {

    public static final String TOPIC = "USER_ACTION_SEARCH_POST";

    private Map<Integer, List<Long>> levelsPostIdMap = new HashMap<>();
    private Map<Integer, List<PostNerResult>> levelsPostEntityScoreMap = new HashMap<>();
    private Map<Integer, List<Integer>> levelsPostLabelScoreMap = new HashMap<>();
}
