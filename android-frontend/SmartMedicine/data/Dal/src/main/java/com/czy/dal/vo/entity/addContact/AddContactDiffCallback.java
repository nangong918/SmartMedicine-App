package com.czy.dal.vo.entity.addContact;

import android.util.Log;

import androidx.recyclerview.widget.DiffUtil;


import java.util.List;
import java.util.Optional;

public class AddContactDiffCallback extends DiffUtil.Callback{

    private final List<AddContactItemVo> oldList;
    private final List<AddContactItemVo> newList;

    public AddContactDiffCallback(List<AddContactItemVo> oldList, List<AddContactItemVo> newList) {
        this.oldList = oldList;
        this.newList = newList;
        Log.d("areItemsTheSame", "init: ");
    }


    @Override
    public int getOldListSize() {
        return Optional.ofNullable(oldList)
                .map(List::size)
                .orElse(0);
    }

    @Override
    public int getNewListSize() {
        return Optional.ofNullable(newList)
                .map(List::size)
                .orElse(0);
    }

    // DiffUtil先比较两个Item是否是同一类型的item；就是用来索引两个item是否需要比较
    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
//        // 使用 equals 方法比较内容
//        return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
        // 通过 account 判断是否是同一个 item
        return oldList.get(oldItemPosition).isItemEquals(newList.get(newItemPosition));
    }

    // 用来比较两个item是否相同
    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
//        // 需要重写 equals 方法
//        return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
        return oldList.get(oldItemPosition).isContentEquals(newList.get(newItemPosition));
    }
}
