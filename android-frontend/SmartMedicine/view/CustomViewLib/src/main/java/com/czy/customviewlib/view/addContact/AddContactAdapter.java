package com.czy.customviewlib.view.addContact;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.czy.customviewlib.databinding.ViewAddContactCardItemBinding;

import com.czy.dal.OnPositionItemClick;
import com.czy.dal.vo.entity.addContact.AddContactDiffCallback;
import com.czy.dal.vo.entity.addContact.AddContactItemVo;

import java.util.List;
import java.util.Optional;

/**
 * @author 13225
 */
public class AddContactAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private final OnPositionItemClick onPositionItemClick;
    private List<AddContactItemVo> currentList;

    public AddContactAdapter(List<AddContactItemVo> contactListVo,
                             OnPositionItemClick onPositionItemClick) {
        this.currentList = contactListVo;
        this.onPositionItemClick = onPositionItemClick;
    }

    // 更新View，与当前的view对比然后更新指定的view
    @SuppressLint("NotifyDataSetChanged")
    public void setChatItems(List<AddContactItemVo> newList){
        // 观察 contactItemList 的变化
        if (currentList != null) {
            // 暂时取消DiffUtil，测试总是出bug，属于过度开发；归为性能优化点
//            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new AddContactDiffCallback(currentList, newList));

            currentList.clear();
            currentList.addAll(newList);

//            diffResult.dispatchUpdatesTo(this);
            notifyDataSetChanged();
        }
        else {
            currentList = newList;
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
        ViewAddContactCardItemBinding binding = ViewAddContactCardItemBinding.inflate(
                LayoutInflater.from(context), parent, false
        );
        return new AddContactItemViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        AddContactItemVo vo = Optional.ofNullable(currentList)
                .filter(list -> list.size() >= position)
                .map(list -> list.get(position))
                .orElse(new AddContactItemVo());
        ((AddContactItemViewHolder)holder).bind(vo);
        ((AddContactItemViewHolder)holder).setPositionClick(onPositionItemClick);
    }

    @Override
    public int getItemCount() {

        return Optional.ofNullable(currentList)
                .map(List::size)
                .orElse(0);
    }
}
