package com.czy.api.domain.vo.post.aaa;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.List;

/**
 * 前端需要的Vo
 */
@Data
public class PostVo implements Serializable, Cloneable {

    // postId (post_info)
    public Long postId = null;

    // author (post_info LEFT JOIN login_user)
    public String authorAvatarUrl;
    public String authorName;

    // post (post_info)
    public String postTitle;
    public String postContent;
    public String postPublishTime; // yyyy-MM-dd HH:mm:ss
    // 阅读数量（点击数量） (redis-hash)
    public String postViewNum = "0";
    // 点赞数量
    public String likeNum = "0";
    // 收藏数量
    public String collectNum = "0";
    // 评论数量
    public String commentNum = "0";
    // 转发数量
    public String forwardNum = "0";
    // (post_info LEFT JOIN post_files INNER JOIN post_file -> OssService)
    public List<String> postImgUrls;

    // action   (login_user INNER JOIN user_post_action INNER JOIN post_info)
    public Boolean like;
    public Boolean collect; // 取消文件夹创建 (你开发jb那么多功能干jb啥, 别他妈的造无用的轮子)
    public Boolean dislike;

    /*
        ner labels
        vo 不需要标签, ner属于是post画像,
        但是vo是已经在feature-service拿到postId之后回调的接口,
        已经属于recommend之后的行为, 无需ner
        并且此处vo的功能单纯就是 id -> vo 可以一个bo联合全部查询出来
     */

    // 排序 (时间， 热度， 推荐评分) (redis获取 / 计算填充)
    public Long timestamp;
    public Long popularity;
    public Long score;

    @NotNull
    @Override
    public PostVo clone() throws CloneNotSupportedException {
        return (PostVo) super.clone();
    }
}
