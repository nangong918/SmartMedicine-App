package com.czy.post.service.impl;

import cn.hutool.core.util.IdUtil;
import com.czy.api.api.post.PostImportService;
import com.czy.api.api.post.PostNerService;
import com.czy.api.domain.ao.post.PostAo;
import com.czy.api.domain.ao.post.PostNerResult;
import com.czy.post.service.PostStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 13225
 * @date 2025/6/4 14:43
 */
@Slf4j
@RequiredArgsConstructor
@Service
@org.apache.dubbo.config.annotation.Service(protocol = "dubbo", version = "1.0.0")
public class PostImportServiceImpl implements PostImportService {

    private final PostStorageService postStorageService;
    private final PostNerService postNerService;

    @Override
    public long importPost(String title, String content, Long publishTime, List<Long> fileIdList, Long userId){
        long postId = IdUtil.getSnowflakeNextId();
        PostAo postAo = new PostAo();
        postAo.setId(postId);
        postAo.setTitle(title);
        postAo.setContent(content);
        postAo.setReleaseTimestamp(publishTime);
        postAo.setFileIds(fileIdList);
        postAo.setAuthorId(userId);
        List<PostNerResult> resultList = postNerService.getPostNerResults(postAo.getTitle());
        postAo.setNerResults(resultList);

        // es + mongo 同步事务存储
        postStorageService.storePostContentToDatabase(postAo);
        // neo4j
        // post-ner
        postStorageService.storePostFeatureToNeo4j(postAo, postAo.getNerResults());
        // user-publish-post
        postStorageService.storePostAuthorRelationToNeo4j(postAo, userId);
        // mysql
        postStorageService.storePostInfoToDatabase(postAo);
        // files
        postStorageService.storePostFilesToDatabase(postAo);
        return postId;
    }

}
