package com.czy.post.service.impl;

import com.czy.api.api.post.PostUserRelationService;
import com.czy.api.domain.Do.post.post.PostInfoDo;
import com.czy.api.domain.Do.post.post.UserPostBrowseDo;
import com.czy.post.mapper.mysql.PostInfoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author 13225
 * @date 2025/5/28 9:38
 */
@Slf4j
@RequiredArgsConstructor
@Service
@org.apache.dubbo.config.annotation.Service(protocol = "dubbo", version = "1.0.0")
public class PostUserRelationServiceImpl implements PostUserRelationService {

    private final PostInfoMapper postInfoMapper;

    @Override
    public Long queryPostAuthor(Long postId) {
        PostInfoDo postInfoDo = postInfoMapper.getPostInfoDoById(postId);
        return Optional.ofNullable(postInfoDo)
                .map(PostInfoDo::getAuthorId)
                .orElse(null);
    }

    @Override
    public List<Long> queryUserPosts(Long userId) {
        return postInfoMapper.getPostInfoDoListByAuthorId(userId);
    }

    @Override
    public void recordUserBrowsePost(Long userId, List<Long> postIds) {
        for (Long postId : postIds){
            UserPostBrowseDo userPostBrowseDo = new UserPostBrowseDo();
            userPostBrowseDo.setUserId(userId);
            userPostBrowseDo.setPostId(postId);
            userPostBrowseDo.setTimestamp(System.currentTimeMillis());
        }

        // Todo 添加到hive
    }

    @Override
    public List<Long> queryUserBrowsePost(Long userId) {
        // todo 从hive拿数据
        return new ArrayList<>();
    }

    @Override
    public List<Long> queryPostBrowseUser(Long postId) {
        // todo 从hive拿数据
        return new ArrayList<>();
    }
}
