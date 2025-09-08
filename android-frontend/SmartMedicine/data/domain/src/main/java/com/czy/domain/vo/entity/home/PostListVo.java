package com.czy.domain.vo.entity.home;


import com.czy.domain.ao.home.PostPreviewAo;

import java.util.ArrayList;
import java.util.List;

public class PostListVo {

    // 所有的list不适用livedata，因为list的元素是固定的，不能动态添加和删除
    public List<PostPreviewAo> postPreviewAoList = new ArrayList<>();

}
