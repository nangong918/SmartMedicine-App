package com.czy.domain.ao.search;


import com.czy.domain.constant.search.PostSearchResultListEnum;
import com.czy.domain.vo.entity.home.PostPreviewExVo;
import com.czy.domain.vo.entity.home.PostPreviewVo;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    public List<PostPreviewVo> likePostPreviewVoList = new ArrayList<>();
    // tokenized匹配结果
    public List<PostPreviewVo> tokenizedPostPreviewVoList = new ArrayList<>();
    // similar匹配结果
    public List<PostPreviewVo> similarPostPreviewVoList = new ArrayList<>();
    // recommend匹配结果 (上述全部无结果，然后：为您推荐)
    public List<PostPreviewVo> recommendPostPreviewVoList = new ArrayList<>();

    public void clearAll(){
        this.likePostPreviewVoList.clear();
        this.tokenizedPostPreviewVoList.clear();
        this.similarPostPreviewVoList.clear();
        this.recommendPostPreviewVoList.clear();
    }

    public List<PostPreviewExVo> getPostExVoList(){
        List<PostPreviewExVo> allList = new ArrayList<>();
        // 使用 Stream API 将数据填充到 List<PostExVo>
        List<PostPreviewExVo> list1 = likePostPreviewVoList.stream()
                .map(postPreviewVo -> {
                    PostPreviewExVo postPreviewExVo = new PostPreviewExVo();
                    postPreviewExVo.setPostPreviewVo(postPreviewVo);
                    postPreviewExVo.type = PostSearchResultListEnum.LIKE_MATCH_RESULT.getValue();
                    return postPreviewExVo;
                })
                .collect(Collectors.toList());
        List<PostPreviewExVo> list2 = tokenizedPostPreviewVoList.stream()
                .map(postPreviewVo -> {
                    PostPreviewExVo postPreviewExVo = new PostPreviewExVo();
                    postPreviewExVo.setPostPreviewVo(postPreviewVo);
                    postPreviewExVo.type = PostSearchResultListEnum.TOKENIZED_MATCH_RESULT.getValue();
                    return postPreviewExVo;
                })
                .collect(Collectors.toList());
        List<PostPreviewExVo> list3 = similarPostPreviewVoList.stream()
                .map(postPreviewVo -> {
                    PostPreviewExVo postPreviewExVo = new PostPreviewExVo();
                    postPreviewExVo.setPostPreviewVo(postPreviewVo);
                    postPreviewExVo.type = PostSearchResultListEnum.SIMILAR_MATCH_RESULT.getValue();
                    return postPreviewExVo;
                })
                .collect(Collectors.toList());
        List<PostPreviewExVo> list4 = recommendPostPreviewVoList.stream()
                .map(postPreviewVo -> {
                    PostPreviewExVo postPreviewExVo = new PostPreviewExVo();
                    postPreviewExVo.setPostPreviewVo(postPreviewVo);
                    postPreviewExVo.type = PostSearchResultListEnum.RECOMMEND_MATCH_RESULT.getValue();
                    return postPreviewExVo;
                })
                .collect(Collectors.toList());

        allList.addAll(list1);
        allList.addAll(list2);
        allList.addAll(list3);
        allList.addAll(list4);
        return allList;
    }
}
