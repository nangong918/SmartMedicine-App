package com.czy.api.converter.domain.post;

import com.czy.api.domain.Do.post.post.PostDetailDo;
import com.czy.api.domain.Do.post.post.content.PostContentEntity;
import com.czy.api.domain.ao.post.PostNerResult;
import com.czy.api.domain.bo.post.PostViewBo;
import com.czy.api.domain.vo.post.PostPreviewVo;
import com.czy.api.domain.vo.post.PostVo;
import date.DateUtils;
import org.jetbrains.annotations.NotNull;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Optional;

/**
 * @author 13225
 * @date 2025/9/5 16:53
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PostViewConverter {

    // INSTANCE
    PostViewConverter INSTANCE = Mappers.getMapper(PostViewConverter.class);

    // bo -> vo
    @Mapping(source = "postId", target = "postId")
    @Mapping(source = "authorName", target = "authorName")
    @Mapping(source = "authorId", target = "authorId")
    @Mapping(source = "postTitle", target = "postTitle")
    @Mapping(source = "postViewNum", target = "postViewNum")
    @Mapping(source = "likeNum", target = "likeNum")
    @Mapping(source = "collectNum", target = "collectNum")
    @Mapping(source = "commentNum", target = "commentNum")
    @Mapping(source = "forwardNum", target = "forwardNum")
    @Mapping(source = "like", target = "like")
    @Mapping(source = "collect", target = "collect")
    @Mapping(source = "dislike", target = "dislike")
    @Mapping(source = "releaseTimestamp", target = "timestamp")
    PostVo getVoByBo_(@NotNull PostViewBo bo);

    default PostVo getVoByBo(
            @NotNull PostViewBo bo,
            String authorAvatarUrl,
            List<PostContentEntity> postContents,
            List<String> postImgUrls,
            List<PostNerResult> nerResults
    ){
        PostVo vo = getVoByBo_(bo);
        vo.setAuthorAvatarUrl(authorAvatarUrl);
        vo.setPostContents(postContents);
        vo.setPostImgUrls(postImgUrls);
        vo.setNerResults(nerResults);
        Optional.ofNullable(bo.getReleaseTimestamp())
                .map(DateUtils::getDateStringByTimestamp)
                .ifPresent(vo::setPostPublishTime);
        return vo;
    }

    default PostVo getVoByBo(
            @NotNull PostViewBo bo,
            PostDetailDo postDetailDo,
            String authorAvatarUrl,
            List<String> postImgUrls
    ){
        PostVo vo = getVoByBo_(bo);
        vo.setAuthorAvatarUrl(authorAvatarUrl);
        Optional.ofNullable(postDetailDo)
            .ifPresent(pdo -> {
                vo.setPostContents(pdo.getPostContents());
                vo.setNerResults(pdo.getNerResults());
            });
        vo.setPostImgUrls(postImgUrls);
        Optional.ofNullable(bo.getReleaseTimestamp())
                .map(DateUtils::getDateStringByTimestamp)
                .ifPresent(vo::setPostPublishTime);
        return vo;
    }

    // bo -> preview vo
    @Mapping(source = "postId", target = "postId")
    @Mapping(source = "authorName", target = "authorName")
    @Mapping(source = "authorId", target = "authorId")
    @Mapping(source = "postTitle", target = "postTitle")
    @Mapping(source = "postViewNum", target = "postViewNum")
    @Mapping(source = "likeNum", target = "likeNum")
    @Mapping(source = "collectNum", target = "collectNum")
    @Mapping(source = "commentNum", target = "commentNum")
    @Mapping(source = "forwardNum", target = "forwardNum")
    @Mapping(source = "like", target = "like")
    @Mapping(source = "collect", target = "collect")
    @Mapping(source = "dislike", target = "dislike")
    @Mapping(source = "releaseTimestamp", target = "timestamp")
    PostPreviewVo getPreviewVoByBo_(@NotNull PostViewBo bo);

    default PostPreviewVo getPreviewVoByBo(
            @NotNull PostViewBo bo,
            String authorAvatarUrl,
            String postImgUrl0,
            List<PostNerResult> nerResults
    ){
        PostPreviewVo vo = getPreviewVoByBo_(bo);
        vo.setAuthorAvatarUrl(authorAvatarUrl);
        vo.setPostImgUrl0(postImgUrl0);
        vo.setNerResults(nerResults);
        Optional.ofNullable(bo.getReleaseTimestamp())
                .map(DateUtils::getDateStringByTimestamp)
                .ifPresent(vo::setPostPublishTime);
        return vo;
    }
}
