package com.czy.post.front;

import com.czy.api.domain.ao.post.PostAo;
import com.czy.api.domain.ao.post.PostInfoAo;
import com.czy.api.domain.vo.post.PostPreviewVo;
import com.czy.api.domain.vo.post.PostVo;
import com.czy.api.domain.vo.post.old.PostOldVo;
import com.czy.api.domain.vo.post.toFront.PostFVo;
import com.czy.api.domain.vo.post.toFront.PostPreviewFVo;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author 13225
 * @date 2025/6/6 16:38
 * 转换成前端需要的类型
 */
public interface PostFrontService {
    /**
     * 转换成前端需要的类型    (会过滤null, 不保证与入参数量相等)
     * @param postAoList    postAoList
     * @return              List<PostPreviewVo>
     */
    @NotNull
    List<PostPreviewVo> toPostPreviewVoList(List<PostInfoAo> postAoList, @NonNull Long userId);

    // PostAo -> PostVo
    PostOldVo postAoToPostVo(PostAo postAo);

    /**
     * 获取帖子Vo
     * @param postId    postId
     * @param userId    userId
     * @return          PostVo
     */
    PostVo getPostVo(Long postId, Long userId);

    List<PostFVo> getPostFVos(@NonNull List<PostVo> list);

    List<PostPreviewFVo> getPostPreviewFVos(@NonNull List<PostPreviewVo> list);
}
