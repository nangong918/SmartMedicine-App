package com.czy.post.service.impl;

import com.czy.api.api.post.PostNerService;
import com.czy.api.domain.ao.post.PostNerResult;
import com.czy.post.component.NerAcTree;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author 13225
 * @date 2025/5/5 17:22
 */
@Slf4j
@RequiredArgsConstructor
@org.apache.dubbo.config.annotation.Service(protocol = "dubbo", version = "1.0.0")
public class PostNerServiceImpl implements PostNerService {

    private final NerAcTree nerAcTree;

    @Override
    public List<PostNerResult> getPostNerResults(String postTitle) {
        return nerAcTree.getPostNerResults(postTitle);
    }
}
