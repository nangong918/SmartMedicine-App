package com.czy.api.converter.domain.post;

import com.czy.api.domain.Do.post.post.PostDetailDo;
import com.czy.api.domain.Do.post.post.PostDetailEsDo;
import com.czy.api.domain.Do.post.post.PostFilesDo;
import com.czy.api.domain.Do.post.post.PostInfoDo;
import com.czy.api.domain.ao.post.PostAo;
import com.czy.api.domain.dto.http.request.PostPublishRequest;
import com.czy.api.domain.dto.http.request.PostUpdateRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author 13225
 * @date 2025/4/18 21:18
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PostConverter {
    // INSTANCE
    PostConverter INSTANCE = Mappers.getMapper(PostConverter.class);

    // ao -> esDo
    @Mapping(source = "id", target = "id")
    @Mapping(source = "title", target = "title")
    PostDetailEsDo toEsDo(PostAo postAo);


    // ao -> mongoDo
    @Mapping(source = "id", target = "id")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "content", target = "content")
    PostDetailDo toMongoDo(PostAo postAo);

    // ao -> mysqlDo
    @Mapping(source = "id", target = "id")
    @Mapping(source = "authorId", target = "authorId")
    @Mapping(source = "releaseTimestamp", target = "releaseTimestamp")
    @Mapping(source = "likeCount", target = "likeCount")
    @Mapping(source = "collectCount", target = "collectCount")
    @Mapping(source = "commentCount", target = "commentCount")
    @Mapping(source = "forwardCount", target = "forwardCount")
    PostInfoDo toInfoDo(PostAo postAo);

    /**
     * ao -> PostFiles
     * 注意此处拿不到PostFiles的id
     * @param postAo
     * @return
     */
    default List<PostFilesDo> toPostFilesList(PostAo postAo){
        if (postAo == null || CollectionUtils.isEmpty(postAo.getFileIds())){
            return new ArrayList<>();
        }
        List<PostFilesDo> postFilesDoList = new ArrayList<>();
        postAo.getFileIds().forEach(fileId -> {
            PostFilesDo postFilesDo = new PostFilesDo();
            postFilesDo.setPostId(postAo.getId());
            postFilesDo.setFileId(fileId);
            // 注意此处拿不到PostFiles的id
            postFilesDoList.add(postFilesDo);
        });
        return postFilesDoList;
    }

    // do -> ao
    default PostAo doToAo(
            PostDetailDo postDetailDo,
            PostInfoDo postInfoDo, List<PostFilesDo> postFilesDoList){
        PostAo postAo = new PostAo();
        if (postDetailDo != null){
            postAo.setTitle(postDetailDo.getTitle());
            postAo.setContent(postDetailDo.getContent());
        }
        if (postInfoDo != null){
            postAo.setAuthorId(postInfoDo.getAuthorId());
            postAo.setReleaseTimestamp(postInfoDo.getReleaseTimestamp());
            postAo.setLikeCount(postInfoDo.getLikeCount());
            postAo.setCollectCount(postInfoDo.getCollectCount());
            postAo.setCommentCount(postInfoDo.getCommentCount());
            postAo.setForwardCount(postInfoDo.getForwardCount());
        }
        if (!CollectionUtils.isEmpty(postFilesDoList)){
            List<Long> fileIds = postFilesDoList.
                    stream()
                    .filter(Objects::nonNull)
                    .map(PostFilesDo::getFileId)
                    .collect(Collectors.toList());
            postAo.setFileIds(fileIds);
        }
        return postAo;
    }


    // request -> ao
    default PostAo requestToAo(PostPublishRequest request, Long userId){
        PostAo postAo = new PostAo();
        postAo.setTitle(request.getTitle());
        postAo.setContent(request.getContent());
        postAo.setAuthorId(userId);
        return postAo;
    }

    // update request -> ao
    default PostAo updateRequestToAo(PostUpdateRequest request, Long userId){
        PostAo postAo = new PostAo();
        postAo.setTitle(request.getTitle());
        postAo.setContent(request.getContent());
        postAo.setAuthorId(userId);
        return postAo;
    }

}
