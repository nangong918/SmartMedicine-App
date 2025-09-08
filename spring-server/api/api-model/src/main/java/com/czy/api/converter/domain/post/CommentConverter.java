package com.czy.api.converter.domain.post;

import com.czy.api.domain.ao.post.CommentAo;
import com.czy.api.domain.bo.post.CommentBo;
import com.czy.api.domain.vo.post.aaa.CommentVo;
import date.DateUtils;
import org.jetbrains.annotations.NotNull;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;

import java.util.Optional;

/**
 * @author 13225
 * @date 2025/9/5 18:29
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CommentConverter {

    // INSTANCE
    CommentConverter INSTANCE = Mappers.getMapper(CommentConverter.class);

    // bo -> vo
    default CommentAo getAoByBo_(@NotNull CommentBo commentBo){
        CommentAo ao = new CommentAo();
        ao.commentVo = new CommentVo();
        ao.commentVo.setUserName(commentBo.getUserName());
        ao.commentVo.setReplyUserName(commentBo.getReplyUserName());
        ao.commentVo.setCommentTime(Optional.ofNullable(commentBo.getCommentTimestamp())
                .map(DateUtils::getDateStringByTimestamp)
                .orElse("")
        );
        ao.commentVo.setCommentContent(commentBo.getCommentContent());
        ao.commentId = commentBo.getCommentId();
        ao.parentCommentId = commentBo.getParentCommentId();
        ao.postId = commentBo.getPostId();
        ao.commenterId = commentBo.getCommenterId();
        return ao;
    };

    default CommentAo getAoByBo(CommentBo commentBo, String avatarUrl){
        if (commentBo == null){
            return null;
        }
        CommentAo ao = getAoByBo_(commentBo);
        ao.commentVo.setAvatarUrl(avatarUrl);
        return ao;
    }
}
