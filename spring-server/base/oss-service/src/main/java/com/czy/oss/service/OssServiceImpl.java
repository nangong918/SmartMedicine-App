package com.czy.oss.service;


import com.czy.api.api.oss.OssService;
import com.czy.api.constant.exception.OssException;
import com.czy.api.domain.Do.oss.OssFileDo;
import com.czy.api.domain.ao.oss.ErrorFile;
import com.czy.api.domain.ao.oss.FileNameAo;
import com.czy.oss.mapper.OssMapper;
import com.czy.oss.utils.MinIOUtils;
import io.minio.ObjectWriteResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
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
    public boolean checkFileNameExist(Long userId, String fileStorageName, String bucketName) {
        OssFileDo ossFileDo = ossMapper.getByFileStorageNameAndBucketName(userId, fileStorageName, bucketName);
        boolean isMysqlExist = ossFileDo != null;
        boolean isOssExist = minIOUtils.isObjectExist(bucketName, fileStorageName);
        if (isOssExist != isMysqlExist){
            log.warn("mysql和oss文件信息不一致，请检查");
        }
        return isOssExist;
    }

    @Override
    public long getFileCountByUserId(Long userId) {
        return ossMapper.getFileCountByUserId(userId);
    }

    @Override
    public String getFileStorageName(Long userId, String fileName) {
        // userId + fileName + 时间戳
        // 保留 fileName 的前 15 个字符
        String shortFileName = fileName.length() > 15 ? fileName.substring(0, 15) : fileName;
        // 获取当前时间戳
        long timestamp = System.currentTimeMillis();
        // 构建待编码字符串
        String input = userId + "_" + shortFileName + "_" + timestamp;
        // 使用 Base64 编码
        return Base64.getEncoder().encodeToString(input.getBytes(StandardCharsets.UTF_8));
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
    public List<ErrorFile> uploadFiles(List<MultipartFile> files, Long userId, String bucketName) {
        // 内部包含检查是否已经存在的逻辑
        try {
            minIOUtils.createBucket(bucketName);
        } catch (Exception e) {
            log.error("创建存储桶失败", e);
            throw new OssException("创建存储桶失败");
        }
        List<ErrorFile> errorFileNames = new LinkedList<>();
        // 存储
        files.forEach(file -> {
            // 幂等性
            String fileName = file.getOriginalFilename();
            if (fileName == null){
                errorFileNames.add(new ErrorFile("", "[文件名不能为空]"));
                return;
            }
            Long fileSize = file.getSize();
            boolean idempotent = checkFileIdempotent(userId, fileName, fileSize);
            if (idempotent){
                errorFileNames.add(new ErrorFile(fileName, "[文件已存在]"));
                return;
            }
            String fileStorageName = getFileStorageName(userId, fileName);
            // oss
            try {
                ObjectWriteResponse response = minIOUtils.uploadFile(bucketName, file, fileStorageName, file.getContentType());
                if (response.object() != null){
                    OssFileDo ossFileDo = new OssFileDo();
                    ossFileDo.setFileName(fileName);
                    ossFileDo.setUserId(userId);
                    ossFileDo.setBucketName(bucketName);
                    ossFileDo.setFileStorageName(fileStorageName);
                    ossFileDo.setFileSize(fileSize);
                    ossFileDo.setUploadTimestamp(System.currentTimeMillis());
                    ossMapper.insert(ossFileDo);
                }
            } catch (Exception e) {
                log.error("上传文件失败", e);
                errorFileNames.add(new ErrorFile(fileName, "[上传失败]"));
            }
        });
        return errorFileNames;
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
            try{
                String bucketName = ossFileDo.getBucketName();
                if (!StringUtils.hasText(bucketName)){
                    throw new OssException("存储桶不存在");
                }
                String fileStorageName = ossFileDo.getFileStorageName();
                if (!StringUtils.hasText(fileStorageName)){
                    throw new OssException("文件不存在");
                }
                String fileUrl = minIOUtils.getPresignedObjectUrl(bucketName, fileStorageName);
                fileUrls.add(fileUrl);
            } catch (Exception e){
                log.warn("获取文件地址失败", e);
                fileUrls.add("");
            }
        }
        return fileUrls;
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
