package com.czy.appcore.service.post;

import com.czy.baseutil.algorithm.SortItem;

import java.util.List;

/**
 * 列表数据改变监听
 * 排序方式: 推荐index: 不参考
 *          热门index: heat
 *          社区index: timestamp
 * @param <T>   PostAo / PostVo;
 */
public interface OnPostChange<T extends SortItem> {
    /**
     * 只会返回ui需要更新的
     * @param list              list指针
     * @param beforeSize        原先的长度 (改变起始位置)
     * @param changeSize        改变了的长度 (改变结束位置; 总长度 = 原先的长度 + 改变了的长度)
     */
    void onChange(List<T> list, int beforeSize, int changeSize);

    /**
     * 所有数据改变
     * @param list              list指针
     */
    void allChange(List<T> list);
}
