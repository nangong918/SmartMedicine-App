package com.czy.api.domain.bo.post;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 13225
 * @date 2025/9/5 10:54
 */
@Slf4j
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
    public String postImgFileIdsStr;

    // action   (login_user INNER JOIN user_post_action INNER JOIN post_info)
    public Boolean isLike;  // 改为isLike, 避免与Mysql关键词like冲突
    public Boolean collect; // 取消文件夹创建 (你开发jb那么多功能干jb啥, 别他妈的造无用的轮子)
    public Boolean dislike;

    // 此值不在mysql, 需要去mongoDb查询
//    public List<PostNerResult> nerResults;

    public List<Long> getPostImgFileIds(){
        if (postImgFileIdsStr == null || postImgFileIdsStr.isEmpty()) {
            return Collections.emptyList();
        }
        try {
            return Arrays.stream(postImgFileIdsStr.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(Long::valueOf)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("postImgFileIdsStr转换失败: {}", postImgFileIdsStr, e);
            return Collections.emptyList();
        }
    }
}
