package com.czy.api.domain.Do.oss;

import lombok.Data;
import org.springframework.data.annotation.Id;

/**
 * @author 13225
 * @date 2025/4/17 16:54
 */
@Data
public class OssFileDo {
    // fileId
    @Id
    public Long id;
    // 文件本身的名称
    public String fileName;
    // 上传时间
    public Long uploadTimestamp = System.currentTimeMillis();
    // 文件上传者id（索引：用于查询某个用户的全部文件）
    public Long userId;
    // 文件存储在minIO的名称（避免名称重复）（bucketName + fileStorageName联合索引）
    public String fileStorageName;
    // 文件大小（用于幂等性判断：fileName + fileSize共同判断）
    // 2^63 - 1 = 9.22 EB
    public Long fileSize;
    // 存储桶位置
    public String bucketName;
}

/**
 * CREATE TABLE oss_file (
 *     id BIGINT PRIMARY KEY AUTO_INCREMENT,
 *     file_name VARCHAR(255) NOT NULL,
 *     upload_timestamp BIGINT NOT NULL,
 *     user_id BIGINT NOT NULL,
 *     file_storage_name VARCHAR(255) NOT NULL,
 *     file_size BIGINT NOT NULL,
 *     bucket_name VARCHAR(255) NOT NULL,
 *     INDEX idx_user_id (user_id),  -- 为 userId 创建索引
 *     INDEX idx_bucket_storage (bucket_name, file_storage_name)  -- 创建联合索引
 * );
 */
