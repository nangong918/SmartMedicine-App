package com.czy.post.front;

import com.czy.api.domain.Do.post.comment.PostCommentDo;
import com.czy.api.domain.ao.post.PostAo;
import com.czy.api.domain.ao.post.PostInfoAo;
import com.czy.api.domain.vo.CommentVo;
import com.czy.api.domain.vo.PostPreviewVo;
import com.czy.api.domain.vo.PostVo;

import java.util.List;

/**
 * @author 13225
 * @date 2025/6/6 16:38
 * 转换成前端需要的类型
 */
public interface PostFrontService {
    /**
     * 转换成前端需要的类型
     * @param postAoList    postAoList
     * @return              List<PostPreviewVo>
     */
    List<PostPreviewVo> toPostPreviewVoList(List<PostInfoAo> postAoList);

    // PostAo -> PostVo
    PostVo postAoToPostVo(PostAo postAo);

    // List<PostCommentDo> -> List<CommentVo>
    List<CommentVo> getCommentVosByPostCommentDos(List<PostCommentDo> postCommentDos);
}
