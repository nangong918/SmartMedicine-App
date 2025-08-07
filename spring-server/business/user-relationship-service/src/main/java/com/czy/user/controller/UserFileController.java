package com.czy.user.controller;

import com.czy.api.api.oss.OssService;
import com.czy.api.api.user_relationship.LoginService;
import com.czy.api.constant.oss.OssResponseTypeEnum;
import com.czy.api.constant.oss.OssTaskTypeEnum;
import com.czy.api.constant.user_relationship.UserConstant;
import com.czy.api.domain.Do.user.LoginUserDo;
import com.czy.api.domain.Do.user.UserDo;
import com.czy.api.domain.ao.oss.FileIsExistAo;
import com.czy.api.domain.dto.base.BaseResponse;
import com.czy.api.domain.entity.event.UserOssResponse;
import com.czy.api.domain.vo.user.UserVo;
import com.czy.api.exception.CommonExceptions;
import com.czy.api.exception.UserExceptions;
import com.czy.user.mapper.mysql.user.LoginUserMapper;
import com.czy.user.mapper.mysql.user.UserMapper;
import com.czy.user.service.front.UserFrontService;
import com.utils.mvc.redisson.RedissonClusterLock;
import com.utils.mvc.redisson.RedissonService;
import com.utils.mvc.service.MinIOService;
import domain.FileIsExistResult;
import domain.FileOptionResult;
import domain.SuccessFile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
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
    private final UserMapper userMapper;
    private final LoginUserMapper loginUserMapper;
    private final UserFrontService userFrontService;

    // 注册用户的上传头像
    @PostMapping("/register")
    public BaseResponse<UserVo> registerUserUploadImg(
            @RequestParam("img") MultipartFile img,
            @RequestParam("phone") String phone,
            @RequestParam("userId") Long userId
    ) {
        if (img == null){
            return BaseResponse.LogBackError(UserExceptions.IMAGE_NOT_UPLOAD);
        }
        if (!StringUtils.hasText(phone) || userId == null){
            return BaseResponse.LogBackError(CommonExceptions.PARAM_ERROR);
        }

        String userImageBucket = UserConstant.USER_FILE_BUCKET + userId;
        String ossKey = UserConstant.USER_REGISTER_REDIS_KEY + phone;
        String lockPath = UserConstant.Login_CONTROLLER + UserConstant.Password_Register;

        String errorMsg = String.format("请检查phone：%s 是否申请注册", phone);
        if (!redissonService.hasKey(ossKey)) {
            log.warn("用户上传头像的锁不存在, userPhone: {}", phone);
            releaseLock(phone, lockPath);

            return BaseResponse.LogBackError(errorMsg);
        }

        List<Long> fileIdList = new ArrayList<>();

        try {
            List<MultipartFile> multipartFiles = new ArrayList<>(1);
            multipartFiles.add(img);
            FileOptionResult fileOptionResult = minIOService.uploadImages(
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
                userOssResponse.setOssOperationType(OssTaskTypeEnum.ADD.getCode());

                boolean result = finishRegister(userOssResponse);
                if (!result){
                    log.warn("处理UserOssResponseEvent失败, userPhone: {}", phone);
                    return BaseResponse.LogBackError(errorMsg);
                }
                else {
                    // 删除redis缓存
                    redissonService.deleteObject(ossKey);
                }
                UserVo userVo = userFrontService.getUserVoById(userId);
                return BaseResponse.getResponseEntitySuccess(userVo);
            }
            else {
                return BaseResponse.LogBackError(errorMsg);
            }
        } catch (Exception e){
            log.error("上传文件失败", e);
            return BaseResponse.LogBackError(errorMsg);
        } finally {
            releaseLock(phone, lockPath);
        }
    }

    // 修改头像
    @PostMapping(UserConstant.Update_Image)
    public BaseResponse<UserVo> updateImage(
            @RequestParam("img") MultipartFile img,
            @RequestParam("userId") Long userId
    ) {
        String errorMsg = String.format("userId：%s 修改头像失败", userId);
        int operationType = OssTaskTypeEnum.UPDATE.getCode();

        if (img == null){
            return BaseResponse.LogBackError("请上传图片");
        }
        if (userId == null){
            return BaseResponse.LogBackError("请输入用户id");
        }

        String userImageBucket = UserConstant.USER_FILE_BUCKET + userId;
        String lockPath = UserConstant.User_File_CONTROLLER + UserConstant.Update_Image;

        RedissonClusterLock redissonClusterLock = new RedissonClusterLock(
                String.valueOf(userId),
                lockPath,
                UserConstant.USER_CHANGE_KEY_EXPIRE_TIME
        );

        if (!redissonService.tryLock(redissonClusterLock)){
            return BaseResponse.LogBackError("正在修改请勿频繁点击");
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
            List<MultipartFile> files = new ArrayList<>(1);
            List<FileIsExistAo> fileIsExistAos = new ArrayList<>(1);
            files.add(img);
            for (MultipartFile file : files){
                String fileName = file.getOriginalFilename();
                Long fileSize = file.getSize();

                FileIsExistAo fileIsExistAo = new FileIsExistAo();
                fileIsExistAo.setFileName(fileName);
                fileIsExistAo.setFileSize(fileSize);
                fileIsExistAo.setUserId(userId);
                fileIsExistAo.setBucketName(userImageBucket);

                fileIsExistAos.add(fileIsExistAo);
            }
            // 幂等性结果
            List<FileIsExistResult> results = ossService.checkFilesExistForResult(fileIsExistAos);

            // 上传到minIO
            FileOptionResult fileOptionResult = minIOService.uploadFilesWithIdempotent(
                    files,
                    results,
                    userImageBucket,
                    userId,
                    true
            );

            // 上传记录数据到mysql
            ossService.uploadFilesRecord(fileOptionResult.getSuccessFiles(), userId, userImageBucket);

            // 获取成功ID
            List<Long> successIds = fileOptionResult.getSuccessFiles()
                    .stream()
                    .map(SuccessFile::getFileId)
                    .collect(Collectors.toList());

            // 进行mysql需改userVo
            if (!CollectionUtils.isEmpty(successIds)){
                Long newFileId = successIds.get(0);

                // 查找原先的记录
                UserDo userDo = userMapper.getUserById(userId);
                Long oldFileId = userDo.getAvatarFileId();

                // 重复上传检查
                if (newFileId.equals(oldFileId)){
                    log.info("用户{}上传的图片和之前上传的图片相同", userId);
                    UserVo userVo = userFrontService.getUserVoById(userId);
                    return BaseResponse.getResponseEntitySuccess(userVo);
                }
                else {
                    // 先删除原来不需要的文件
                    if (oldFileId != null){
                        ossService.deleteFileByFileId(oldFileId);
                        log.info("用户:{}删除了之前上传的图片:{}", userId, oldFileId);
                    }

                    // 存储新的记录
                    UserOssResponse userOssResponse = new UserOssResponse();
                    userOssResponse.setUserId(userId);
                    userOssResponse.setFileIds(successIds);
                    userOssResponse.setClusterLockPath(lockPath);
                    userOssResponse.setOssResponseType(OssResponseTypeEnum.SUCCESS.getCode());
                    userOssResponse.setOssOperationType(operationType);

                    boolean result = finishUpdate(userOssResponse);
                    if (!result){
                        errorMsg = String.format("上传头像成功，但是更新user: %s 头像信息失败", userId);
                        log.warn(errorMsg);
                        return BaseResponse.LogBackError(errorMsg);
                    }
                    UserVo userVo = userFrontService.getUserVoById(userId);
                    return BaseResponse.getResponseEntitySuccess(userVo);
                }
            }
        } catch (Exception e){
            log.error(errorMsg, e);
            return BaseResponse.LogBackError(errorMsg);
        } finally {
            releaseLock(String.valueOf(userId), lockPath);
        }
        return BaseResponse.LogBackError(errorMsg);
    }

    // 注册
    private boolean finishRegister(UserOssResponse userOssResponse){
        String clusterLockPath = userOssResponse.getClusterLockPath();
        String phone = userOssResponse.getPhone();

        String fileRedisKey = userOssResponse.getFileRedisKey();
        if (!StringUtils.hasText(fileRedisKey)){
            log.warn("处理UserOssResponseEvent失败, fileRedisKey为空");
            return false;
        }
        try {
            if (userOssResponse.ossResponseType == OssResponseTypeEnum.SUCCESS.getCode()){
                try {
                    LoginUserDo loginUserDo = redissonService.getObjectFromJson(fileRedisKey, LoginUserDo.class);
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
//                    // 修改
//                    else if (OssTaskTypeEnum.UPDATE.getCode() == userOssResponse.getOssOperationType()){
//                        loginService.updateStorageToDatabase(loginUserDo);
//                        return true;
//                    }
                } catch (Exception e){
                    log.error("获取redis失败，fileRedisKey: {}", fileRedisKey, e);
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

    // 更新
    private boolean finishUpdate(UserOssResponse userOssResponse){
        try {
            LoginUserDo loginUserDo = loginUserMapper.getLoginUser(userOssResponse.getUserId());
            loginUserDo.setAvatarFileId(userOssResponse.getFileIds().get(0));
            if (userOssResponse.ossResponseType == OssResponseTypeEnum.SUCCESS.getCode()){
                if (OssTaskTypeEnum.UPDATE.getCode() == userOssResponse.getOssOperationType()){
                    // 存储到userService
                    loginService.updateStorageToDatabase(loginUserDo);
                    return true;
                }
            }
        } catch (Exception e){
            log.error("上传头像失败", e);
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
