package com.czy.oss.service;


import com.czy.api.api.oss.OssService;
import com.czy.api.domain.Do.oss.OssFileDo;
import com.czy.api.domain.ao.oss.FileIsExistAo;
import com.czy.api.domain.ao.oss.FileNameAo;
import com.czy.oss.mapper.OssMapper;
import com.utils.mvc.service.MinIOService;
import com.utils.mvc.utils.MinIOUtils;
import domain.ErrorFile;
import domain.FileIsExistResult;
import domain.FileOptionResult;
import domain.SuccessFile;
import exception.OssException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author 13225
 * @date 2025/4/9 9:50
 * mysql存储文件信息。作为文件索引以及文件幂等性。
 * 先上传oss再上传mysql，这样的保证文件和数据一致性。
 * 删除也是一样。
 * oss和mysql禁止使用事务。因为速度差距太大，oss上传过程中会对mysql全程上锁
 */
@Slf4j
@RequiredArgsConstructor
@Service
@org.apache.dubbo.config.annotation.Service(protocol = "dubbo", version = "1.0.0")
public class OssServiceImpl implements OssService {

    private final OssMapper ossMapper;
    private final MinIOUtils minIOUtils;
    private final MinIOService minIOService;

    @Override
    public OssFileDo getFileInfoByFileId(Long fileId) {
        return ossMapper.getById(fileId);
    }

    @Override
    public OssFileDo getFileInfoByUserIdAndFileName(Long userId, String bucketName, String fileName) {
        return ossMapper.getByFileStorageNameAndBucketName(userId, fileName, bucketName);
    }

    @Override
    public OssFileDo getFileInfoByUserIdAndFileStorageName(Long userId, String bucketName, String fileStorageName) {
        return ossMapper.getByFileNameAndUserId(userId, fileStorageName);
    }

    @Override
    public boolean checkFileIdempotent(Long userId, String fileName, Long fileSize) {
        return ossMapper.checkFileExist(userId, fileName, fileSize);
    }

    @Override
    public Long checkFileIdempotentAndBackId(Long userId, String fileName, Long fileSize) {
        return ossMapper.getFileIdByIdempotent(userId, fileName, fileSize);
    }

    @Override
    public FileIsExistResult checkFileNameExistForResult(Long userId, String fileName, String bucketName, Long fileSize) {
        FileIsExistResult result = new FileIsExistResult();
        Boolean isExist = ossMapper.checkFileExist(userId, fileName, fileSize);
        result.setIsExist(isExist);
        if (isExist){
            OssFileDo ossFileDo = ossMapper.getByFileStorageNameAndBucketName(userId, fileName, bucketName);
            result.setFileId(ossFileDo.getId());
        }
        return result;
    }

    @Override
    public List<FileIsExistResult> checkFilesExistForResult(List<FileIsExistAo> fileIsExistAos){
        if (CollectionUtils.isEmpty(fileIsExistAos)){
            return new ArrayList<>();
        }
        List<FileIsExistResult> result = new ArrayList<>(fileIsExistAos.size());
        for (FileIsExistAo fileIsExistAo : fileIsExistAos){
            FileIsExistResult fileIsExistResult = new FileIsExistResult();
            OssFileDo ossFileDo = ossMapper.getByFileStorageNameBucketNameFileSize(
                    fileIsExistAo.getUserId(), fileIsExistAo.getFileName(),
                    fileIsExistAo.getBucketName(), fileIsExistAo.getFileSize()
            );
            boolean isExist = ossFileDo != null && ossFileDo.getId() != null;
            fileIsExistResult.setIsExist(isExist);
            if (isExist){
                fileIsExistResult.setFileId(ossFileDo.getId());
            }
            result.add(fileIsExistResult);
        }
        return result;
    }

    @Override
    public boolean checkFileNameExist(Long userId, String fileName, String bucketName) {
        OssFileDo ossFileDo = ossMapper.getByFileStorageNameAndBucketName(userId, fileName, bucketName);
        if (ossFileDo == null){
            return false;
        }
        String fileStorageName = ossFileDo.getFileStorageName();
        boolean isOssExist = minIOUtils.isObjectExist(bucketName, fileStorageName);
        if (!isOssExist){
            log.warn("mysql和oss文件信息不一致，请检查");
        }
        return true;
    }

    @Override
    public long getFileCountByUserId(Long userId) {
        return ossMapper.getFileCountByUserId(userId);
    }

