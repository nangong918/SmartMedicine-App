package com.czy.customviewlib.view.contact;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.czy.customviewlib.databinding.ViewContactItemBinding;
import com.czy.dal.OnPositionItemClick;
import com.czy.dal.ao.chat.ChatContactItemAo;
import com.czy.dal.vo.entity.contact.ContactDiffCallback;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author 13225
 */
public class ContactAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private final OnPositionItemClick onPositionItemClick;
    private List<ChatContactItemAo> currentList;


    public ContactAdapter(List<ChatContactItemAo> contactList,
                          OnPositionItemClick onPositionItemClick) {
        this.currentList = new ArrayList<>();
        this.currentList.addAll(contactList);
        this.onPositionItemClick = onPositionItemClick;
    }

    // 更新View，与当前的view对比然后更新指定的view
    @SuppressLint("NotifyDataSetChanged")
    public void setCurrentList(List<ChatContactItemAo> newList){
        if (this.currentList != null){
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new ContactDiffCallback(this.currentList, newList));
            // 清空并添加新的列表
            this.currentList.clear();
            this.currentList.addAll(newList);
            // 通过 diffResult 更新 RecyclerView
            diffResult.dispatchUpdatesTo(this);
        }
        else {
            this.currentList = new ArrayList<>();
            this.currentList.addAll(newList);
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        if (context == null){
            throw new IllegalStateException("Context has been garbage collected");
        }
        ViewContactItemBinding binding = ViewContactItemBinding.inflate(
                LayoutInflater.from(context), parent, false
        );
        return new ContactItemViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatContactItemAo ao = Optional.ofNullable(currentList)
                .filter(list -> list.size() >= position)
                .map(list -> list.get(position))
                .orElse(new ChatContactItemAo());
        ((ContactItemViewHolder)holder).bind(ao);
        ((ContactItemViewHolder)holder).setPositionClick(onPositionItemClick);
    }

    @Override
    public int getItemCount() {

        return Optional.ofNullable(currentList)
                .map(List::size)
                .orElse(0);
    }
}
