package com.czy.post.front.impl;

import com.api.mapper.post.mongo.PostDetailMongoMapper;
import com.api.mapper.post.mybatis.bo.PostViewBoMapper;
import com.czy.api.api.user.user.UserService;
import com.czy.api.converter.domain.post.PostViewConverter;
import com.czy.api.domain.Do.post.comment.PostCommentDo;
import com.czy.api.domain.Do.post.post.PostDetailDo;
import com.czy.api.domain.Do.user.UserDo;
import com.czy.api.domain.ao.post.PostAo;
import com.czy.api.domain.ao.post.PostInfoAo;
import com.czy.api.domain.bo.post.PostViewBo;
import com.czy.api.domain.vo.post.CommentVo;
import com.czy.api.domain.vo.post.PostOldVo;
import com.czy.api.domain.vo.post.PostPreviewVo;
import com.czy.api.domain.vo.post.aaa.PostVo;
import com.czy.post.front.PostFrontService;
import com.czy.post.service.PostService;
import com.utils.minio.service.OssService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

    @Override
    public List<PostPreviewVo> toPostPreviewVoList(List<PostInfoAo> postAoList){

        List<Long> fileIds = new ArrayList<>(postAoList.size());
        for (PostInfoAo postInfoAo : postAoList){
            if (postInfoAo == null){
                fileIds.add(null);
                continue;
            }
            fileIds.add(postInfoAo.getFileId());
        }

        long postUrlStartTime = System.currentTimeMillis();
        List<String> fileUrls = ossService.getFileUrlsByFileIds(fileIds);
        // TODO 时间过长需要将url拆分存储在redis中
        log.info("获取post的imageUrl耗时：{} ms", System.currentTimeMillis() - postUrlStartTime);

        List<Long> authorIds = new ArrayList<>(postAoList.size());
        for (PostInfoAo postInfoAo : postAoList){
            if (postInfoAo == null){
                authorIds.add(null);
                continue;
            }
            authorIds.add(postInfoAo.getAuthorId());
        }
        // author信息 可能存在null
        List<UserDo> userDoList = new ArrayList<>(authorIds.size());
        for (Long authorId : authorIds){
            if (authorId == null){
                userDoList.add(null);
                continue;
            }
            UserDo userDo = userService.getUserById(authorId);
            userDoList.add(userDo);
        }
        List<Long> userImgIds = new ArrayList<>(authorIds.size());
        for (UserDo userDo : userDoList){
            if (userDo == null){
                userImgIds.add(null);
                continue;
            }
            userImgIds.add(userDo.getAvatarFileId());
        }
        long userImgUrlStartTime = System.currentTimeMillis();
        List<String> userImgUrls = ossService.getFileUrlsByFileIds(userImgIds);
        // TODO 时间过长需要将url拆分存储在redis中
        log.info("获取post的imageUrl耗时：{} ms", System.currentTimeMillis() - userImgUrlStartTime);

        List<PostPreviewVo> postPreviewVos = new ArrayList<>(postAoList.size());

        for (int i = 0; i < postAoList.size(); i++){
            PostInfoAo postInfoAo = postAoList.get(i);
            if (postInfoAo == null){
                postPreviewVos.add(null);
                continue;
            }
            PostPreviewVo postPreviewVo = new PostPreviewVo();
            postPreviewVo.setPostId(postInfoAo.getId());
            List<String> postImgUrls = new ArrayList<>();
            postImgUrls.add(fileUrls.get(i));
            postPreviewVo.setPostImgUrls(postImgUrls);
            postPreviewVo.setPostTitle(postInfoAo.getTitle());
            postPreviewVo.setAuthorId(postInfoAo.getAuthorId());
            String authorName = null;
            if (!CollectionUtils.isEmpty(userDoList)){
                authorName = Optional.ofNullable(userDoList.get(i))
                        .map(UserDo::getUserName)
                        .orElse(null);
            }
            postPreviewVo.setAuthorName(
                    authorName
            );
            String url = null;
            if (!CollectionUtils.isEmpty(userImgUrls)){
                url = userImgUrls.get(i);
            }
            postPreviewVo.setAuthorAvatarUrl(
                    url
            );
            postPreviewVo.setLikeNum(PostPreviewVo.numToString(postInfoAo.getLikeCount()));
            postPreviewVo.setCollectNum(PostPreviewVo.numToString(postInfoAo.getCollectCount()));
            postPreviewVo.setCommentNum(PostPreviewVo.numToString(postInfoAo.getCommentCount()));
//            postPreviewVo.setReadNum(PostPreviewVo.numToString(postInfoAo.getReadCount()));
            postPreviewVo.setForwardNum(PostPreviewVo.numToString(postInfoAo.getForwardCount()));
            postPreviewVo.setPostPublishTimestamp(postInfoAo.getReleaseTimestamp());

            // TODO user的阅读状态

            postPreviewVos.add(postPreviewVo);
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

    @Override
    public PostOldVo getPostVo(Long postId) {
        PostAo postAo = postService.findPostById(postId);
        if (postAo != null && postAo.getId() != null){
            return postAoToPostVo(postAo);
        }
        return null;
    }

    @Override
    public List<CommentVo> getCommentVosByPostCommentDos(List<PostCommentDo> postCommentDos) {
        return postCommentDos.stream()
                .map(this::getCommentVo)
                .collect(Collectors.toList());
    }

    private CommentVo getCommentVo(PostCommentDo postCommentDo) {
        CommentVo commentVo = new CommentVo();
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

}
