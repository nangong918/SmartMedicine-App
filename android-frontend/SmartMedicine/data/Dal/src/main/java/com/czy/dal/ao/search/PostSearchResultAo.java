package com.czy.dal.ao.search;


import com.czy.dal.vo.entity.home.PostVo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 13225
 * @date 2025/5/6 14:13
 * 1：0~1级 mysql like搜索结果
 * 2：2级 IK/Jieba分词 + elasticsearch搜索结果
 * 3：3级 AcTree实体命名识别 + Neo4j图临近相似实体搜索结果
 * 4：4级 user context特征向量 + Bert意图识别 + AcTree推荐搜索结果
 */
public class PostSearchResultAo {
    // like匹配结果
    public List<PostVo> likePostPreviewVoList = new ArrayList<>();
    // tokenized匹配结果
    public List<PostVo> tokenizedPostPreviewVoList = new ArrayList<>();
    // similar匹配结果
    public List<PostVo> similarPostPreviewVoList = new ArrayList<>();
    // recommend匹配结果 (上述全部无结果，然后：为您推荐)
    public List<PostVo> recommendPostPreviewVoList = new ArrayList<>();
}
