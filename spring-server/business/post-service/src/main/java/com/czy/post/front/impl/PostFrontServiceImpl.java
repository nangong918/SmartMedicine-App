package com.czy.post.front.impl;

import com.czy.api.api.oss.OssService;
import com.czy.api.api.user_relationship.UserService;
import com.czy.api.domain.Do.post.comment.PostCommentDo;
import com.czy.api.domain.Do.user.UserDo;
import com.czy.api.domain.ao.post.PostAo;
import com.czy.api.domain.ao.post.PostInfoAo;
import com.czy.api.domain.vo.CommentVo;
import com.czy.api.domain.vo.PostPreviewVo;
import com.czy.api.domain.vo.PostVo;
import com.czy.post.front.PostFrontService;
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

    @Reference(protocol = "dubbo", version = "1.0.0", check = false)
    private OssService ossService;
    @Reference(protocol = "dubbo", version = "1.0.0", check = false)
    private UserService userService;

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
            postPreviewVo.setPostImgUrl(fileUrls.get(i));
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
    public PostVo postAoToPostVo(@NonNull PostAo postAo) {
        PostVo postVo = new PostVo();
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
        postVo.likeNum = PostVo.numToString(postAo.getLikeCount());
        postVo.collectNum = PostVo.numToString(postAo.getCollectCount());
        postVo.commentNum = PostVo.numToString(postAo.getCommentCount());
        postVo.readNum = PostVo.numToString(postAo.getReadCount());
        postVo.forwardNum = PostVo.numToString(postAo.getForwardCount());

        // user属性 TODO
//        postVo.isLike = postAo.getLikeCount() > 0;
//        postVo.isCollect = postAo.getCollectCount() > 0;
//        postVo.isDislike = postAo.getDislikeCount() > 0;

        return postVo;
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

}
