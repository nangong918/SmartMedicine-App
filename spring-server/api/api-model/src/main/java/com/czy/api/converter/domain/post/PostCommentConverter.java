package com.czy.api.converter.domain.post;

import com.czy.api.domain.Do.post.comment.PostCommentDo;
import com.czy.api.domain.ao.post.PostCommentAo;
import com.czy.api.domain.dto.http.PostCommentDto;
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

    // ao -> dto
    @Mapping(source = "postId", target = "postId")
    @Mapping(source = "commenterId", target = "commenterId")
    @Mapping(source = "replyCommentId", target = "replyCommentId")
    @Mapping(source = "content", target = "content")
    @Mapping(source = "timestamp", target = "timestamp")
    @Mapping(source = "commenterAccount", target = "commenterAccount")
    @Mapping(source = "commenterName", target = "commenterName")
    PostCommentDto postCommentAoToPostCommentDto(PostCommentAo ad);

    default PostCommentDto postCommentAoToPostCommentDto(PostCommentAo ad, String commenterAvatarUrl){
        PostCommentDto postCommentDto = postCommentAoToPostCommentDto(ad);
        postCommentDto.setCommenterAvatarUrl(commenterAvatarUrl);
        return postCommentDto;
    }
}
