package com.czy.api.domain.ao.search;

import com.czy.api.domain.vo.PostPreviewVo;
import lombok.Data;

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
@Data
public class PostSearchResultAo {
    // like匹配结果
    private List<PostPreviewVo> likePostPreviewVoList = new ArrayList<>();
    // tokenized匹配结果
    private List<PostPreviewVo> tokenizedPostPreviewVoList = new ArrayList<>();
    // similar匹配结果
    private List<PostPreviewVo> similarPostPreviewVoList = new ArrayList<>();
    // recommend匹配结果
    private List<PostPreviewVo> recommendPostPreviewVoList = new ArrayList<>();
}
