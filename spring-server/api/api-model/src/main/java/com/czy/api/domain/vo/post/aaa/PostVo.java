package com.czy.api.domain.vo.post.aaa;

import com.czy.api.domain.Do.post.post.content.PostContentEntity;
import com.czy.api.domain.ao.post.PostNerResult;
import lombok.Data;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 前端需要的Vo
 * Redis存储格式: Hash
 */
@Data
public class PostVo implements Serializable, Cloneable {

    // postId (post_info)
    public Long postId = null;

    // author (post_info LEFT JOIN login_user)
    public String authorAvatarUrl;
    public String authorName;
    public Long authorId;

    // post (post_info)
    public String postTitle;
    // mongoDb 获取内容
    public List<PostContentEntity> postContents;
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
        虽然但是, mongoDB能查询到, 那就都给他算了
     */
    public List<PostNerResult> nerResults;

    // 排序 (时间， 热度， 推荐评分) (redis获取 / 计算填充)
    public Long timestamp;
    public Long popularity;
    public Long score;

    @SneakyThrows
    @NotNull
    @Override
    public PostVo clone() throws CloneNotSupportedException {
        PostVo cloned = (PostVo) super.clone();

        // 深克隆 postContents
        if (this.postContents != null) {
            cloned.postContents = this.postContents.stream()
                    .map(PostContentEntity::clone)
                    .collect(Collectors.toList());
        }

        // 深克隆 nerResults
        if (this.nerResults != null) {
            cloned.nerResults = this.nerResults.stream()
                    .map(PostNerResult::clone)
                    .collect(Collectors.toList());
        }

        // 深克隆 postImgUrls（如果需要）
        if (this.postImgUrls != null) {
            cloned.postImgUrls = new ArrayList<>(this.postImgUrls);
        }

        return cloned;
    }
}
