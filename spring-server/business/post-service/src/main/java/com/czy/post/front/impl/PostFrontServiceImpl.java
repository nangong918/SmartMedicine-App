package com.czy.post.front.impl;

import com.api.mapper.post.mongo.PostDetailMongoMapper;
import com.api.mapper.post.mybatis.bo.PostViewBoMapper;
import com.czy.api.api.user.user.UserService;
import com.czy.api.converter.domain.post.PostViewConverter;
import com.czy.api.domain.Do.post.comment.PostCommentMongoDo;
import com.czy.api.domain.Do.post.post.PostDetailDo;
import com.czy.api.domain.Do.user.UserDo;
import com.czy.api.domain.ao.post.PostAo;
import com.czy.api.domain.ao.post.PostInfoAo;
import com.czy.api.domain.ao.post.PostNerResult;
import com.czy.api.domain.bo.post.PostViewBo;
import com.czy.api.domain.vo.post.PostPreviewVo;
import com.czy.api.domain.vo.post.PostVo;
import com.czy.api.domain.vo.post.old.CommentOldVo;
import com.czy.api.domain.vo.post.old.PostOldVo;
import com.czy.api.domain.vo.post.toFront.PostFVo;
import com.czy.api.domain.vo.post.toFront.PostPreviewFVo;
import com.czy.post.front.PostFrontService;
import com.czy.post.service.PostService;
import com.utils.minio.service.OssService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author 13225
 * @date 2025/6/6 16:39
 * 后端的数据类型转为前端需要的数据类型
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class PostFrontServiceImpl implements PostFrontService {

    private final OssService ossService;
    @Reference(protocol = "dubbo", version = "1.0.0", check = false)
    private UserService userService;
    private final PostService postService;
    private final PostViewBoMapper postViewBoMapper;
    private final PostViewConverter postViewConverter;
    private final PostDetailMongoMapper postDetailMongoMapper;


    @NonNull
    @Override
    public List<PostPreviewVo> toPostPreviewVoList(List<PostInfoAo> postAoList, @NotNull Long userId){

        if (CollectionUtils.isEmpty(postAoList)){
            return new ArrayList<>();
        }

        List<Long> postIds = postAoList.stream()
                .filter(Objects::nonNull)
                .map(PostInfoAo::getId)
                .collect(Collectors.toList());

        List<PostViewBo> postViewBos = postViewBoMapper.getPostViewBoListByIds(
                postIds,
                userId
        );

        if (CollectionUtils.isEmpty(postViewBos)){
            return new ArrayList<>();
        }

        List<PostPreviewVo> postPreviewVos = new ArrayList<>(postViewBos.size());

        // oss
        List<Long> postAuthorImgFileIds = new ArrayList<>(postViewBos.size());
        // 取出全部的第一个组成一个list
        List<Long> postImgFile0Ids = new ArrayList<>(postViewBos.size());
        for (PostViewBo postViewBo : postViewBos) {
            postAuthorImgFileIds.add(postViewBo.authorAvatarFileId);
            postImgFile0Ids.add(postViewBo.postId);
        }
        List<String> authorImgUrls = ossService.getFileUrlsByFileIds(postAuthorImgFileIds);
        List<String> postFileUrls = ossService.getFileUrlsByFileIds(postImgFile0Ids);

        // ner
        List<PostDetailDo> postDetailDoList = postDetailMongoMapper.findPostDetailsByIdList(
                postIds
        );
        for (PostViewBo postViewBo : postViewBos){
            List<PostNerResult> nerResults = null;

            for (PostDetailDo postDetailDo : postDetailDoList){
                if (postViewBo.postId.equals(postDetailDo.getId())){
                    nerResults = postDetailDo.getNerResults();
                    break;
                }
            }

            PostPreviewVo previewVo = postViewConverter.getPreviewVoByBo(
                    postViewBo,
                    authorImgUrls.get(postViewBos.indexOf(postViewBo)),
                    postFileUrls.get(postViewBos.indexOf(postViewBo)),
                    nerResults
            );
            postPreviewVos.add(previewVo);
        }

        return postPreviewVos;
    }

    @Override
    public PostOldVo postAoToPostVo(@NonNull PostAo postAo) {
        PostOldVo postVo = new PostOldVo();
        // 所属info
        postVo.postId = postAo.getId();

        // file
        postVo.postImgUrls = ossService.getFileUrlsByFileIds(postAo.getFileIds());

        // author
        postVo.authorId = postAo.getAuthorId();
        UserDo userDo = userService.getUserById(postAo.getAuthorId());
        if (userDo != null){
            postVo.authorName = userDo.getUserName();
            Long avatarFileId = userDo.getAvatarFileId();
            if (avatarFileId != null){
                List<Long> avatarFileIdList = new ArrayList<>();
                avatarFileIdList.add(avatarFileId);
                postVo.authorAvatarUrl = ossService.getFileUrlsByFileIds(avatarFileIdList).get(0);
            }
        }

        // 内容
        postVo.postTitle = postAo.getTitle();
        postVo.postContent = postAo.getContent();
        postVo.postPublishTimestamp = postAo.getReleaseTimestamp();

        // 数据
        postVo.likeNum = PostOldVo.numToString(postAo.getLikeCount());
        postVo.collectNum = PostOldVo.numToString(postAo.getCollectCount());
        postVo.commentNum = PostOldVo.numToString(postAo.getCommentCount());
        postVo.readNum = PostOldVo.numToString(postAo.getReadCount());
        postVo.forwardNum = PostOldVo.numToString(postAo.getForwardCount());

        // user属性 TODO
//        postVo.isLike = postAo.getLikeCount() > 0;
//        postVo.isCollect = postAo.getCollectCount() > 0;
//        postVo.isDislike = postAo.getDislikeCount() > 0;

        return postVo;
    }

    private CommentOldVo getCommentVo(PostCommentMongoDo postCommentDo) {
        CommentOldVo commentVo = new CommentOldVo();
        commentVo.commentId = postCommentDo.getId();
        commentVo.replyCommentId = postCommentDo.getReplyCommentId();
        commentVo.postId = postCommentDo.getPostId();

        commentVo.content = postCommentDo.getContent();
        commentVo.commentTimestamp = postCommentDo.getTimestamp();

        commentVo.commenterId = postCommentDo.getCommenterId();

        UserDo userDo = userService.getUserById(postCommentDo.getCommenterId());
        if (userDo != null){
            commentVo.commentName = userDo.getUserName();
            Long avatarFileId = userDo.getAvatarFileId();
            if (avatarFileId != null){
                List<Long> avatarFileIdList = new ArrayList<>();
                avatarFileIdList.add(avatarFileId);
                commentVo.commentAvatarUrl = ossService.getFileUrlsByFileIds(avatarFileIdList).get(0);
            }
        }

        return commentVo;
    }

    @Override
    public PostVo getPostVo(Long postId, Long userId) {
        PostViewBo postViewBo = postViewBoMapper.getPostViewBoById(
                postId,
                userId
        );
        if (postViewBo == null || postViewBo.getPostId() == null){
            return null;
        }

        // fileId -> url
        List<Long> fileIds = new ArrayList<>();
        fileIds.add(postViewBo.getAuthorAvatarFileId());
        fileIds.addAll(postViewBo.getPostImgFileIds());
        List<String> fileUrls = ossService.getFileUrlsByFileIds(fileIds);
        assert fileUrls.size() == fileIds.size();
        String authorAvatarUrl = null;
        List<String> postImgUrls = new ArrayList<>();
        for (int i = 0; i < fileIds.size(); i++){
            if (i == 0){
                authorAvatarUrl = fileUrls.get(i);
            }
            else {
                postImgUrls.add(fileUrls.get(i));
            }
        }

        // details
        PostDetailDo postDetailDo = postDetailMongoMapper.findPostDetailById(postId);

        // convert bo to vo
        return postViewConverter.getVoByBo(
                postViewBo,
                postDetailDo,
                authorAvatarUrl,
                postImgUrls
        );
    }

    @Override
    public List<PostFVo> getPostFVos(@NonNull List<PostVo> list) {
        if (list.isEmpty()){
            return Collections.emptyList();
        }
        return list.stream()
                .map(postVo -> {
                    if (postVo == null){
                        return null;
                    }
                    return new PostFVo(postVo);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<PostPreviewFVo> getPostPreviewFVos(@NonNull List<PostPreviewVo> list) {
        if (list.isEmpty()){
            return Collections.emptyList();
        }
        return list.stream()
                .map(postPreviewVo -> {
                    if (postPreviewVo == null){
                        return null;
                    }
                    return new PostPreviewFVo(postPreviewVo);
                })
                .collect(Collectors.toList());
    }

}
