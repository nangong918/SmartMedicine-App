package com.czy.baseUtilsLib.algorithm;


import java.util.List;

public class SortUtils {

    /**
     * 二分查找找到插入位置
     *  二分查找：O(log m)，其中 m 是 chatList 的大小。
     *  插入操作：O(m)，在最坏情况下可能需要移动元素。
     *  总体时间复杂度：O(n + m)
     *  O(n)，用于存储 timestampItemMap 和 chatList
     * 条件：对有序list进行排序
     * @param index 索引
     * @return  插入位置
     */
    public static <T extends SortItem> int findInsertPosition(long index, List<T> sortItemList) {
        int low = 0, high = sortItemList.size() - 1;

        while (low <= high) {
            int mid = (low + high) / 2;
            if (sortItemList.get(mid).index < index) {
                low = mid + 1; // 向右查找
            } else {
                high = mid - 1; // 向左查找
            }
        }
        return low; // 返回插入位置
    }

    public static <T extends SortItem> Integer findInsertPosition(T item, List<T> sortItemList){
        if (item == null) {
            return null; // 如果 item 为 null，返回 null
        }

        int low = 0, high = sortItemList.size() - 1;

        while (low <= high) {
            int mid = (low + high) / 2;
            T midItem = sortItemList.get(mid);

            // 检查是否存在相同的消息
            if (item.equals(midItem)) {
                return null; // 找到相同的消息，返回 null
            }

            if (midItem.index < item.index) {
                low = mid + 1; // 向右查找
            } else {
                high = mid - 1; // 向左查找
            }
        }
        return low; // 返回插入位置
    }

}
