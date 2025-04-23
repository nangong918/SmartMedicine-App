package com.czy.oss.mapper;

import com.czy.api.domain.Do.oss.OssFileDo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 13225
 * @date 2025/4/17 18:14
 */
@Mapper
public interface OssMapper {

    // 增
    Long insert(OssFileDo ossFileDo);

    // 批量增
    List<Long> insertBatch(List<OssFileDo> ossFileDos);

    // 删
    void delete(Long id);

    // 根据 fileStorageName + bucketName删除
    void deleteByFileStorageNameAndBucketName(
            @Param("fileStorageName")String fileStorageName,
            @Param("bucketName")String bucketName);

    // 批量删
    void deleteBatch(List<Long> ids);

    // 改
    void update(@Param("do") OssFileDo ossFileDo);

    // 批量改
    void updateBatch(@Param("doList") List<OssFileDo> ossFileDos);

    // 根据id 查询
    OssFileDo getById(Long id);

    // 根据user_id查询List
    List<OssFileDo> getByUserId(Long userId);

    // 根据fileStorageName + bucketName 查询
    OssFileDo getByFileStorageNameAndBucketName(
            @Param("userId") Long userId,
            @Param("fileName") String fileName,
            @Param("bucketName") String bucketName
    );

    // 根据fileName + userId查询
    OssFileDo getByFileNameAndUserId(
            @Param("userId") Long userId,
            @Param("fileName") String fileName);

    // 查询user在bucketName内包含的数量
    long getFileCountByUserIdAndBucketName(
            @Param("userId")Long userId,
            @Param("bucketName")String bucketName);

    // 查询user的全部文件数量
    long getFileCountByUserId(Long userId);

    // 检查是否文件存在
    boolean checkFileExist(
            @Param("userId")Long userId,
            @Param("fileName")String fileName,
            @Param("fileSize")Long fileSize);
}
