package com.czy.dal.vo.entity.chat;

import androidx.recyclerview.widget.DiffUtil;

import com.czy.dal.vo.entity.message.ChatMessageItemVo;

import java.util.List;
import java.util.Optional;

public class ChatMessageDiffCallback extends DiffUtil.Callback{

    private final List<ChatMessageItemVo> oldList;
    private final List<ChatMessageItemVo> newList;

    public ChatMessageDiffCallback(List<ChatMessageItemVo> oldList, List<ChatMessageItemVo> newList) {
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
