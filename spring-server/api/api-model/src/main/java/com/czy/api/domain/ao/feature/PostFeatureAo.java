package com.czy.api.domain.ao.feature;

import com.czy.api.constant.feature.PostTypeEnum;
import com.czy.api.domain.ao.post.PostNerResult;
import lombok.Data;

import java.util.List;

/**
 * @author 13225
 * @date 2025/5/10 13:51
 * Post的特征
 */
@Data
public class PostFeatureAo {
    private List<PostNerResult> postNerResultList;
    // 发表的时候审核识别分配
    private Integer postType = PostTypeEnum.OTHER.getCode();
}
