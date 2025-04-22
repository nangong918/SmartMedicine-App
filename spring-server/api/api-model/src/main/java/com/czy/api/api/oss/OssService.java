package com.czy.api.api.oss;

import com.czy.api.domain.Do.oss.OssFileDo;
import com.czy.api.domain.ao.oss.FileNameAo;
import domain.ErrorFile;
import domain.SuccessFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

/**
 * @author 13225
 * @date 2025/4/9 11:58
 */
public interface OssService {

    // id -> ossDo
    OssFileDo getFileInfoByFileId(Long fileId);

    /**
     * userId + bucketName + fileName -> OssFileDo
     * @param userId            用户id
     * @param bucketName        bucketName
     * @param fileName          文件名
     * @return                  OssFile
     */
    OssFileDo getFileInfoByUserIdAndFileName(Long userId, String bucketName, String fileName);

    // userId bucketName fileStorageName -> ossDo
    OssFileDo getFileInfoByUserIdAndFileStorageName(Long userId, String bucketName, String fileStorageName);

    /**
     * 检查文件是否存在？（幂等性）
     * 文件幂等性和timestamp无关
     * @param userId            用户id
     * @param fileName          文件名
     * @param fileSize          文件大小
     * @return  true：文件存在；false：文件不存在
     */
    boolean checkFileIdempotent(Long userId, String fileName, Long fileSize);

    /**
     * 检查文件名是否存在？（非幂等性）
     * @param userId            用户id
     * @param fileStorageName   文件存储名称
     * @param bucketName        bucketName
     * @return                  true：文件名存在；false：文件名不存在
     */
    boolean checkFileNameExist(Long userId, String fileStorageName, String bucketName);

    // 某个用户上传的文件数量
    long getFileCountByUserId(Long userId);

    // 通过fileStorageName获取FileNameAo
    FileNameAo getFileNameAoByFileStorageName(String fileStorageName);

    /**
     * 上传文件List
     * @param files             文件List
     * @param userId            用户id
     * @param bucketName        bucketName
     * @return                  ErrorFileList
     */
    List<ErrorFile> uploadFiles(List<MultipartFile> files, Long userId, String bucketName);

    /**
     * 成功的存储到数据库
     * @param files             文件List
     * @param userId            用户id
     * @param bucketName        bucketName
     */
    void uploadFilesRecord(List<SuccessFile> files, Long userId, String bucketName);

    /**
     * 通过fileStorageName单个文件下载
     * userId 用于鉴定权限
     * 鉴权：JwtPayload取出权限信息，判断是否拥有下载权限
     * @param fileStorageName   文件存储名称；用于获取文件
     */
    InputStream downloadFileByStorageName(Long userId, String fileStorageName, String bucketName);

    /**
     * 通过fileName下载
     * @param userId            用户id
     * @param fileName          文件名
     * @return                  InputStream
     */
    InputStream downloadFileByFileName(Long userId, String fileName);

    /**
     * 通过List<filename>获取图片List<Url>
     * @param userId            用户id
     * @param fileNames         文件名List
     * @return
     */
    List<String> getFileUrlsByUserIdAndFileNames(Long userId, List<String> fileNames);

    List<String> getFileUrlsByFileIds(List<Long> fileIds);

    // 删除文件 fileStorageName
    boolean deleteFileByStorageName(Long userId, String fileStorageName, String bucketName);

    // 删除文件 fileName
    boolean deleteFileByFileName(Long userId, String fileName, String bucketName);

    // 根据fileId删除
    boolean deleteFileByFileId(Long fileId);

    // 批量删除
    List<ErrorFile> deleteFiles(Long userId, List<String> fileStorageNames, String bucketName);
}
