package com.czy.post.mapper.service.impl;

import cn.hutool.core.util.IdUtil;
import com.czy.api.constant.post.PostConstant;
import com.czy.api.domain.ao.post.PostAo;
import com.czy.post.mapper.service.PostService;
import com.czy.post.mapper.service.PostStorageService;
import com.czy.springUtils.service.RedisManagerService;
import exception.AppException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 13225
 * @date 2025/4/18 17:39
 * TODO 整个Service待测试
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class PostServiceImpl implements PostService {

    private final RedisManagerService redisManagerService;
    private final PostStorageService postStorageService;
    private final ThreadPoolTaskExecutor globalTaskExecutor;

    /**
     * 发布消息是两个http请求：
     * 【此方法上游要进行限流】
     * 1.json数据发布
     * 2.文件资源上传
     * 所以对其拆分：
     * 1.前端将json发送给后端，由于两次http请求可能都不是一个服务处理的，所以数据需要缓存在redis
     * 2.后端将数据存储在redis并预生成mysql的雪花id并返回id通知前端进行oss上传
     * 3.前端拿到id之后执行将id带入执行oss上传。
     * 4.oss上传成功之后将id找到数据，异步执行[mysql][mongo + es]
     * 5.失败则删除数据和id，避免oss和数据库同时执行事务。
     * @param postAo    postAo
     * @return          boolean
     */
    @Override
    public Long releasePostFirst(@NonNull PostAo postAo) {
        // 由于两次http请求可能都不是一个服务处理的，所以数据需要缓存在redis
        // 生成发布的雪花id
        long publishId = IdUtil.getSnowflakeNextId();
        // redis的存储key是：post_publish_key: + 发布id
        String key = PostConstant.POST_PUBLISH_KEY + publishId;
        boolean result = redisManagerService.setObjectAsString(key, postAo, PostConstant.POST_PUBLISH_KEY_EXPIRE_TIME);
        if (!result){
            log.warn("Post上传到Redis失败，authorId：{}", postAo.getAuthorId());
            throw new AppException("post上传到服务端失败");
        }
        return publishId;
    }

    @Override
    public void releasePostAfterOss(@NonNull Long publishId) {
        // 获取redis的key
        String key = PostConstant.POST_PUBLISH_KEY + publishId;
        PostAo postAo = redisManagerService.getObjectFromString(key, PostAo.class);
        if (postAo != null){
            // 先删除redis数据
            redisManagerService.deleteAny(key);
            // 异步存储
            globalTaskExecutor.execute(() -> {
                // es + mongo 同步事务存储
                postStorageService.storePostContentToDatabase(postAo, publishId);
            });
            // 异步存储
            globalTaskExecutor.execute(() -> {
                // mysql
                postStorageService.storePostInfoToDatabase(postAo, publishId);
            });
        }
        else {
            log.warn("Post发布失败，postId：{}", publishId);
            throw new AppException("post发布失败，服务器缓存数据过期，请重新上传");
        }
    }

    @Override
    public void deletePost(Long postId) {
        postStorageService.deletePostContentFromDatabase(postId);
        postStorageService.deletePostInfoFromDatabase(postId);
        // 消息队列通知，异步删除oss TODO
    }

    @Override
    public void updatePost(PostAo postAo) {

    }

    @Override
    public PostAo findPostById(Long postId) {
        return postStorageService.findPostAoById(postId);
    }

    @Override
    public List<PostAo> findPostsByIdList(List<Long> idList) {
        return postStorageService.findPostAoByIds(idList);
    }
}
