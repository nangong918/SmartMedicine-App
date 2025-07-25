package com.czy.dal.ao.home;


import com.czy.dal.vo.entity.home.PostVo;

import java.util.Optional;

/**
 * @author 13225
 * @date 2025/4/18 17:24
 */
public class PostInfoUrlAo {
    public Long id;
    // authorId；not null（索引）
    public Long authorId;
    public String authorName;
    public String authorAvatarUrl;
    // fileUrl 只展示1个
    public String fileUrl;
    // title；not null
    public String title;
    // releaseTimestamp；not null
    public Long releaseTimestamp;

    // 阅读数；not null
    public Long readCount = 0L;
    // 点赞数；not null
    public Long likeCount = 0L;
    // 收藏数；not null
    public Long collectCount = 0L;
    // 评论数；not null
    public Long commentCount = 0L;
    // 转发数量
    public Long forwardCount = 0L;

    public static PostInfoUrlAo getPostInfoUsrAoByVo(PostVo vo){
        PostInfoUrlAo ao = new PostInfoUrlAo();
        ao.id = vo.postId;
        ao.authorId = vo.authorId;
        ao.authorName = vo.authorName;
        ao.authorAvatarUrl = vo.authorAvatarUrl;
        ao.fileUrl = Optional.ofNullable(vo.postImgUrls)
                .filter(list -> !list.isEmpty())
                .map(list -> list.get(0))
                .orElse("");
        ao.title = vo.postTitle;
        ao.releaseTimestamp = vo.postPublishTimestamp;
        // 这些数据转换存在问题，暂时不加上，因为PostVo的数据被String简化了
//        ao.readCount = Long.parseLong(vo.readNum);
//        ao.likeCount = Long.parseLong(vo.likeNum);
//        ao.collectCount = Long.parseLong(vo.collectNum);
//        ao.commentCount = Long.parseLong(vo.commentNum);
//        ao.forwardCount = Long.parseLong(vo.forwardNum);
        return ao;
    }

}
