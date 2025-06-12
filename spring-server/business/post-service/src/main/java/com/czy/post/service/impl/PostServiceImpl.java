package com.czy.post.service.impl;

import cn.hutool.core.util.IdUtil;
import com.czy.api.api.oss.OssService;
import com.czy.api.api.user_relationship.UserService;
import com.czy.api.constant.netty.NettyConstants;
import com.czy.api.constant.netty.ResponseMessageType;
import com.czy.api.constant.post.PostConstant;
import com.czy.api.domain.Do.user.UserDo;
import com.czy.api.domain.ao.post.PostAo;
import com.czy.api.domain.entity.event.Message;
import com.czy.post.component.RabbitMqSender;
import com.czy.post.service.PostFileService;
import com.czy.post.service.PostService;
import com.czy.post.service.PostStorageService;
import com.utils.mvc.redisson.RedissonService;
import domain.FileOptionResult;
import exception.AppException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 13225
 * @date 2025/4/18 17:39
 * TODO 整个Service待测试
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class PostServiceImpl implements PostService {

    @Reference(protocol = "dubbo", version = "1.0.0", check = false)
    private UserService userService;
    private final RedissonService redissonService;
    private final PostStorageService postStorageService;
    private final ThreadPoolTaskExecutor globalTaskExecutor;
    private final RabbitMqSender rabbitMqSender;
    private final PostFileService postFileService;
    @Reference(protocol = "dubbo", version = "1.0.0", check = false)
    private OssService ossService;

    @Override
    public long releasePostWithoutFile(@NonNull PostAo postAo) {
        long publishId = IdUtil.getSnowflakeNextId();
        postAo.setId(publishId);
        // mysql
        postStorageService.storePostInfoToDatabase(postAo);
        postStorageService.storePostFilesToDatabase(postAo);
        // mongo + es
        postStorageService.storePostContentToDatabase(postAo);
        // neo4j
        postStorageService.storePostFeatureToNeo4j(postAo, postAo.getNerResults());
        return publishId;
    }

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
    public long releasePostFirst(@NonNull PostAo postAo) throws Exception{
        // 由于两次http请求可能都不是一个服务处理的，所以数据需要缓存在redis
        // 生成发布的雪花id
        long publishId = IdUtil.getSnowflakeNextId();
        // redis的存储key是：post_publish_key: + 发布id
        // key统一格式：post_publish_key:snowflakeId（注意是snowflakeId不是userAccount或者userName）
        String ossKey = PostConstant.POST_PUBLISH_KEY + publishId;
        postAo.setId(publishId);
        boolean result = redissonService.setObjectByJson(ossKey, postAo, PostConstant.POST_CHANGE_KEY_EXPIRE_TIME);
        if (!result){
            log.warn("Post上传到Redis失败，authorId：{}", postAo.getAuthorId());
            throw new AppException("post上传到服务端失败");
        }
        else {
            log.info("Post上传到Redis成功，authorId：{}, ossKey: {}", postAo.getAuthorId(), ossKey);
        }
        return publishId;
    }

    @Override
    public void releasePostAfterOss(@NonNull PostAo postAo) {
        // es + mongo 同步事务存储
        postStorageService.storePostContentToDatabase(postAo);
        // neo4j
        postStorageService.storePostFeatureToNeo4j(postAo, postAo.getNerResults());
        // mysql
        postStorageService.storePostInfoToDatabase(postAo);
        // files
        postStorageService.storePostFilesToDatabase(postAo);
    }

    @Override
    public boolean isLegalPost(@NonNull PostAo postAo) {
        // 查询authorId是否发布过相同的title，防止刷帖
        Long postId = postStorageService.findPostIdByAuthorIdAndTitle(postAo.getAuthorId(), postAo.getTitle());
        if (postId != null){
            log.warn("用户:{}发布过相同的标题:{};postId:{}", postAo.getAuthorId(), postAo.getTitle(), postId);
            return false;
        }
        return true;
    }

    @Override
    public void deletePost(Long postId, Long userId) {
        PostAo postAo = postStorageService.findPostAoById(postId);
        String toFront = "删除成功";
        FileOptionResult fileOptionResult = postFileService.deleteFileByPostAo(postAo);
        if (!fileOptionResult.getErrorFiles().isEmpty()){
            toFront = String.format("删除成功，但是存在失败的文件：%s", fileOptionResult.getErrorFiles());
            log.warn("用户:{}删除帖子:{}失败，存在失败的文件：{}", userId, postId, fileOptionResult.getErrorFiles());
        }
        // 懒得写分布式删除了，原理跟发布一样。
        postStorageService.deletePostContentFromDatabase(postId);
        // 不在此处删除mysql，因为oss还需要查询具体数据，删除oss之后再删除mysql
//        postStorageService.deletePostInfoFromDatabase(postId);
        // 消息队列通知，异步删除oss
//        OssTask ossTask = new OssTask();
//        ossTask.setOssFileId(postId);
//        ossTask.setUserId(userId);
//        ossTask.setOssTaskType(OssTaskTypeEnum.DELETE.getCode());
//        // 消息队列异步告诉oss删除
//        rabbitMqSender.pushToOss(ossTask);
        // 消息队列告诉前端删除结果
        UserDo userDo = userService.getUserById(userId);
        if (userDo != null && StringUtils.hasText(userDo.getAccount())){
            Message message = new Message();
            message.setSenderId(NettyConstants.SERVER_ID);
            message.setReceiverId(userDo.getAccount());
            message.setTimestamp(System.currentTimeMillis());
            message.setType(ResponseMessageType.Oss.DELETE_FILE);
            Map<String, String> data = new HashMap<>();
            data.put("message", toFront);
            message.setData(data);

            rabbitMqSender.push(message);
        }
    }

    @Override
    public void updatePostFirst(PostAo postAo, Long postId) throws Exception{
        // 由于两次http请求可能都不是一个服务处理的，所以数据需要缓存在redis
        String key = PostConstant.POST_UPDATE_KEY + postId;
        boolean result = redissonService.setObjectByJson(key, postAo, PostConstant.POST_CHANGE_KEY_EXPIRE_TIME);
        if (!result){
            log.warn("Post更新到Redis失败，authorId：{}", postAo.getAuthorId());
            throw new AppException("post更新到服务端失败");
        }
    }

    @Override
    public void updatePostAfterOss(@NonNull PostAo postAo) {
        // es + mongo 同步事务存储
        postStorageService.updatePostContentToDatabase(postAo);
        // mysql
        postStorageService.updatePostInfoToDatabase(postAo);
        postStorageService.updatePostFilesToDatabase(postAo);
    }

    @Override
    public void updatePostInfoAndContent(PostAo postAo) {
        postStorageService.updatePostInfoToDatabase(postAo);
        postStorageService.updatePostFilesToDatabase(postAo);
        postStorageService.updatePostContentToDatabase(postAo);
    }

    @Override
    public void updatePostInfo(PostAo postAo) {
        postStorageService.updatePostInfoToDatabase(postAo);
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
