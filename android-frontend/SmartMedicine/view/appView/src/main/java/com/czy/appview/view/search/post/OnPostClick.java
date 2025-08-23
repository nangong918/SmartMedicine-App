package com.czy.appview.view.search.post;

public interface OnPostClick {
    /**
     * 点击帖子
     * @param position  位置
     * @param postId    帖子id
     */
    void onPostClick(int position, Long postId);
}
