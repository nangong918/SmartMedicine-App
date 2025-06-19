package com.czy.api.api.post;


import com.czy.api.domain.Do.post.post.PostDetailDo;
import com.czy.api.domain.ao.post.PostInfoAo;
import com.czy.api.domain.ao.post.PostInfoUrlAo;
import com.czy.api.domain.ao.post.PostSearchEsAo;
import com.czy.api.domain.vo.post.PostPreviewVo;
import com.czy.api.domain.vo.post.PostVo;

import java.util.List;

/**
 * @author 13225
 * @date 2025/5/6 11:45
 */
public interface PostSearchService {

    // 0~1级搜索：完全匹配~mysql like匹配（放一起是因为like能一起做了）
    List<Long> searchPostIdsByLikeTitle(String likeTitle);
    // 2级搜索：分词匹配（分词器：IK/jieba）+ ElasticSearch
    List<Long> searchPostIdsByTokenizedTitle(String tokenizedTitle);

    /**
     * 通过关键词list搜索
     * @param keywords
     * @return list的关键词搜索结果，包阔是那条一级匹配度
     */
    List<PostSearchEsAo> searchByKeywords(List<String> keywords);

    /**
     * 通过关键词list搜索
     * @param keywords          keyword list
     * @param minShouldMatch    最少匹配数
     * @return  list的关键词搜索结果，包阔是那条一级匹配度
     */
    List<PostSearchEsAo> searchByKeywordsByMapper(List<String> keywords, int minShouldMatch);

    List<String> searchBySimilarity(List<String> diseaseNames, int limitNum);
    // postIds -> PostInfoAos
    List<PostInfoAo> searchPostInfAoByIds(List<Long> postIds);

    PostDetailDo searchPostDetailById(Long postId);

    /**
     * 通过idList搜索PostInfoAoList
     * @param idList            idList
     * @return                  PostInfoAoList
     */
    List<PostInfoAo> findPostInfoList(List<Long> idList);

    /**
     * 获取帖子信息   返回包含下载链接的ao
     * @param postIds    帖子id
     * @return            帖子信息
     */
    List<PostInfoUrlAo> getPostInfoUrlAos(List<Long> postIds);

    // 对外提供转换方法
    List<PostPreviewVo> getPostPreviewVosByIds(List<Long> postIds);
    PostVo getPostVoById(Long postId);
}
