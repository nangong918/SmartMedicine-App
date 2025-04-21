package com.czy.post.eventListener;

import com.czy.api.constant.oss.OssResponseTypeEnum;
import com.czy.api.domain.ao.post.PostAo;
import com.czy.api.domain.entity.event.OssResponse;
import com.czy.api.domain.entity.event.event.OssResponseEvent;
import com.czy.post.service.PostService;
import com.utils.mvc.redisson.RedissonClusterLock;
import com.utils.mvc.redisson.RedissonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;


/**
 * @author 13225
 * @date 2025/4/2 14:57
 */

@Slf4j
@RequiredArgsConstructor
@Component
public class OssResponseEventListener implements ApplicationListener<OssResponseEvent> {

    private final RedissonService redissonService;
    private final PostService postService;

    // 成功失败的netty消息都由oss直接调用netty；避免了注册为mq的event和spring的event产生额外消耗
    @Override
    public void onApplicationEvent(@NotNull OssResponseEvent event) {
        if (event.getSource() == null || event.getSource().getOssResponseType() == OssResponseTypeEnum.NULL.getCode()){
            return;
        }
        OssResponse ossResponse = event.getSource();
        // 无论成功失败都要删掉分布式锁
        String clusterLockPath = ossResponse.getClusterLockPath();
        String userId = String.valueOf(ossResponse.getUserId());
        RedissonClusterLock redissonClusterLock = new RedissonClusterLock(userId, clusterLockPath);
        redissonService.unlock(redissonClusterLock);
        // 成功oss直接交给netty
        if (ossResponse.ossResponseType == OssResponseTypeEnum.SUCCESS.getCode()){
            String fileRedisKey = ossResponse.getFileRedisKey();
            try {
                PostAo postAo = redissonService.getObjectFromSerializable(fileRedisKey, PostAo.class);
                if (postAo == null){
                    log.error("获取redis失败，postAo == null，fileRedisKey: {}", fileRedisKey);
                    return;
                }
                // 发布成功之后，将post的消息存储到数据库中
                postService.releasePostAfterOss(ossResponse.getPublishId());
            } catch (Exception e) {
                log.error("获取redis失败，fileRedisKey: {}", fileRedisKey);
            }
        }
        // 失败之后此处处理
        else if (ossResponse.ossResponseType == OssResponseTypeEnum.FAIL.getCode()){
            String fileRedisKey = ossResponse.getFileRedisKey();
            boolean result = redissonService.deleteObject(fileRedisKey);
            if (!result){
                log.error("删除redis失败，fileRedisKey: {}", fileRedisKey);
            }
        }
    }
}
