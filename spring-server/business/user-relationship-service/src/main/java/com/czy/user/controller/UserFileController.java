package com.czy.user.controller;

import com.czy.api.api.oss.OssService;
import com.czy.api.api.user_relationship.LoginService;
import com.czy.api.constant.oss.OssResponseTypeEnum;
import com.czy.api.constant.oss.OssTaskTypeEnum;
import com.czy.api.constant.user_relationship.UserConstant;
import com.czy.api.domain.Do.user.LoginUserDo;
import com.czy.api.domain.dto.base.BaseResponse;
import com.czy.api.domain.entity.event.UserOssResponse;
import com.utils.mvc.redisson.RedissonClusterLock;
import com.utils.mvc.redisson.RedissonService;
import com.utils.mvc.service.MinIOService;
import domain.FileIsExistResult;
import domain.FileOptionResult;
import domain.SuccessFile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
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
 * @date 2025/6/9 11:59
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(UserConstant.User_File_CONTROLLER)
public class UserFileController {

    @Reference(protocol = "dubbo", version = "1.0.0", check = false)
    private OssService ossService;
    private final MinIOService minIOService;
    private final RedissonService redissonService;
    private final LoginService loginService;

    // 注册用户的上传头像
    @PostMapping("/registerUser/uploadImg")
    public BaseResponse<String> registerUserUploadImg(
            @RequestParam("img") MultipartFile img,
            @RequestParam("phone") String phone,
            @RequestParam("userId") Long userId
    ) {
       return handleUpload(img, phone, userId, OssTaskTypeEnum.ADD.getCode());
    }

    // 修改头像
    @PostMapping("/updateImage")
    public BaseResponse<String> updateImage(
            @RequestParam("img") MultipartFile img,
            @RequestParam("phone") String phone,
            @RequestParam("userId") Long userId
    ) {
        return handleUpload(img, phone, userId, OssTaskTypeEnum.UPDATE.getCode());
    }

    private BaseResponse<String> handleUpload(
            MultipartFile img,
            String phone,
            Long userId,
            Integer operationType
    ){
        if (img == null){
            return BaseResponse.LogBackError("请上传图片");
        }
        if (!StringUtils.hasText(phone)){
            return BaseResponse.LogBackError("请输入手机号");
        }
        if (userId == null){
            return BaseResponse.LogBackError("请输入用户id");
        }

        String userImageBucket = UserConstant.USER_FILE_BUCKET + phone;
        String ossKey = UserConstant.USER_REGISTER_REDIS_KEY + phone;
        String lockPath = UserConstant.Login_CONTROLLER + UserConstant.Password_Register;

        String errorMsg = String.format("请检查phone：%s 是否申请注册", phone);
        if (!redissonService.hasKey(ossKey)) {
            log.warn("用户上传头像的锁不存在, userPhone: {}", phone);
            releaseLock(phone, lockPath);

            return BaseResponse.LogBackError(errorMsg);
        }

        // 幂等
        List<Long> fileIdList = new ArrayList<>();
        List<MultipartFile> files = new ArrayList<>(1);
        files.add(img);
        files.removeIf(file -> {
            String fileName = file.getOriginalFilename();
            Long fileSize = file.getSize();
            FileIsExistResult result = ossService.checkFileNameExistForResult(userId, fileName, userImageBucket, fileSize);
            if (result.getIsExist()) {
                fileIdList.add(result.getFileId());
                return true; // 移除已存在的文件
            }
            return false; // 保留文件
        });

        try {
            List<MultipartFile> multipartFiles = new ArrayList<>(1);
            multipartFiles.add(img);
            FileOptionResult fileOptionResult = minIOService.uploadFiles(
                    multipartFiles, userId, userImageBucket);
            ossService.uploadFilesRecord(fileOptionResult.getSuccessFiles(), userId, userImageBucket);
            List<Long> successIds = fileOptionResult.getSuccessFiles()
                    .stream()
                    .map(SuccessFile::getFileId)
                    .collect(Collectors.toList());

            fileIdList.addAll(successIds);

            if (!fileIdList.isEmpty()){
                UserOssResponse userOssResponse = new UserOssResponse();
                userOssResponse.setUserId(userId);
                userOssResponse.setPhone(phone);
                userOssResponse.setFileIds(fileIdList);
                userOssResponse.setClusterLockPath(lockPath);
                userOssResponse.setFileRedisKey(ossKey);
                userOssResponse.setOssResponseType(OssResponseTypeEnum.SUCCESS.getCode());
                userOssResponse.setOssOperationType(operationType);

                boolean result = finishRegister(userOssResponse);
                if (!result){
                    log.warn("处理PostOssResponseEvent失败, userPhone: {}", phone);
                    return BaseResponse.LogBackError(errorMsg);
                }
                return BaseResponse.getResponseEntitySuccess("上传成功");
            }
            else {
                return BaseResponse.LogBackError(errorMsg);
            }
        } catch (Exception e){
            releaseLock(phone, lockPath);
            return BaseResponse.LogBackError(errorMsg);
        }
    }

    private boolean finishRegister(UserOssResponse userOssResponse){
        String clusterLockPath = userOssResponse.getClusterLockPath();
        String phone = userOssResponse.getPhone();

        RedissonClusterLock redissonClusterLock = new RedissonClusterLock(
                // 此处正确，因为上锁也是使用phone
                // 使用phone 1.是因为为了避免用户在执行的过程中修改了userAccount
                // 2.是因为oss服务使用的就是phone
                phone,
                clusterLockPath
        );
        String fileRedisKey = userOssResponse.getFileRedisKey();
        if (!StringUtils.hasText(fileRedisKey)){
            log.warn("处理PostOssResponseEvent失败, fileRedisKey为空");
            return false;
        }
        try {
            if (userOssResponse.ossResponseType == OssResponseTypeEnum.SUCCESS.getCode()){
                try {
                    LoginUserDo loginUserDo = redissonService.getObjectFromSerializable(fileRedisKey, LoginUserDo.class);
                    if (loginUserDo == null || loginUserDo.getId() == null){
                        log.warn("处理userOssResponse失败, loginUserDo为空");
                        return false;
                    }
                    // fileId关联
                    if (userOssResponse.getFileIds().isEmpty()){
                        log.warn("处理userOssResponse失败, fileIds为空");
                        return false;
                    }
                    loginUserDo.setAvatarFileId(userOssResponse.getFileIds().get(0));
                    // 添加
                    if (OssTaskTypeEnum.ADD.getCode() == userOssResponse.getOssOperationType()){
                        // 添加只可能是注册
                        loginService.registerStorageToDatabase(loginUserDo);
                        return true;
                    }
                    // 修改
                    else if (OssTaskTypeEnum.UPDATE.getCode() == userOssResponse.getOssOperationType()){
                        loginService.updateStorageToDatabase(loginUserDo);
                        return true;
                    }
                } catch (Exception e){
                    log.error("获取redis失败，fileRedisKey: {}", fileRedisKey);
                }
            }
        } finally {
            boolean result = redissonService.deleteObject(fileRedisKey);
            // 删除redis
            if (!result){
                log.error("删除redis失败，fileRedisKey: {}", fileRedisKey);
            }
            // 无论成功失败都要删掉分布式锁
            releaseLock(phone, clusterLockPath);
        }
        return false;
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
