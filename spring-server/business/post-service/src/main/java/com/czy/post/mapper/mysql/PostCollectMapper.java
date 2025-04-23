package com.czy.post.mapper.mysql;

import com.czy.api.domain.Do.post.collect.PostCollectDo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author 13225
 * @date 2025/4/23 17:33
 */
@Mapper
public interface PostCollectMapper {
    // 通过id获取
    PostCollectDo findPostCollectById(Long id);

    // 通过collectFolderId获取全部List<PostCollectDo>
    List<PostCollectDo> findPostCollectsByCollectFolderId(Long collectFolderId);

    // 增加PostCollectDo
    void savePostCollect(PostCollectDo postCollectDo);

    // 通过id删除
    void deletePostCollect(Long id);

    // 通过collectFolderId删除全部
    void deletePostCollectsByCollectFolderId(Long collectFolderId);

    // 更新PostCollectDo
    void updatePostCollect(PostCollectDo postCollectDo);
}
