package com.czy.post.mapper.mysql;

import com.czy.api.domain.Do.post.post.PostInfoDo;
import org.apache.ibatis.annotations.Param;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * @author 13225
 * @date 2025/4/17 21:23
 */
@Mapper
public interface PostInfoMapper {

    // 插入
    void insertPostInfoDo(PostInfoDo postInfoDo);

    // 批量插入
    void insertPostInfoDoList(List<PostInfoDo> postInfoDoList);

    // 根据id获取PostInfoDo
    PostInfoDo getPostInfoDoById(Long id);

    // 根据List<id> 获取 List<PostInfoDo>
    List<PostInfoDo> getPostInfoDoListByIdList(List<Long> idList);

    // 根据authorId获取List<id>
    List<Long> getPostInfoDoListByAuthorId(Long authorId);

    // 根据authorId获取List<id> + 分页 + 根据releaseTimestamp排序
    List<Long> getPostInfoDoListByAuthorIdPaging(
            @Param("authorId") Long authorId,
            @Param("page") int page,
            @Param("size") int size);

    // 根据authorId获取List<id> + 分页 + 获取releaseTimestamp前n个
    List<Long> getPostInfoDoListByAuthorIdBeforeTimestamp(
            @Param("authorId") Long authorId,
            @Param("timestamp") Long timestamp,
            @Param("n") int n);

    // 根据authorId获取List<id> + 分页 + 获取releaseTimestamp后n个
    List<Long> getPostInfoDoListByAuthorIdAfterTimestamp(
            @Param("authorId") Long authorId,
            @Param("timestamp") Long timestamp,
            @Param("n") int n);

    // 根据id删除PostInfoDo
    void deletePostInfoDoById(Long id);

    // 根据id更新PostInfoDo
    void updatePostInfoDoById(PostInfoDo postInfoDo);
}
