package com.czy.post.mapper.mysql;

import com.czy.api.domain.Do.post.post.PostFilesDo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author 13225
 * @date 2025/4/17 21:23
 */
@Mapper
public interface PostFilesMapper {

    // 插入
    // 单个插入
    Long insertPostFilesDo(PostFilesDo postInfoDo);
    // 批量插入
    int insertPostFilesDoList(List<PostFilesDo> postInfoDoList);

    // 删除
    // 单个删除
    int deletePostFilesDoById(Long id);
    // 批量删除
    void deletePostFilesDoByIdList(List<Long> idList);

    // 通过post_id删除
    void deletePostFilesDoByPostId(Long postId);
    // 通过post_id批量删除
    void deletePostFilesDoByPostIdList(List<Long> postIdList);

    // 更新
    void updatePostFilesDoById(PostFilesDo postInfoDo);
    // 更新postId的文件ids
    void updatePostFilesDoByPostDo(PostFilesDo postFilesDo);
    void updatePostFilesDoByPostDos(List<PostFilesDo> postFilesDos);

    // 查询
    // 根据postId查询
    List<PostFilesDo> getPostFilesDoListByPostId(Long postId);
    // 根据id
    PostFilesDo getPostFilesDoById(Long id);
}
