package com.czy.domain.vo.entity.home;

import com.czy.domain.constant.search.PostSearchResultListEnum;

public class PostExVo {
    /**
     * like匹配结果 0
     * tokenized匹配结果 1
     * similar匹配结果 2
     * recommend匹配结果 3
     * @see PostSearchResultListEnum
     */
    public int type = PostSearchResultListEnum.LIKE_MATCH_RESULT.getValue();
    public PostVo postVo;

    public PostExVo() {
    }

    public PostExVo(PostVo postVo) {
        this.postVo = postVo;
    }

    public void setPostVo(PostVo postVo){
        this.postVo = postVo;
    }

    public void clonePostVo(PostVo postVo) throws CloneNotSupportedException{
        this.postVo = postVo.clone();
    }
}
