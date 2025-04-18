package com.czy.api.converter.domain.post;

import com.czy.api.domain.Do.post.post.PostDetailDo;
import com.czy.api.domain.Do.post.post.PostDetailEsDo;
import com.czy.api.domain.Do.post.post.PostInfoDo;
import com.czy.api.domain.ao.post.PostAo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;

/**
 * @author 13225
 * @date 2025/4/18 21:18
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PostConverter {
    // INSTANCE
    PostConverter INSTANCE = Mappers.getMapper(PostConverter.class);

    // ao -> esDo
    @Mapping(source = "title", target = "title")
    @Mapping(source = "content", target = "content")
    @Mapping(source = "fileStorageNamesUrl", target = "fileStorageNamesUrl")
    PostDetailEsDo toEsDo(PostAo postAo);

    default PostDetailEsDo toEsDo(PostAo postAo, Long id){
        PostDetailEsDo postDetailEsDo = toEsDo(postAo);
        postDetailEsDo.setId(id);
        return postDetailEsDo;
    }

    // ao -> mongoDo
    @Mapping(source = "title", target = "title")
    @Mapping(source = "content", target = "content")
    @Mapping(source = "fileStorageNamesUrl", target = "fileStorageNamesUrl")
    PostDetailDo toMongoDo(PostAo postAo);

    default PostDetailDo toMongoDo(PostAo postAo, Long id){
        PostDetailDo postDetailDo = toMongoDo(postAo);
        postDetailDo.setId(id);
        return postDetailDo;
    }

    // ao -> mysqlDo
    @Mapping(source = "authorId", target = "authorId")
    @Mapping(source = "releaseTimestamp", target = "releaseTimestamp")
    @Mapping(source = "likeCount", target = "likeCount")
    @Mapping(source = "collectCount", target = "collectCount")
    @Mapping(source = "commentCount", target = "commentCount")
    @Mapping(source = "forwardCount", target = "forwardCount")
    PostInfoDo toMysqlDo(PostAo postAo);

    default PostInfoDo toMysqlDo(PostAo postAo, Long id){
        PostInfoDo postInfoDo = toMysqlDo(postAo);
        postInfoDo.setId(id);
        return postInfoDo;
    }

    // esDo -> mongoDo
    @Mapping(source = "id", target = "id")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "content", target = "content")
    @Mapping(source = "fileStorageNamesUrl", target = "fileStorageNamesUrl")
    PostDetailDo toMongoDo(PostDetailEsDo postDetailEsDo);

    // mongoDo -> esDo
    @Mapping(source = "id", target = "id")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "content", target = "content")
    @Mapping(source = "fileStorageNamesUrl", target = "fileStorageNamesUrl")
    PostDetailEsDo toEsDo(PostDetailDo postDetailDo);

    // do -> ao
    default PostAo doToAo(PostDetailDo postDetailDo, PostInfoDo postInfoDo){
        PostAo postAo = new PostAo();
        if (postDetailDo != null){
            postAo.setTitle(postDetailDo.getTitle());
            postAo.setContent(postDetailDo.getContent());
            postAo.setFileStorageNamesUrl(postDetailDo.getFileStorageNamesUrl());
        }
        if (postInfoDo != null){
            postAo.setAuthorId(postInfoDo.getAuthorId());
            postAo.setReleaseTimestamp(postInfoDo.getReleaseTimestamp());
            postAo.setLikeCount(postInfoDo.getLikeCount());
            postAo.setCollectCount(postInfoDo.getCollectCount());
            postAo.setCommentCount(postInfoDo.getCommentCount());
            postAo.setForwardCount(postInfoDo.getForwardCount());
        }
        return postAo;
    }

    default PostAo doToAo(PostDetailEsDo postDetailEsDo, PostInfoDo postInfoDo){
        PostDetailDo postDetailDo = toMongoDo(postDetailEsDo);
        return doToAo(postDetailDo, postInfoDo);
    }
}
