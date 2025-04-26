package com.czy.post.eventListener;

import com.czy.api.constant.netty.NettyConstants;
import com.czy.api.constant.netty.ResponseMessageType;
import com.czy.api.constant.oss.OssResponseTypeEnum;
import com.czy.api.constant.oss.OssTaskTypeEnum;
import com.czy.api.domain.ao.post.PostAo;
import com.czy.api.domain.entity.event.Message;
import com.czy.api.domain.entity.event.PostOssResponse;
import com.czy.api.domain.entity.event.event.PostOssResponseEvent;
import com.czy.post.component.RabbitMqSender;
import com.czy.post.service.PostService;
import com.utils.mvc.redisson.RedissonClusterLock;
import com.utils.mvc.redisson.RedissonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;


/**
 * @author 13225
 * @date 2025/4/2 14:57
 */

@Slf4j
@RequiredArgsConstructor
@Component
public class OssResponseEventListener implements ApplicationListener<PostOssResponseEvent> {

    private final RedissonService redissonService;
    private final PostService postService;
    private final RabbitMqSender rabbitMqSender;


    // 成功失败的netty消息都由oss直接调用netty；避免了注册为mq的event和spring的event产生额外消耗
    @Override
    public void onApplicationEvent(@NotNull PostOssResponseEvent event) {
        PostOssResponse postOssResponse = event.getSource();
        if (postOssResponse == null || postOssResponse.getOssResponseType() == OssResponseTypeEnum.NULL.getCode()){
            return;
        }

        String clusterLockPath = postOssResponse.getClusterLockPath();
        String userId = String.valueOf(postOssResponse.getUserId());
        RedissonClusterLock redissonClusterLock = new RedissonClusterLock(
                // 此处正确，因为上锁也是使用userId
                // 使用userId 1.是因为为了避免用户在执行的过程中修改了userAccount
                // 2.是因为oss服务使用的就是userId
                userId,
                clusterLockPath
        );
        String fileRedisKey = postOssResponse.getFileRedisKey();
        String serverToFrontend = "";
        if (!StringUtils.hasText(fileRedisKey)){
            log.warn("处理PostOssResponseEvent失败, fileRedisKey为空");
            return;
        }
        try{
            // 成功oss直接交给netty
            if (postOssResponse.ossResponseType == OssResponseTypeEnum.SUCCESS.getCode()){
                try {
                    PostAo postAo = redissonService.getObjectFromSerializable(fileRedisKey, PostAo.class);
                    if (postAo == null){
                        log.error("获取redis失败，postAo == null，fileRedisKey: {}", fileRedisKey);
                        return;
                    }
                    // 关联文件ids和postId
                    postAo.setFileIds(postOssResponse.getFileIds());
                    if (OssTaskTypeEnum.ADD.getCode() == postOssResponse.ossOperationType){
                        // 发布成功之后，将post的消息存储到数据库中
                        postService.releasePostAfterOss(postAo);
                        serverToFrontend = "发布成功";
                    }
                    else if (OssTaskTypeEnum.UPDATE.getCode() == postOssResponse.ossOperationType){
                        // 更新成功之后，将post的消息存储到数据库中
                        postService.updatePostAfterOss(postAo);
                        serverToFrontend = "更新成功";
                    }
                } catch (Exception e) {
                    log.error("获取redis失败，fileRedisKey: {}", fileRedisKey);
                }
            }
            // 失败之后此处处理
            else if (postOssResponse.ossResponseType == OssResponseTypeEnum.FAIL.getCode()){
                // netty通知前端
                serverToFrontend = "发布失败, 文件资源上传失败";
            }
        } finally {
            boolean result = redissonService.deleteObject(fileRedisKey);
            // 删除redis
            if (!result){
                log.error("删除redis失败，fileRedisKey: {}", fileRedisKey);
            }
            if (StringUtils.hasText(serverToFrontend) && StringUtils.hasText(userId)){
                // 发送消息给前端
                Message message = new Message();
                message.setSenderId(NettyConstants.SERVER_ID);
                message.setReceiverId(postOssResponse.getUserAccount());
                message.setTimestamp(System.currentTimeMillis());
                message.setType(ResponseMessageType.Oss.UPLOAD_FILE);
                rabbitMqSender.push(message);
            }
            // 无论成功失败都要删掉分布式锁
            try {
                redissonService.deleteObject(clusterLockPath);
            } catch (Exception e){
                log.error("删除分布式锁失败，clusterLockPath: {}", clusterLockPath);
            }
        }
    }
}
