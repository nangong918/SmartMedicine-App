package com.czy.domain.vo.entity.message.contact;


import androidx.recyclerview.widget.DiffUtil;

import com.czy.domain.ao.message.ChatContactItemAo;

import java.util.List;
import java.util.Optional;

public class ContactDiffCallback extends DiffUtil.Callback{

    private final List<ChatContactItemAo> oldList;
    private final List<ChatContactItemAo> newList;

    public ContactDiffCallback(List<ChatContactItemAo> oldList, List<ChatContactItemAo> newList) {
        this.oldList = oldList;
        this.newList = newList;
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

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).isItemEquals(newList.get(newItemPosition));
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).isContentEquals(newList.get(newItemPosition));
    }
}
