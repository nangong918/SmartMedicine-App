package com.czy.post.controller;

import com.czy.api.api.oss.OssService;
import com.czy.api.api.user_relationship.UserService;
import com.czy.api.constant.oss.FileConstant;
import com.czy.api.constant.oss.OssResponseTypeEnum;
import com.czy.api.constant.oss.OssTaskTypeEnum;
import com.czy.api.constant.post.PostConstant;
import com.czy.api.constant.user_relationship.UserConstant;
import com.czy.api.domain.Do.user.UserDo;
import com.czy.api.domain.ao.oss.FileIsExistAo;
import com.czy.api.domain.ao.post.PostAo;
import com.czy.api.domain.dto.base.BaseResponse;
import com.czy.api.domain.entity.event.PostOssResponse;
import com.czy.api.domain.vo.post.PostVo;
import com.czy.post.front.PostFrontService;
import com.czy.post.service.PostService;
import com.utils.mvc.redisson.RedissonClusterLock;
import com.utils.mvc.redisson.RedissonService;
import com.utils.mvc.service.MinIOService;
import domain.FileIsExistResult;
import domain.FileOptionResult;
import domain.SuccessFile;
import exception.AppException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.context.ApplicationContext;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 13225
 * @date 2025/4/22 14:25
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(PostConstant.POST_FILE_CONTROLLER)
public class PostFileController {
    private final String postFileBucket = FileConstant.POST_FILE_BUCKET;
    @Reference(protocol = "dubbo", version = "1.0.0", check = false)
    private UserService userService;
    @Reference(protocol = "dubbo", version = "1.0.0", check = false)
    private OssService ossService;
    private final MinIOService minIOService;
    private final RedissonService redissonService;
    private final ApplicationContext applicationContext;
    private final PostFrontService postFrontService;
    private final PostService postService;


    /**
     * 上传帖子files，直接http返回，不用netty
     * @param files         需要上传的文件
     * @param postId        第一次http获取的雪花id，此雪花id为postId也是publishId
     * @param userId        用户Id
     * @return              上传结果
     */
    @PostMapping("/uploadPost/immediately")
    public BaseResponse<PostVo> uploadPostFiles(
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam("postId") Long postId,
            @RequestParam("userId") Long userId){

        if (CollectionUtils.isEmpty(files)) {
            return BaseResponse.LogBackError("请检查files, 不能为空");
        }
        if (postId == null) {
            return BaseResponse.LogBackError("请检查postId, 不能为空");
        }
        if (userId == null) {
            return BaseResponse.LogBackError("请检查userId, 不能为空");
        }

        UserDo userDo = userService.getUserById(userId);
        if (userDo == null || userDo.getId() == null || !StringUtils.hasText(userDo.getAccount())){
            return BaseResponse.LogBackError("请检查userId, 该用户不存在");
        }

        String userPostImageBucket = UserConstant.USER_FILE_BUCKET + postId;
        String ossKey = PostConstant.POST_PUBLISH_KEY + postId;
        String lockData = String.valueOf(userId);
        String lockPath = PostConstant.Post_CONTROLLER + PostConstant.POST_PUBLISH_FIRST;

        if (!redissonService.hasKey(ossKey)) {
            log.warn("postId：{}，上传帖子file失败，redis不存在数据", postId);
            releaseLock(lockData, lockPath);
            return BaseResponse.LogBackError(String.format("请检查[postId:%s]的内容是否正确盛传", postId));
        }

        /*
          幂等性：
          1.判断：userId + fileName + bucketName + fileSize共同判断
          2.输入格式：List<FileIsExistAo>
          3.返回格式：List<FileIsExistResult>
          4.上传格式：List<MultipartFile>, List<FileIsExistResult> （不适用Map，但是要求两者要一一对应）
          5.上传结果格式：FileOptionResult
         */
        try {
            List<FileIsExistAo> fileIsExistAos = new ArrayList<>(files.size());
            for (MultipartFile file : files){
                String fileName = file.getOriginalFilename();
                Long fileSize = file.getSize();

                FileIsExistAo fileIsExistAo = new FileIsExistAo();
                fileIsExistAo.setFileName(fileName);
                fileIsExistAo.setFileSize(fileSize);
                fileIsExistAo.setUserId(userId);
                fileIsExistAo.setBucketName(userPostImageBucket);

                fileIsExistAos.add(fileIsExistAo);
            }

            // 幂等性结果
            List<FileIsExistResult> results = ossService.checkFilesExistForResult(fileIsExistAos);

            // 上传到minIO
            FileOptionResult fileOptionResult = minIOService.uploadFilesWithIdempotent(
                    files,
                    results,
                    userPostImageBucket,
                    userId
            );

            // 上传记录数据到mysql
            ossService.uploadFilesRecord(fileOptionResult.getSuccessFiles(), userId, userPostImageBucket);

            // 获取成功ID
            List<Long> successIds = fileOptionResult.getSuccessFiles()
                    .stream()
                    .map(SuccessFile::getFileId)
                    .collect(Collectors.toList());

            // 进行mysql的postVo信息存储更新
            if (!CollectionUtils.isEmpty(successIds)){
                PostOssResponse postOssResponse = new PostOssResponse();
                postOssResponse.setUserId(userId);
                postOssResponse.setUserAccount(userDo.getAccount());
                postOssResponse.setServiceId(PostConstant.serviceName);
                postOssResponse.setPublishId(postId);
                postOssResponse.setFileIds(successIds);
                postOssResponse.setFileRedisKey(ossKey);
                postOssResponse.setClusterLockPath(lockPath);
                postOssResponse.setOssResponseType(OssResponseTypeEnum.SUCCESS.getCode());
                postOssResponse.setOssOperationType(OssTaskTypeEnum.ADD.getCode());

                boolean result = handleUpload(postOssResponse);
                if (!result){
                    return BaseResponse.LogBackError("上传文件到数据库失败");
                }
                PostVo postVo = postFrontService.getPostVo(postId);
                return BaseResponse.getResponseEntitySuccess(postVo);
            }
        } catch (Exception e){
            log.error("上传文件失败", e);
            return BaseResponse.LogBackError("上传文件失败");
        } finally {
            releaseLock(lockData, lockPath);
        }
        return BaseResponse.LogBackError("上传文件失败");
    }

