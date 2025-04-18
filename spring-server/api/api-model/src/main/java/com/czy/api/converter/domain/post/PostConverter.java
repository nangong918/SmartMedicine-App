package com.czy.api.converter.domain.post;

import com.czy.api.domain.Do.post.post.PostDetailDo;
import com.czy.api.domain.Do.post.post.PostDetailEsDo;
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
    PostDetailDo toDo(PostAo postAo);

    default PostDetailDo toDo(PostAo postAo, Long id){
        PostDetailDo postDetailDo = toDo(postAo);
        postDetailDo.setId(id);
        return postDetailDo;
    }

    // esDo -> mongoDo
    @Mapping(source = "id", target = "id")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "content", target = "content")
    @Mapping(source = "fileStorageNamesUrl", target = "fileStorageNamesUrl")
    PostDetailDo toDo(PostDetailEsDo postDetailEsDo);

    // mongoDo -> esDo
    @Mapping(source = "id", target = "id")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "content", target = "content")
    @Mapping(source = "fileStorageNamesUrl", target = "fileStorageNamesUrl")
    PostDetailEsDo toEsDo(PostDetailDo postDetailDo);
}
