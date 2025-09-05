package com.api.mapper.post.mybatis.bo;

import com.czy.api.domain.bo.post.PostViewBo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author 13225
 * @date 2025/9/5 11:59
 */
@Mapper
public interface PostViewBoMapper {

    /**
     * 根据id获取帖子view信息       （已测试）
     * @param postId            帖子id
     * @param userId            用户id (用于查询用户行为)
     * @return                  帖子viewBo信息
     SELECT
        pi.id AS postId,

        au.id AS authorId,
        au.avatar_file_id AS authorAvatarFileId,
        au.user_name AS authorName,

        pi.title AS postTitle,
        pi.release_timestamp AS releaseTimestamp,
        pi.view_count AS postViewNum,
        pi.like_count AS likeNum,
        pi.collect_count AS collectNum,
        pi.comment_count AS commentNum,
        pi.forward_count AS forwardNum,

        -- 子查询聚合文件ID（不需要GROUP BY）
        (SELECT
            GROUP_CONCAT(DISTINCT pf.file_id)
        FROM post_files pf
        WHERE pf.post_id = pi.id) AS postImgFileIds,

        IFNULL(upa.like, false) AS like,
        IFNULL(upa.collect, false) AS collect,
        IFNULL(upa.dislike, false) AS dislike

     FROM post_info AS pi
     LEFT JOIN login_user AS au ON pi.author_id = au.id
     LEFT JOIN post_files AS pf ON pi.id = pf.post_id
     LEFT JOIN user_post_action AS upa ON pi.id = upa.post_id AND upa.user_id = #{userId}
     WHERE pi.id = #{postId}
     */
    PostViewBo getPostViewBoById(
            @Param("postId") Long postId,
            @Param("userId") Long userId
    );

}
