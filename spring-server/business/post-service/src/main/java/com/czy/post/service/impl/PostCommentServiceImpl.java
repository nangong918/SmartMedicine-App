package com.czy.post.service.impl;

import com.czy.api.api.user.UserService;
import com.czy.api.domain.Do.post.comment.PostCommentDo;
import com.czy.api.domain.Do.user.UserDo;
import com.czy.api.domain.ao.post.PostCommentAo;
import com.czy.post.mapper.mongo.PostCommentMongoMapper;
import com.czy.post.service.PostCommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author 13225
 * @date 2025/4/24 11:59
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class PostCommentServiceImpl implements PostCommentService {

    private final PostCommentMongoMapper postCommentMongoMapper;
    @Reference(protocol = "dubbo", version = "1.0.0", check = false)
    private UserService userService;

    @Override
    public List<PostCommentDo> getLevel1PostComments(Long postId, Integer pageSize, Integer pageNum) {
        return postCommentMongoMapper.findLevel1CommentsByPostIdAndPaging(postId, pageSize, pageNum);
    }

    @Override
    public List<PostCommentDo> getLevel2PostComments(Long postId, Long level2CommentId, Integer pageSize, Integer pageNum) {
        int finalPageNum = pageNum;
        if (finalPageNum < 10){
            finalPageNum = 10;
        }
        else if (finalPageNum > 20){
            finalPageNum = 20;
        }
        return postCommentMongoMapper.findLevel2CommentsByPostIdAndReplyCommentIdPaging(postId, level2CommentId, pageSize, finalPageNum);
    }

    @Override
    public PostCommentDo getPostCommentById(Long commentId) {
        return postCommentMongoMapper.findCommentById(commentId);
    }

    @Override
    public List<PostCommentAo> getPostCommentAoList(List<PostCommentDo> postCommentDoList) {
        if (CollectionUtils.isEmpty(postCommentDoList)){
            return new ArrayList<>();
        }
        // null也需要保存，保证list长度一致
        // 保留null以确保列表长度一致
        List<Long> commenterIds = postCommentDoList.stream()
                .map(postComment -> postComment == null ? null : postComment.getCommenterId())
                .collect(Collectors.toList());

        // 可能会去除null对象，为了position一一对应，需要适用
        List<UserDo> userDos = userService.getByUserIds(commenterIds);
        // 转换为 Map<Integer, UserDo> 避免双重for循环的O(n*n),而是O(m+n)
        // key是userId也就是Map<UserId,UserDo> userMap
        Map<Long, UserDo> userMap = userDos.stream()
                .collect(Collectors.toMap(UserDo::getId, user -> user, (existing, replacement) -> existing));
        List<UserDo> allUserDos = new ArrayList<>(commenterIds.size());

        // 生成存在null的allUserDos
        for(int i = 0; i < postCommentDoList.size(); i++){
            if (commenterIds.get(i) != null && userMap.get(commenterIds.get(i)) != null){
                allUserDos.add(userMap.get(commenterIds.get(i)));
            }
            else {
                allUserDos.add(null);
            }
        }

        // 生成一个存在null的list
        List<PostCommentAo> postCommentAoList = new ArrayList<>();
        for (int i = 0; i < postCommentDoList.size(); i++) {
            PostCommentDo postCommentDo = postCommentDoList.get(i);
            if (postCommentDo == null){
                postCommentAoList.add(null);
                continue;
            }
            PostCommentAo ao = new PostCommentAo();
            ao.setPostId(postCommentDo.getPostId());
            ao.setCommenterId(postCommentDo.getCommenterId());
            ao.setContent(postCommentDo.getContent());
            ao.setTimestamp(postCommentDo.getTimestamp());

            if (allUserDos.get(i) != null){
                ao.setCommenterAccount(allUserDos.get(i).getAccount());
                ao.setCommenterName(allUserDos.get(i).getUserName());
                ao.setCommenterAvatarFileId(allUserDos.get(i).getAvatarFileId());
                ao.setReplyCommentId(postCommentDo.getReplyCommentId());
            }
            postCommentAoList.add(ao);
        }
        return postCommentAoList;
    }
}
