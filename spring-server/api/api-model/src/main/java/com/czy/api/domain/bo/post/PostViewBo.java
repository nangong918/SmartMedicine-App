package com.czy.api.domain.bo.post;

import lombok.Data;

import java.util.List;

/**
 * @author 13225
 * @date 2025/9/5 10:54
 */
@Data
public class PostViewBo {

    // postId (post_info)
    public Long postId;

    // author (post_info LEFT JOIN login_user)
    public Long authorAvatarFileId;
    public String authorName;
    public Long authorId;

    // post (post_info)
    public String postTitle;
    // 此值不在mysql, 需要去mongoDb查询
//    public List<PostContentEntity> postContents;
    public Long releaseTimestamp;
    // 阅读数量（点击数量） (redis-hash未命中, 此处是查询数据库)
    public Long postViewNum = 0L;
    // 点赞数量
    public Long likeNum = 0L;
    // 收藏数量
    public Long collectNum = 0L;
    // 评论数量
    public Long commentNum = 0L;
    // 转发数量
    public Long forwardNum = 0L;
    // (post_info LEFT JOIN post_files INNER JOIN oss_file -> OssService)
    // 此处的fileIds暂时来自于mysql, 而不是mongoDB 后续需要升级再改为mongodb
    public List<Long> postImgFileIds;

    // action   (login_user INNER JOIN user_post_action INNER JOIN post_info)
    public Boolean like;
    public Boolean collect; // 取消文件夹创建 (你开发jb那么多功能干jb啥, 别他妈的造无用的轮子)
    public Boolean dislike;

    // 此值不在mysql, 需要去mongoDb查询
//    public List<PostNerResult> nerResults;
}
