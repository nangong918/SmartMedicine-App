package com.czy.dal.ao.search;


import com.czy.dal.constant.search.PostSearchResultListEnum;
import com.czy.dal.vo.entity.home.PostExVo;
import com.czy.dal.vo.entity.home.PostVo;

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
    public List<PostVo> likePostPreviewVoList = new ArrayList<>();
    // tokenized匹配结果
    public List<PostVo> tokenizedPostPreviewVoList = new ArrayList<>();
    // similar匹配结果
    public List<PostVo> similarPostPreviewVoList = new ArrayList<>();
    // recommend匹配结果 (上述全部无结果，然后：为您推荐)
    public List<PostVo> recommendPostPreviewVoList = new ArrayList<>();

    public void clearAll(){
        this.likePostPreviewVoList.clear();
        this.tokenizedPostPreviewVoList.clear();
        this.similarPostPreviewVoList.clear();
        this.recommendPostPreviewVoList.clear();
    }

    public List<PostExVo> getPostExVoList(){
        List<PostExVo> allList = new ArrayList<>();
        // 使用 Stream API 将数据填充到 List<PostExVo>
        List<PostExVo> list1 = likePostPreviewVoList.stream()
                .map(postVo -> {
                    PostExVo postExVo = new PostExVo();
                    postExVo.setByPostVo(postVo);
                    postExVo.type = PostSearchResultListEnum.LIKE_MATCH_RESULT.getValue();
                    return postExVo;
                })
                .collect(Collectors.toList());
        List<PostExVo> list2 = tokenizedPostPreviewVoList.stream()
                .map(postVo -> {
                    PostExVo postExVo = new PostExVo();
                    postExVo.setByPostVo(postVo);
                    postExVo.type = PostSearchResultListEnum.TOKENIZED_MATCH_RESULT.getValue();
                    return postExVo;
                })
                .collect(Collectors.toList());
        List<PostExVo> list3 = similarPostPreviewVoList.stream()
                .map(postVo -> {
                    PostExVo postExVo = new PostExVo();
                    postExVo.setByPostVo(postVo);
                    postExVo.type = PostSearchResultListEnum.SIMILAR_MATCH_RESULT.getValue();
                    return postExVo;
                })
                .collect(Collectors.toList());
        List<PostExVo> list4 = recommendPostPreviewVoList.stream()
                .map(postVo -> {
                    PostExVo postExVo = new PostExVo();
                    postExVo.setByPostVo(postVo);
                    postExVo.type = PostSearchResultListEnum.RECOMMEND_MATCH_RESULT.getValue();
                    return postExVo;
                })
                .collect(Collectors.toList());

        allList.addAll(list1);
        allList.addAll(list2);
        allList.addAll(list3);
        allList.addAll(list4);
        return allList;
    }
}
