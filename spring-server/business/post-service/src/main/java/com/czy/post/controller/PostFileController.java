package com.czy.post.controller;

import com.czy.api.api.oss.OssService;
import com.czy.api.api.user.UserService;
import com.czy.api.constant.oss.OssConstant;
import com.czy.api.constant.oss.OssResponseTypeEnum;
import com.czy.api.constant.oss.OssTaskTypeEnum;
import com.czy.api.constant.post.PostConstant;
import com.czy.api.domain.Do.user.UserDo;
import com.czy.api.domain.dto.base.BaseResponse;
import com.czy.api.domain.entity.event.PostOssResponse;
import com.utils.mvc.redisson.RedissonClusterLock;
import com.utils.mvc.redisson.RedissonService;
import com.utils.mvc.service.MinIOService;
import domain.ErrorFile;
import domain.FileIsExistResult;
import domain.FileOptionResult;
import domain.SuccessFile;
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

    @Reference(protocol = "dubbo", version = "1.0.0", check = false)
    private UserService userService;
    @Reference(protocol = "dubbo", version = "1.0.0", check = false)
    private OssService ossService;
    private final MinIOService minIOService;
    private final RedissonService redissonService;
    private final String POST_FILE_BUCKET = "post-file";
    private final ApplicationContext applicationContext;


    // 上传帖子file
    @PostMapping("/upload")
    public BaseResponse<String> upload(
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam("postId") Long postId,
            @RequestParam("userAccount") String userAccount){
        if (CollectionUtils.isEmpty(files)){
            return BaseResponse.LogBackError("请检查files");
        }
        String ossKey = PostConstant.POST_PUBLISH_KEY + postId;
        if (!StringUtils.hasText(userAccount)){
            return BaseResponse.LogBackError("请检查userAccount: " + userAccount);
        }
        UserDo userDo = userService.getUserByAccount(userAccount);
        if (userDo == null || userDo.getId() == null){
            return BaseResponse.LogBackError("请检查userAccount: " + userAccount);
        }
        Long userId = userDo.getId();
        String lockData = String.valueOf(userDo.getId());
        String lockPath = PostConstant.Post_CONTROLLER + PostConstant.POST_PUBLISH_FIRST;
        if (!redissonService.hasKey(ossKey)){
            log.warn("postId：{}，上传帖子file失败，请检查postId", postId);
            releaseLock(
                    lockData,
                    lockPath
            );
            return BaseResponse.LogBackError("请检查postId: " + postId);
        }
        List<Long> fileIdList = new ArrayList<>();
        // 幂等性检查，以及相同文件就不用存储了，post指向相同的文件id
        files.forEach(file -> {
            // 幂等性
            String fileName = file.getOriginalFilename();
            Long fileSize = file.getSize();
            FileIsExistResult result = ossService.checkFileNameExistForResult(userId, fileName, POST_FILE_BUCKET, fileSize);
            if (result.getIsExist()){
                fileIdList.add(result.getFileId());
                files.remove(file);
            }
        });
        FileOptionResult fileOptionResult = minIOService.uploadFiles(files, userId, POST_FILE_BUCKET);
        // 成功的存储到数据库
        ossService.uploadFilesRecord(fileOptionResult.getSuccessFiles(), userId, POST_FILE_BUCKET);
        List<Long> successIds = fileOptionResult.getSuccessFiles()
                .stream()
                .map(SuccessFile::getFileId)
                .collect(Collectors.toList());
        fileIdList.addAll(successIds);
        PostOssResponse postOssResponse = new PostOssResponse();
        postOssResponse.setUserId(userId);
        postOssResponse.setServiceId(PostConstant.serviceName);
        postOssResponse.setPublishId(postId);
        postOssResponse.setFileIds(fileIdList);
        postOssResponse.setFileRedisKey(ossKey);
        postOssResponse.setClusterLockPath(lockPath);
        postOssResponse.setOssResponseType(OssResponseTypeEnum.SUCCESS.getCode());
        postOssResponse.setOssOperationType(OssTaskTypeEnum.ADD.getCode());
        applicationContext.publishEvent(postOssResponse);
        return BaseResponse.getResponseEntitySuccess("上传成功");
    }

    //    // 关联postId 和 fileIdList
    //    void recordPostIdAndFileIdList(Long postId, List<Long> fileIdList);

    // 获取帖子files；(不需要，因为在service能提postService下载url)

    // 修改帖子file

    // 删除帖子

    // 解除分布式锁
    private void releaseLock(String lockData, String path){
        RedissonClusterLock redissonClusterLock = new RedissonClusterLock(
                lockData,
                path
        );
        redissonService.unlock(redissonClusterLock);
        log.info("已解除分布式锁：lockData：{}，lockPath：{}", lockData, path);
    }
}
