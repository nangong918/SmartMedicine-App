package com.czy.dal.vo.entity.home;

import com.czy.dal.constant.search.PostSearchResultListEnum;

public class PostExVo extends PostVo{
    /**
     * like匹配结果 0
     * tokenized匹配结果 1
     * similar匹配结果 2
     * recommend匹配结果 3
     * @see PostSearchResultListEnum
     */
    public int type = PostSearchResultListEnum.LIKE_MATCH_RESULT.getValue();

    public void setPostVo(PostVo vo){
        super.setByPostVo(vo);
    }
}