    private boolean handleUpload(PostOssResponse postOssResponse){
        String fileRedisKey = postOssResponse.getFileRedisKey();
        try {
            PostAo postAo = redissonService.getObjectFromJson(fileRedisKey, PostAo.class);
            if (postAo == null){
                log.error("获取redis失败，postAo == null，fileRedisKey: {}", fileRedisKey);
                return false;
            }
            else {
                log.info("开始处理上传文件，fileRedisKey: {}, postAo: {}", fileRedisKey, postAo.toJsonString());
            }
            // 关联FileIds和postId
            postAo.setFileIds(postOssResponse.getFileIds());
            if (OssTaskTypeEnum.ADD.getCode() == postOssResponse.ossOperationType){
                // 发布成功之后，将post的消息存储到数据库中
                postService.releasePostAfterOss(postAo);
            }
            else if (OssTaskTypeEnum.UPDATE.getCode() == postOssResponse.ossOperationType){
                // 更新成功之后，将post的消息存储到数据库中
                postService.updatePostAfterOss(postAo);
            }
        } catch (Exception e) {
            log.error("获取redis失败，fileRedisKey: {}", fileRedisKey);
            return false;
        }
        return true;
    }

    /**
     * 上传帖子files,netty返回
     * @param files         需要上传的文件
     * @param postId        第一次http获取的雪花id，此雪花id为postId也是publishId
     * @param userId        用户Id
     * @return              提示
     */
    @PostMapping("/uploadPost/netty")
    public BaseResponse<String> uploadPostFilesNetty(
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam("postId") Long postId,
            @RequestParam("userId") Long userId){
        // TODO 待完善
        return BaseResponse.LogBackError("开发中");
    }


    //    // 关联postId 和 fileIdList
    //    void recordPostIdAndFileIdList(Long postId, List<Long> fileIdList);

    // 获取帖子files；[不需要，因为在service能提postService下载url]

