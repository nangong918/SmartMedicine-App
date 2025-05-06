package com.czy.post.service.impl;

import com.czy.api.api.post.PostSearchService;
import com.czy.post.mapper.mysql.PostInfoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 13225
 * @date 2025/5/6 11:47
 */
@Slf4j
@RequiredArgsConstructor
@org.apache.dubbo.config.annotation.Service(protocol = "dubbo", version = "1.0.0")
public class PostSearchServiceImpl implements PostSearchService {

    private final PostInfoMapper postInfoMapper;

    @Override
    public List<Long> searchPostIdsByLikeTitle(String likeTitle) {
        if (!StringUtils.hasText(likeTitle)){
            return new ArrayList<>();
        }
        List<Long> postIds = postInfoMapper.findPostIdByLikeTitle(likeTitle);
        if (CollectionUtils.isEmpty(postIds)){
            return new ArrayList<>();
        }
        return postIds;
    }
}
