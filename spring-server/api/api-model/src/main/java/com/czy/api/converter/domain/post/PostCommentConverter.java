package com.czy.api.converter.domain.post;

import com.czy.api.domain.Do.post.comment.PostCommentDo;
import com.czy.api.domain.dto.socket.request.PostCommentRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;

/**
 * @author 13225
 * @date 2025/4/24 11:21
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PostCommentConverter {

    // INSTANCE
    PostCommentConverter INSTANCE = Mappers.getMapper(PostCommentConverter.class);

    // PostCommentRequest -> PostCommentDo
    @Mapping(source = "postId", target = "postId")
    @Mapping(source = "content", target = "content")
    @Mapping(source = "replyCommentId", target = "replyCommentId")
    PostCommentDo postCommentRequestToPostCommentDo(PostCommentRequest request);

    default PostCommentDo postCommentRequestToPostCommentDo(PostCommentRequest request, Long commenterId) {
        PostCommentDo postCommentDo = postCommentRequestToPostCommentDo(request);
        postCommentDo.setCommenterId(commenterId);
        return postCommentDo;
    }
}
