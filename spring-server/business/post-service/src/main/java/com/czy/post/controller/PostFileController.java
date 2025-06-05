package com.czy.post.controller;

import com.czy.api.api.oss.OssService;
import com.czy.api.api.user.UserService;
import com.czy.api.constant.oss.OssResponseTypeEnum;
import com.czy.api.constant.oss.OssTaskTypeEnum;
import com.czy.api.constant.post.PostConstant;
import com.czy.api.domain.Do.user.UserDo;
import com.czy.api.domain.dto.base.BaseResponse;
import com.czy.api.domain.entity.event.PostOssResponse;
import com.czy.api.constant.oss.FileConstant;
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


    /**
     * 上传帖子files
     * @param files         需要上传的文件
     * @param postId        第一次http获取的雪花id，此雪花id为postId也是publishId
     * @param userAccount   用户账号
     * @return              上传结果
     */
    @PostMapping("/uploadPost")
    public BaseResponse<String> uploadPostFiles(
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam("postId") Long postId,
            @RequestParam("userAccount") String userAccount){
        return handleUpload(files, postId, userAccount, OssTaskTypeEnum.ADD);
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
