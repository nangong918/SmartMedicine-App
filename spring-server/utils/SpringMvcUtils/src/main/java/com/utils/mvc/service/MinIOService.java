package com.utils.mvc.service;


import com.czy.api.domain.ao.oss.FileAo;
import domain.FileIsExistResult;
import domain.FileOptionResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
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

    /**
     * 包含幂等性的上传
     * @param files                 文件List
     * @param fileIsExistResults    文件是否已经存在
     * @param bucketName            bucketName
     * @param userId                userId
     * @return                      FileOptionResult
     */
    FileOptionResult uploadFilesWithIdempotent
            (List<MultipartFile> files, List<FileIsExistResult> fileIsExistResults, String bucketName, Long userId);

    // fileName + userId 生成 fileStorageName
    String getFileStorageName(Long userId, String fileName);

    /**
     *  上传文件List
     * @param files          文件List
     * @param bucketName     bucketName
     * @return                ErrorFileList
     */
    FileOptionResult uploadFiles(List<File> files, String bucketName);

    FileOptionResult uploadMultipartFiles(List<MultipartFile> files, String bucketName);

    FileOptionResult uploadLoadFiles(List<FileAo> fileAos, String bucketName);

    void deleteBucketAll(String bucketName) throws Exception;
}
