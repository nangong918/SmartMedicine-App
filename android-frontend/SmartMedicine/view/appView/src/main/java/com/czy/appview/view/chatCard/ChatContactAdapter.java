package com.czy.appview.view.chatCard;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.czy.appview.databinding.ViewChatCardItemBinding;
import com.czy.domain.OnPositionItemClick;
import com.czy.domain.ao.message.ChatContactItemAo;

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
    public void setCurrentList(@NonNull List<ChatContactItemAo> newList){
        currentList.clear();
        currentList.addAll(newList);
        notifyDataSetChanged();
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
        Optional.of(currentList)
                .filter(list -> list.size() >= position)
                .map(list -> list.get(position))
                .ifPresent(ao -> {
                    ((ChatCardItemViewHolder)holder).bind(ao);
                    ((ChatCardItemViewHolder)holder).setPositionClick(onPositionItemClick);
                });
    }

    @Override
    public int getItemCount() {
        return currentList.size();
    }
}
