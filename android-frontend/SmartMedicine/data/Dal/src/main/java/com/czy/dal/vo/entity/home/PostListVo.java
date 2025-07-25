package com.czy.dal.vo.entity.home;


import com.czy.dal.ao.home.PostAo;

import java.util.ArrayList;
import java.util.List;

public class PostListVo {

    // 所有的list不适用livedata，因为list的元素是固定的，不能动态添加和删除
    public List<PostAo> postAoList = new ArrayList<>();

}
