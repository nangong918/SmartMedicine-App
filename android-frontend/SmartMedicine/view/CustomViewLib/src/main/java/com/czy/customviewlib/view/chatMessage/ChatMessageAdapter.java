package com.czy.customviewlib.view.chatMessage;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;



import com.czy.customviewlib.R;
import com.czy.customviewlib.databinding.ViewReceivedMessageItemBinding;
import com.czy.customviewlib.databinding.ViewSendMessageItemBinding;
import com.czy.dal.vo.entity.chat.ChatMessageDiffCallback;
import com.czy.dal.vo.entity.message.ChatMessageItemVo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author 13225
 * todo 此处需要修改：1.收到消息的头像需要缓存到本地，避免每次都去请求url
 *  2. image的显示存在问题
 *  3. 收到的数据回显都是 发送者（显示错误）
 */
public class ChatMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    public static class SenderViewHolder extends RecyclerView.ViewHolder {
        // 声明发送方对话框的视图组件
        public ViewSendMessageItemBinding binding;
        public SenderViewHolder(ViewSendMessageItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            // 初始化发送方对话框的视图组件

            binding.tvMessage.setText("");
            binding.tvTime.setText("");
            binding.imgvMessage.setVisibility(View.GONE);
        }
    }

    public static class ReceiverViewHolder extends RecyclerView.ViewHolder {
        // 声明接收方对话框的视图组件
        public ViewReceivedMessageItemBinding binding;

        public ReceiverViewHolder(ViewReceivedMessageItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            // 初始化发送方对话框的视图组件

            binding.tvMessage.setText("");
            binding.tvTime.setText("");
            binding.imgvProfile.setImageResource(R.mipmap.logo);
            binding.imgvMessage.setVisibility(View.GONE);
        }
    }

    private final List<ChatMessageItemVo> currentList = new ArrayList<>();

    // 更新View，与当前的view对比然后更新指定的view
    @SuppressLint("NotifyDataSetChanged")
    public void setCurrentList(List<ChatMessageItemVo> newList){
        // 入参为null
        if (newList == null){
            currentList.clear();
            notifyDataSetChanged();
            // 滚动到最底部
            Optional.ofNullable(onSetMessage)
                    .ifPresent(Runnable::run);
            return;
        }
        // 相同地址的情况
        if (newList == currentList){
            // 地址相同直接更新
            notifyDataSetChanged();
        }
        else {
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new ChatMessageDiffCallback(this.currentList, newList));
            this.currentList.clear();
            this.currentList.addAll(newList);
            diffResult.dispatchUpdatesTo(this);
            // TODO BUG此处有问题，暂时使用全部更新Bug
            notifyItemChanged(newList.size() - 1);
        }

        // 滚动到最底部
        Optional.ofNullable(onSetMessage)
                        .ifPresent(Runnable::run);
    }

    public ChatMessageAdapter(){
    }

    //实现不同的viewType
    @Override
    public int getItemViewType(int position) {
        return currentList.get(position).viewType;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (viewType == ChatMessageItemVo.VIEW_TYPE_SENDER) {
            ViewSendMessageItemBinding binding = ViewSendMessageItemBinding.inflate(inflater, parent, false);
            return new SenderViewHolder(binding);
        }
        else{
            ViewReceivedMessageItemBinding binding = ViewReceivedMessageItemBinding.inflate(inflater, parent, false);
            return new ReceiverViewHolder(binding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessageItemVo chatMessageItemVo = currentList.get(position);
        if (chatMessageItemVo.viewType == ChatMessageItemVo.VIEW_TYPE_SENDER){
            // 处理发送方对话框的数据和视图
            ((SenderViewHolder) holder).binding.tvMessage.setText(chatMessageItemVo.content);
            ((SenderViewHolder) holder).binding.tvTime.setText(chatMessageItemVo.time);
            if (chatMessageItemVo.bitmap != null){
                ((SenderViewHolder) holder).binding.imgvMessage.setVisibility(View.VISIBLE);
                ((SenderViewHolder) holder).binding.imgvMessage.setImageBitmap(chatMessageItemVo.bitmap);
            }
            else {
                ((SenderViewHolder) holder).binding.imgvMessage.setVisibility(View.GONE);
            }
            Log.e("Intercep", "chatMessageItemVo.bitmap: " + chatMessageItemVo.bitmap + " position: " + position);
        }
        else {
            // 处理发送方对话框的数据和视图
            ((ReceiverViewHolder) holder).binding.tvMessage.setText(chatMessageItemVo.content);
            ((ReceiverViewHolder) holder).binding.tvTime.setText(chatMessageItemVo.time);
            if (chatMessageItemVo.bitmap != null){
                ((ReceiverViewHolder) holder).binding.imgvMessage.setVisibility(View.VISIBLE);
                ((ReceiverViewHolder) holder).binding.imgvMessage.setImageBitmap(chatMessageItemVo.bitmap);
            }
            else {
                ((ReceiverViewHolder) holder).binding.imgvMessage.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return Optional.ofNullable(this.currentList)
                .map(List::size)
                .orElse(0);
    }

    private Runnable onSetMessage;

    public void setOnSetMessageCallback(Runnable onSetMessage){
        this.onSetMessage = onSetMessage;
    }

}