    // 修改帖子file
    @PostMapping("/updatePost")
    public BaseResponse<String> updatePostFiles(
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam("postId") Long postId,
            @RequestParam("userAccount") String userAccount){
        return handleUpload(files, postId, userAccount, OssTaskTypeEnum.UPDATE);
    }

    private BaseResponse<String> handleUpload(
            List<MultipartFile> files,
            Long postId,
            String userAccount,
            OssTaskTypeEnum operationType) {
        if (CollectionUtils.isEmpty(files)) {
            return BaseResponse.LogBackError("请检查files");
        }

        String ossKey = PostConstant.POST_PUBLISH_KEY + postId;
        UserDo userDo = getUserDo(userAccount);
        Long userId = userDo.getId();
        String lockData = String.valueOf(userDo.getId());
        String lockPath = PostConstant.Post_CONTROLLER + PostConstant.POST_PUBLISH_FIRST;

        if (!redissonService.hasKey(ossKey)) {
            log.warn("postId：{}，上传帖子file失败，请检查postId", postId);
            releaseLock(lockData, lockPath);
            return BaseResponse.LogBackError("请检查postId: " + postId);
        }

        List<Long> fileIdList = new ArrayList<>();
        files.removeIf(file -> {
            String fileName = file.getOriginalFilename();
            Long fileSize = file.getSize();
            FileIsExistResult result = ossService.checkFileNameExistForResult(userId, fileName, postFileBucket, fileSize);
            if (result.getIsExist()) {
                fileIdList.add(result.getFileId());
                return true; // 移除已存在的文件
            }
            return false; // 保留文件
        });

        try {
            FileOptionResult fileOptionResult = minIOService.uploadFiles(files, userId, postFileBucket);
            ossService.uploadFilesRecord(fileOptionResult.getSuccessFiles(), userId, postFileBucket);
            List<Long> successIds = fileOptionResult.getSuccessFiles()
                    .stream()
                    .map(SuccessFile::getFileId)
                    .collect(Collectors.toList());
            fileIdList.addAll(successIds);

            PostOssResponse postOssResponse = new PostOssResponse();
            postOssResponse.setUserId(userId);
            postOssResponse.setUserAccount(userAccount);
            postOssResponse.setServiceId(PostConstant.serviceName);
            postOssResponse.setPublishId(postId);
            postOssResponse.setFileIds(fileIdList);
            postOssResponse.setFileRedisKey(ossKey);
            postOssResponse.setClusterLockPath(lockPath);
            postOssResponse.setOssResponseType(OssResponseTypeEnum.SUCCESS.getCode());
            postOssResponse.setOssOperationType(operationType.getCode());

            applicationContext.publishEvent(postOssResponse);
        } catch (Exception e) {
            log.error("postId：{}，上传帖子file失败，请检查postId", postId);
            releaseLock(lockData, lockPath);
            return BaseResponse.LogBackError("请检查帖子的文件是否正确;postId：" + postId);
        }

        return BaseResponse.getResponseEntitySuccess("上传中，请等待");
    }

    // 删除帖子 [不需要，因为直接调用ossService传入filesId]


    // 检查用户是否合法
    private UserDo getUserDo(String userAccount) throws AppException {
        String errMsg = "请检查userAccount: " + userAccount;
        if (!StringUtils.hasText(userAccount)){
            throw new AppException(errMsg);
        }
        UserDo userDo = userService.getUserByAccount(userAccount);
        if (userDo == null || userDo.getId() == null){
            throw new AppException(errMsg);
        }
        return userDo;
    }

    /**
     * 解除分布式锁
     * @param lockData  lockData
     * @param path      lockPath
     */
    private void releaseLock(String lockData, String path){
        RedissonClusterLock redissonClusterLock = new RedissonClusterLock(
                lockData,
                path
        );
        try {
            redissonService.unlock(redissonClusterLock);
        } catch (Exception e){
            log.error("redissonService.unlock(redissonClusterLock)失败，请检查redissonClusterLock：{}", redissonClusterLock);
        }
        log.info("已解除分布式锁：lockData：{}，lockPath：{}", lockData, path);
    }
}
