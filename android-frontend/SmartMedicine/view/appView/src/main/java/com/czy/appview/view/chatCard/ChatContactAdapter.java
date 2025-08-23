package com.czy.appview.view.chatCard;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.czy.appview.databinding.ViewChatCardItemBinding;
import com.czy.dal.OnPositionItemClick;
import com.czy.dal.ao.chat.ChatContactItemAo;
import com.czy.dal.vo.entity.contact.ContactDiffCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author 13225
 */
public class ChatContactAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private final List<ChatContactItemAo> currentList = new ArrayList<>();
    private final OnPositionItemClick onPositionItemClick;


    public ChatContactAdapter(OnPositionItemClick onPositionItemClick){
        this.onPositionItemClick = onPositionItemClick;
    }

    // 更新View，与当前的view对比然后更新指定的view
    @SuppressLint("NotifyDataSetChanged")
    public void setCurrentList(List<ChatContactItemAo> newList){
        // 入参为null
        if (newList == null){
            currentList.clear();
            notifyDataSetChanged();
            return;
        }
        // 相同地址的情况
        if (newList == currentList){
            // 地址相同直接更新
            notifyDataSetChanged();
        }
        else {
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new ContactDiffCallback(this.currentList, newList), true);
            // 清空并添加新的列表
            this.currentList.clear();
            this.currentList.addAll(newList);
            // 通过 diffResult 更新 RecyclerView
            diffResult.dispatchUpdatesTo(this);
            // TODO BUG此处有问题，暂时使用全部更新Bug
            notifyItemChanged(newList.size() - 1);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        ViewChatCardItemBinding binding = ViewChatCardItemBinding.inflate(
                LayoutInflater.from(context), parent, false
        );
        return new ChatCardItemViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatContactItemAo chatContactItemAo = Optional.ofNullable(currentList)
                .filter(list -> list.size() >= position)
                .map(list -> list.get(position))
                .orElse(new ChatContactItemAo());
        ((ChatCardItemViewHolder)holder).bind(chatContactItemAo);
        ((ChatCardItemViewHolder)holder).setPositionClick(onPositionItemClick);
    }

    @Override
    public int getItemCount() {
        return Optional.of(currentList)
                .map(List::size)
                .orElse(0);
    }
}
