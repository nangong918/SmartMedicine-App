package com.api.mapper.post.mybatis.bo;

import com.czy.api.domain.bo.post.CommentBo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 13225
 * @date 2025/9/8 11:15
 * 哎, 暂时就写id -> Bo; idList -> BoList
 * 懒得写PostCommentMapper的其他方法; 两次io罢了, 不耽误事
 * 再说了PostCommentMapper是干嘛吃的?
 * @see com.api.mapper.post.mybatis.PostCommentMapper
 */
@Mapper
public interface PostCommentBoMapper {

    /**
     * 通过commentId获取CommentBo
     * @param commentId 评论id
     * @return          评论Bo
     SELECT
        cmu.avatar_file_id AS avatarFileId,
        cmu.user_name AS userName,
        rplu.user_name AS replyUserName,

        pc.timestamp AS commentTimestamp,
        pc.content AS commentContent,

        pc.id AS commentId,
        pc.reply_comment_id AS parentCommentId,
        pc.post_id AS postId,
        pc.commenter_id AS commenterId

     FROM post_comment AS pc
     LEFT JOIN login_user AS cmu ON pc.commenter_id = cmu.id
     LEFT JOIN post_comment AS reply_comment ON pc.reply_comment_id = reply_comment.id -- 避免使用子查询
     LEFT JOIN login_user AS rplu ON reply_comment.commenter_id = rplu.id
     WHERE pc.id = #{commentId}
     */
    CommentBo getCommentBoById(Long commentId);

    /**
     * 通过commentIdList获取CommentBoList
     * @param commentIdList 评论idList
     * @return              评论BoList
     */
    List<CommentBo> getCommentBoByIdList(
            @Param("list") List<Long> commentIdList
    );
}
