package com.czy.post.service;

import com.czy.api.domain.Do.post.comment.PostCommentDo;
import com.czy.api.domain.ao.post.CommentAo;
import com.czy.api.domain.dto.service.CommentResultDto;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author 13225
 * @date 2025/4/24 11:51
 */
public interface PostCommentService {

    // [暂时根据时间排序，后续需要根据综合算法如点赞数排序（类似推荐系统）]

    /**
     * 分页获取某个post的一级comment （一页多少条n + 第几页m）（comment在mongodb需要用Page）
     * @param postId              帖子id
     * @param pageSize            每页数量
     * @param pageNum             页数
     * @return  List
     */
    List<CommentAo> getLevel1PostCommentAos(Long postId, Integer pageSize, Integer pageNum);

    /**
     * 获取某个comment的子评论（一页多少条n + 第几页m）（comment在mongodb需要用Page）
     * @param postId              帖子id
     * @param replyCommentId      被回复的评论id
     * @param pageSize            每页数量
     * @param pageNum             页数
     * @return   List
     */
    List<CommentAo> getLevel2PostCommentAos(Long postId, Long replyCommentId, Integer pageSize, Integer pageNum);

    // 通过id获取
    PostCommentDo getPostCommentById(Long commentId);

    /**
     * 评论
     * @param senderId           发送者id
     * @param postId             帖子id
     * @param replyCommentId     回复的评论id (null则为一级评论，存在则是二级评论（回复）)
     * @param content            评论内容
     * @param timestamp          时间戳
     * @return                   评论结果
     */
    @NotNull
    CommentResultDto comment(Long senderId, Long postId, @Nullable Long replyCommentId, @NotNull String content, Long timestamp);

    // 删除评论
    void deleteComment(Long postId, Long commentId, Long senderId);
}
