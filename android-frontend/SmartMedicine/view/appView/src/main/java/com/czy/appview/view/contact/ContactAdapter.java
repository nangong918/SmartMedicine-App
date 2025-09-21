package com.czy.appview.view.contact;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.czy.appview.databinding.ViewContactItemBinding;
import com.czy.domain.OnPositionItemClick;
import com.czy.domain.ao.message.ContactItemAo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author 13225
 */
public class ContactAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private final OnPositionItemClick onPositionItemClick;
    private final List<ContactItemAo> currentList = new ArrayList<>();


    public ContactAdapter(List<ContactItemAo> contactList,
                          OnPositionItemClick onPositionItemClick) {
        this.currentList.addAll(contactList);
        this.onPositionItemClick = onPositionItemClick;
    }

    // 更新View，与当前的view对比然后更新指定的view
    @SuppressLint("NotifyDataSetChanged")
    public void setCurrentList(List<ContactItemAo> newList){
        this.currentList.clear();
        this.currentList.addAll(newList);
        notifyDataSetChanged();
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
        Optional.of(currentList)
                .filter(list -> list.size() >= position)
                .map(list -> list.get(position))
                .ifPresent(ao -> {
                    ((ContactItemViewHolder)holder).bind(ao);
                    ((ContactItemViewHolder)holder).setPositionClick(onPositionItemClick);
                });
    }

    @Override
    public int getItemCount() {

        return currentList.size();
    }
}
