package com.czy.post.mapper.mysql;

import com.czy.api.domain.Do.post.collect.PostCollectDo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 13225
 * @date 2025/4/23 17:33
 */
@Mapper
public interface PostCollectMapper {
    // 通过id获取
    PostCollectDo findPostCollectById(Long id);
    // 通过postId和FolderId查找到
    PostCollectDo findPostCollectByPostIdAndFolderId(
            @Param("postId") Long postId,
            @Param("collectFolderId") Long collectFolderId
    );

    // 通过idList获取
    List<PostCollectDo> findPostCollectsByIdList(List<Long> idList);

    // 通过collectFolderId获取全部List<PostCollectDo>
    List<PostCollectDo> findPostCollectsByCollectFolderId(Long collectFolderId);

    // 通过userId获取全部List<PostCollectDo> (需要联表查询)
    List<PostCollectDo> findPostCollectsByUserId(Long userId);

    /**
     * 通过userId分页获取全部List<PostCollectDo> (需要联表查询)
     * @param userId    用户id
     * @param pageNum   页码
     * @param pageSize  每页数量
     * @return          List<PostCollectDo>
     */
    List<PostCollectDo> findPostCollectsByUserIdPaging(
            @Param("userId") Long userId,
            @Param("pageNum") Integer pageNum,
            @Param("pageSize") Integer pageSize
    );

    // 增加PostCollectDo
    void savePostCollect(PostCollectDo postCollectDo);

    // 通过id删除
    void deletePostCollect(Long id);

    // 通过collectFolderId删除全部
    void deletePostCollectsByCollectFolderId(Long collectFolderId);
    // 联合postId和collectFolderId删除某条
    void deletePostCollectByPostIdAndCollectFolderId(
            @Param("postId") Long postId,
            @Param("collectFolderId") Long collectFolderId);

    // 更新PostCollectDo
    void updatePostCollect(PostCollectDo postCollectDo);
}
