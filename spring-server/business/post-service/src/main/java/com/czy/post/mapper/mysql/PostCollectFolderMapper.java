package com.czy.post.mapper.mysql;

import com.czy.api.domain.Do.post.collect.PostCollectFolderDo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 13225
 * @date 2025/4/23 17:18
 */
@Mapper
public interface PostCollectFolderMapper {

    // 添加 PostCollectFolder
    // 添加文件夹
    Long savePostCollectFolder(PostCollectFolderDo postCollectFolderDo);

    // 删除
    // 删除文件夹
    void deletePostCollectFolder(
            @Param("collectFolderId") Long collectFolderId,
            @Param("userId") Long userId);

    // 更新
    void updatePostCollectFolder(PostCollectFolderDo postCollectFolderDo);

    // (postId + userId)查询
    PostCollectFolderDo findPostCollectFolderByPostIdAndUserId(
            @Param("postId")Long postId,
            @Param("userId")Long userId);

    // 查询某个user的全部文件夹
    List<PostCollectFolderDo> findPostCollectFolderByUserId(Long userId);

    // collectFolderId查询
    PostCollectFolderDo findPostCollectFolderById(Long collectFolderId);
}
