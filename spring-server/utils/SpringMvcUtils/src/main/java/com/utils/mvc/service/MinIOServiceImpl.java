package com.utils.mvc.service;

import domain.ErrorFile;
import com.utils.mvc.utils.MinIOUtils;
import domain.FileOptionResult;
import domain.SuccessFile;
import exception.OssException;
import io.minio.ObjectWriteResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;

/**
 * @author 13225
 * @date 2025/4/22 15:45
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class MinIOServiceImpl implements MinIOService{

    private final MinIOUtils minIOUtils;

    @Override
    public FileOptionResult uploadFiles(List<MultipartFile> files, Long userId, String bucketName) {
        // 内部包含检查是否已经存在的逻辑
        FileOptionResult fileOptionResult = new FileOptionResult();
        List<SuccessFile> successFiles = new ArrayList<>();
        List<ErrorFile> errorFiles = new ArrayList<>();
        try {
            minIOUtils.createBucket(bucketName);
        } catch (Exception e) {
            log.error("创建存储桶失败", e);
            throw new OssException("创建存储桶失败");
        }
        for (MultipartFile file : files) {
            String fileName = file.getOriginalFilename();
            if (fileName == null) {
                errorFiles.add(new ErrorFile("", "[文件名不能为空]"));
                continue;
            }
            try {
                String fileStorageName = getFileStorageName(userId, fileName);
                ObjectWriteResponse response = minIOUtils.uploadFile(bucketName, file, fileStorageName, file.getContentType());
                if (response != null){
                    successFiles.add(new SuccessFile(fileName, fileStorageName, file.getSize()));
                }
            } catch (Exception e) {
                log.error("上传文件失败", e);
                errorFiles.add(new ErrorFile(fileName, "[上传失败]"));
            }
        }
        fileOptionResult.setErrorFiles(errorFiles);
        fileOptionResult.setSuccessFiles(successFiles);
        return fileOptionResult;
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
}