    @Override
    public FileNameAo getFileNameAoByFileStorageName(String fileStorageName) {
        try{
            byte[] decodedBytes = Base64.getDecoder().decode(fileStorageName);
            String decoded = new String(decodedBytes, StandardCharsets.UTF_8);
            String[] parts = decoded.split("_");
            if (parts.length == 3) {
                FileNameAo fileNameAo = new FileNameAo();
                fileNameAo.setUserId(Long.parseLong(parts[0]));
                fileNameAo.setFileName(parts[1]);
                fileNameAo.setTimestamp(Long.parseLong(parts[2]));
                return fileNameAo;
            } else {
                throw new OssException("文件名格式错误");
            }
        } catch (Exception e){
            log.error("文件名解码失败", e);
            throw new OssException("文件名解码失败");
        }
    }

    @Override
    public FileOptionResult uploadFiles(List<MultipartFile> files, Long userId, String bucketName) {
        if (CollectionUtils.isEmpty(files)){
            return new FileOptionResult();
        }
        List<ErrorFile> errorFileList = new LinkedList<>();
        files.forEach(file -> {
            // 幂等性
            String fileName = file.getOriginalFilename();
            Long fileSize = file.getSize();
            boolean idempotent = checkFileIdempotent(userId, fileName, fileSize);
            if (idempotent){
                // 此处不需要position
                errorFileList.add(new ErrorFile(fileName, "[文件已存在]"));
                files.remove(file);
            }
        });
        FileOptionResult result = minIOService.uploadFiles(files, userId, bucketName);
        // 成功的存储到数据库
        uploadFilesRecord(result.getSuccessFiles(), userId, bucketName);
        // 失败的加入到list
        result.getErrorFiles().addAll(errorFileList);
        return result;
    }

    @Override
    public void uploadFilesRecord(List<SuccessFile> files, Long userId, String bucketName) {
        for (SuccessFile successFile : files){
            // oss
            OssFileDo ossFileDo = new OssFileDo();
            ossFileDo.setFileName(successFile.getFileName());
            ossFileDo.setUserId(userId);
            ossFileDo.setBucketName(bucketName);
            ossFileDo.setFileStorageName(successFile.getFileStorageName());
            ossFileDo.setFileSize(successFile.getFileSize());
            ossFileDo.setUploadTimestamp(System.currentTimeMillis());
            // 已经设置了id
            if (successFile.getFileId() != null){
                // 检查id是否已经上传，避免重复上传
                OssFileDo checkExistFileDo = ossMapper.getById(successFile.getFileId());
                if (checkExistFileDo != null && checkExistFileDo.getId() != null){
                    log.info("文件已经存在，id为：{}", checkExistFileDo.getId());
                    continue;
                }
                ossFileDo.setId(successFile.getFileId());
                // 插入
                Long fileId = ossMapper.insert(ossFileDo);
                log.info("文件插入成功，fileId:{}, successFile.id:{}", fileId, successFile.getFileId());
            }
            else {
                // 插入
                Long fileId = ossMapper.insert(ossFileDo);
                // 设置fileId
                successFile.setFileId(fileId);
            }
        }
    }

    @Override
    public InputStream downloadFileByStorageName(Long userId, String fileStorageName, String bucketName) {
        // userId鉴权
        try{
            return minIOUtils.getObject(bucketName, fileStorageName);
        } catch (Exception e){
            log.warn("下载文件失败", e);
            throw new OssException("下载文件失败");
        }
    }

    @Override
    public InputStream downloadFileByFileName(Long userId, String fileName) {
        OssFileDo ossFileDo = ossMapper.getByFileNameAndUserId(userId, fileName);
        if (ossFileDo != null){

            try{
                boolean isBucketExist = minIOUtils.bucketExists(ossFileDo.getBucketName());
                if (!isBucketExist){
                    throw new OssException("存储桶不存在");
                }
                return minIOUtils.getObject(ossFileDo.getBucketName(), ossFileDo.getFileStorageName());
            } catch (Exception e){
                log.warn("下载文件失败", e);
                throw new OssException("下载文件失败");
            }
        }
        throw new OssException("文件不存在");
    }

    @Override
    public List<String> getFileUrlsByUserIdAndFileNames(Long userId, List<String> fileNames) {
        List<String> fileUrls = new LinkedList<>();
        List<OssFileDo> ossFileDos = ossMapper.getByUserId(userId);
        ossFileDos = ossFileDos.stream()
                .filter(Objects::nonNull)
                .filter(ossFileDo -> fileNames.contains(ossFileDo.getFileName()))
                .collect(Collectors.toList());
        for (OssFileDo ossFileDo : ossFileDos){
            addUrlToList(fileUrls, ossFileDo);
        }
        return fileUrls;
    }

    @Override
    public List<String> getFileUrlsByBucketNameAndFileNames(String bucketName, List<String> fileNames) {
        List<String> fileUrls = new LinkedList<>();
        for (String fileName : fileNames){
            String url = getFileUrl(bucketName, fileName);
            fileUrls.add(url);
        }
        return fileUrls;
    }

