package com.utils.mvc.service;


import domain.FileOptionResult;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author 13225
 * @date 2025/4/22 15:39
 */
public interface MinIOService {

    /**
     * 上传文件List
     * 幂等性在上游做
     * @param files             文件List
     * @param bucketName        bucketName
     * @return                  ErrorFileList
     */
    FileOptionResult uploadFiles(List<MultipartFile> files, Long userId, String bucketName);

    // fileName + userId 生成 fileStorageName
    String getFileStorageName(Long userId, String fileName);
}
