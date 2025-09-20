package com.czy.domain.vo.entity.home;

import com.czy.baseutil.json.BaseBean;
import com.czy.domain.constant.search.PostSearchResultListEnum;

import java.io.Serializable;

public class PostPreviewExVo implements Serializable, BaseBean {
    /**
     * like匹配结果 0
     * tokenized匹配结果 1
     * similar匹配结果 2
     * recommend匹配结果 3
     * @see PostSearchResultListEnum
     */
    public int type = PostSearchResultListEnum.LIKE_MATCH_RESULT.getValue();
    public PostPreviewVo postPreviewVo;

    public PostPreviewExVo() {
    }

    public PostPreviewExVo(PostPreviewVo postPreviewVo) {
        this.postPreviewVo = postPreviewVo;
    }

    public void setPostPreviewVo(PostPreviewVo postPreviewVo){
        this.postPreviewVo = postPreviewVo;
    }

    public void clonePostPreviewVo(PostPreviewVo postPreviewVo) throws CloneNotSupportedException{
        this.postPreviewVo = postPreviewVo.clone();
    }
}
