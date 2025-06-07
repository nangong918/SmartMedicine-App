package com.czy.api.domain.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 13225
 * @date 2025/6/6 11:49
 * Post预览vo
 */
@Data
public class PostPreviewVo implements Serializable {
    // postId
    public Long postId = null;

    // 文章图片
    public String postImgUrl = null;

    // 文章标题
    public String postTitle = "";

    // 作者id
    public Long authorId = null;

    // 作者名称
    public String authorName = "";

    // 作者头像
    public String authorAvatarUrl = "";

    // 点赞数量
    public String likeNum = "0";
    // 收藏数量
    public String collectNum = "0";
    // 评论数量
    public String commentNum = "0";
    // 阅读数量（点击数量）
    public String readNum = "0";
    // 转发数量
    public String forwardNum = "0";
    // 发表时间
    public Long postPublishTimestamp = 0L;

    // 当前用户是否点赞
    public Boolean isLike = false;
    // 当前用户是否收藏
    public Boolean isCollect = false;
    // 当前用户是否不喜欢
    public Boolean isDislike = false;

    public static String numToString(Long num) {
        if (num < 0) {
            return "Invalid number";
        }

        // 大于 1000M展示为 1B；比如105643909 -> 1.0B
        if (num >= 1_000_000_000) {
            return String.format("%.1fB", num / 1_000_000_000.0);
        }
        // 大于 1000k展示为 1M；比如105643 -> 1.0M
        else if (num >= 1_000_000) {
            return String.format("%.1fM", num / 1_000_000.0);
        }
        // 大于 1000k展示为 1K；比如1105 -> 1.1K
        else if (num >= 1_000) {
            return String.format("%.1fK", num / 1_000.0);
        } else {
            return num.toString();
        }
    }

    public static void main(String[] args) {
        Long num = 1056439090L;
        System.out.println(numToString(num));
    }
}
