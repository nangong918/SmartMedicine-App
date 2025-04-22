package com.czy.post.mapper.mysql;

import com.czy.api.domain.Do.post.post.PostFilesDo;
import com.czy.api.domain.Do.post.post.PostInfoDo;
import org.apache.ibatis.annotations.Param;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * @author 13225
 * @date 2025/4/17 21:23
 */
@Mapper
public interface PostFilesMapper {

    // 插入
    // 单个插入
    void insertPostFilesDo(PostFilesDo postInfoDo);
    // 批量插入
    void insertPostFilesDoList(List<PostFilesDo> postInfoDoList);

    // 删除
    // 单个删除
    void deletePostFilesDoById(Long id);
    // 批量删除
    void deletePostFilesDoByIdList(List<Long> idList);

    // 更新
    void updatePostFilesDoById(PostFilesDo postInfoDo);
    // 更新postId的文件ids
    void updatePostFilesDoByPostId( Long postId);

    // 查询
    // 根据postId查询
    List<PostFilesDo> getPostFilesDoListByPostId(Long postId);
    // 根据id
    PostFilesDo getPostFilesDoById(Long id);
}
