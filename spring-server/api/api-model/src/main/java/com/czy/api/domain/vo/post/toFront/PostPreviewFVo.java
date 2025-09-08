package com.czy.api.domain.vo.post.toFront;

import com.czy.api.domain.ao.post.PostNerResult;
import com.czy.api.domain.vo.post.PostPreviewVo;
import com.czy.api.utils.NumberUtils;
import lombok.Data;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 前端需要的Vo
 * Redis存储格式: Hash
 */
@Data
public class PostPreviewFVo implements Serializable, Cloneable {

    public PostPreviewFVo(){}
    public PostPreviewFVo(PostPreviewVo vo){
        this.postId = vo.getPostId();
        this.authorAvatarUrl = vo.getAuthorAvatarUrl();
        this.authorName = vo.getAuthorName();
        this.authorId = vo.getAuthorId();
        this.postTitle = vo.getPostTitle();
        this.postPublishTime = vo.getPostPublishTime();
        this.postViewNum = NumberUtils.numToString(vo.getPostViewNum());
        this.likeNum = NumberUtils.numToString(vo.getLikeNum());
        this.collectNum = NumberUtils.numToString(vo.getCollectNum());
        this.commentNum = NumberUtils.numToString(vo.getCommentNum());
        this.forwardNum = NumberUtils.numToString(vo.getForwardNum());
        this.postImgUrl0 = vo.getPostImgUrl0();
        this.like = vo.getLike();
        this.collect = vo.getCollect();
        this.dislike = vo.getDislike();
        this.nerResults = vo.getNerResults();
        this.timestamp = vo.getTimestamp();
        this.popularity = vo.getPopularity();
        this.score = vo.getScore();
    }

    // postId (post_info)
    public Long postId = null;

    // author (post_info LEFT JOIN login_user)
    public String authorAvatarUrl;
    public String authorName;
    public Long authorId;

    // post (post_info)
    public String postTitle;
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
    // 此处的fileIds暂时来自于mysql, 而不是mongoDB 后续需要升级再改为mongodb
    public String postImgUrl0;

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

    // 排序 (时间， 热度， 推荐评分) (redis获取 / 计算填充; bo的converter无法填充)
    public Long timestamp;
    public Long popularity;
    public Long score;

    @SneakyThrows
    @NotNull
    @Override
    public PostPreviewFVo clone() throws CloneNotSupportedException {
        PostPreviewFVo cloned = (PostPreviewFVo) super.clone();

        // 深克隆 nerResults
        if (this.nerResults != null) {
            List<PostNerResult> clonedNerResults = new ArrayList<>();
            for (PostNerResult result : this.nerResults) {
                clonedNerResults.add(result.clone());
            }
            cloned.nerResults = clonedNerResults;
        }

        return cloned;
    }
}
