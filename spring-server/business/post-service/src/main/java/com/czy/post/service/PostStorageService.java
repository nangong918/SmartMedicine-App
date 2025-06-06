package com.czy.post.service;

import com.czy.api.domain.ao.post.PostAo;
import com.czy.api.domain.ao.post.PostInfoAo;
import com.czy.api.domain.ao.post.PostNerResult;

import java.util.List;

/**
 * @author 13225
 * @date 2025/4/18 18:29
 */
public interface PostStorageService {
    // es + mongo
    void storePostContentToDatabase(PostAo postAo);
    // mysql
    void storePostInfoToDatabase(PostAo postAo);
    void storePostFilesToDatabase(PostAo postAo);
    // 特征存储到neo4j
    void storePostFeatureToNeo4j(PostAo postAo, List<PostNerResult> featureList);

    void storePostAuthorRelationToNeo4j(PostAo postAo, Long userId);

    // 更新post的特征到neo4j
    void updatePostFeatureToNeo4j(PostAo postAo, List<PostNerResult> featureList);
    // 删除post在neo4j的全部特征
    void deletePostFeatureFromNeo4j(Long postId);
    // 删除es + mongo
    void deletePostContentFromDatabase(Long id);
    // 删除mysql
    void deletePostInfoFromDatabase(Long id);
    
    // 获取PostAo
    PostAo findPostAoById(Long id);

    List<PostAo> findPostAoByIds(List<Long> idList);
    List<PostInfoAo> findPostInfoAoList(List<Long> idList);

    void updatePostContentToDatabase(PostAo postAo);

    void updatePostInfoToDatabase(PostAo postAo);
    void updatePostFilesToDatabase(PostAo postAo);
    // 通过authorId和title找到postId
    Long findPostIdByAuthorIdAndTitle(Long authorId, String title);
}