    @Override
    public List<String> getFileUrlsByFileIds(List<Long> fileIds) {
        List<String> fileUrls = new LinkedList<>();
        for (Long fileId : fileIds){
            if (fileId == null){
                fileUrls.add(null);
                continue;
            }
            OssFileDo ossFileDo = ossMapper.getById(fileId);
            addUrlToList(fileUrls, ossFileDo);
        }
        return fileUrls;
    }

    private void addUrlToList(List<String> fileUrls, OssFileDo ossFileDo){
        if (ossFileDo != null){
            String bucketName = ossFileDo.getBucketName();
            String fileStorageName = ossFileDo.getFileStorageName();
            log.info("bucketName:{}, fileId: {}, fileStorageName:{}",
                    bucketName,
                    ossFileDo.getId(),
                    fileStorageName
            );
            String url = getFileUrl(bucketName, fileStorageName);
            fileUrls.add(url);
        }
    }

    private String getFileUrl(String bucketName, String fileStorageName){
        try{
            if (!StringUtils.hasText(bucketName)){
                throw new OssException("存储桶不存在");
            }
            if (!StringUtils.hasText(fileStorageName)){
                throw new OssException("文件不存在");
            }
            return minIOUtils.getPresignedObjectUrl(bucketName, fileStorageName);
        } catch (Exception e){
            log.warn("获取文件地址失败", e);
            return "";
        }
    }

    @Override
    public boolean deleteFileByStorageName(Long userId, String fileStorageName, String bucketName) {
        try{
            minIOUtils.removeFile(bucketName, fileStorageName);
            ossMapper.deleteByFileStorageNameAndBucketName(fileStorageName, bucketName);
            return true;
        } catch (Exception e){
            log.warn("删除文件失败", e);
            throw new OssException("删除文件失败");
        }
    }

    @Override
    public boolean deleteFileByFileName(Long userId, String fileName, String bucketName) {
        OssFileDo ossFileDo = ossMapper.getByFileNameAndUserId(userId, fileName);
        if (ossFileDo != null){
            if (!StringUtils.hasText(ossFileDo.fileStorageName)){
                return false;
            }
            try{
                minIOUtils.removeFile(bucketName, ossFileDo.getFileStorageName());
                ossMapper.deleteByFileStorageNameAndBucketName(ossFileDo.getFileStorageName(), bucketName);
                return true;
            } catch (Exception e){
                log.warn("删除文件失败", e);
                throw new OssException("删除文件失败");
            }
        }
        return false;
    }

    @Override
    public boolean deleteFileByFileId(Long fileId) {
        OssFileDo ossFileDo = ossMapper.getById(fileId);
        if (ossFileDo != null){
            try{
                // oss删除
                minIOUtils.removeFile(ossFileDo.getBucketName(), ossFileDo.getFileStorageName());
                // mysql删除
                ossMapper.delete(fileId);
                return true;
            } catch (Exception e){
                log.warn("删除文件失败", e);
                throw new OssException("删除文件失败");
            }
        }
        return false;
    }

    @Override
    public List<ErrorFile> deleteFiles(Long userId, List<String> fileStorageNames, String bucketName) {
        List<ErrorFile> errorFileNames = new LinkedList<>();
        fileStorageNames.forEach(fileStorageName -> {
            try{
                minIOUtils.removeFile(bucketName, fileStorageName);
                ossMapper.deleteByFileStorageNameAndBucketName(fileStorageName, bucketName);
            } catch (Exception e){
                log.warn("删除文件失败", e);
                errorFileNames.add(new ErrorFile(fileStorageName, "[删除失败]"));
            }
        });
        return errorFileNames;
    }

    public static void main(String[] args) {
        String fileName = "test.txt";
        Long userId = 123L;
        // userId + fileName + 时间戳
        // 获取当前时间戳
        long timestamp = System.currentTimeMillis();
        // 构建待编码字符串
        String input = userId + "_" + fileName + "_" + timestamp;
        // 使用 Base64 编码
        String fileStorageName = Base64.getEncoder().encodeToString(input.getBytes(StandardCharsets.UTF_8));
        System.out.println("fileStorageName = " + fileStorageName);
        try{
            byte[] decodedBytes = Base64.getDecoder().decode(fileStorageName);
            String decoded = new String(decodedBytes, StandardCharsets.UTF_8);
            String[] parts = decoded.split("_");
            if (parts.length == 3) {
                FileNameAo fileNameAo = new FileNameAo();
                fileNameAo.setUserId(Long.parseLong(parts[0]));
                fileNameAo.setFileName(parts[1]);
                fileNameAo.setTimestamp(Long.parseLong(parts[2]));
                System.out.println("fileNameAo.toJsonString() = " + fileNameAo.toJsonString());
            } else {
                throw new OssException("文件名格式错误");
            }
        } catch (Exception e){
            throw new OssException("文件名解码失败");
        }
    }
}
