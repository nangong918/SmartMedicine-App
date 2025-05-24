package com.czy.dal.vo.entity.home;

public class PostVo {

    // 文章图片
    public String postImgUrl = "";

    // 文章标题
    public String postTitle = "";

    // 作者名称
    public String authorName = "";

    // 作者头像
    public String authorAvatarUrl = "";

    // 点赞数量
    public Integer likeNum = 0;
    // 收藏数量
    public Integer collectNum = 0;
    // 评论数量
    public Integer commentNum = 0;
    // 阅读数量（点击数量）
    public Integer readNum = 0;
    // 转发数量
    public Integer forwardNum = 0;
    // 发表时间
    public Long postPublishTimestamp = 0L;

    // 当前用户是否点赞
    public Boolean isLike = false;
    // 当前用户是否收藏
    public Boolean isCollect = false;
    // 当前用户是否不喜欢
    public Boolean isDislike = false;

}
