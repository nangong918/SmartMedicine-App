package com.example.chattest.Chat.recyclerview;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chattest.R;
import com.example.chattest.databinding.ItemReceviedMessageBinding;
import com.example.chattest.databinding.ItemSentMessageBinding;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    public static class SenderViewHolder extends RecyclerView.ViewHolder {
        // 声明发送方对话框的视图组件
        public ItemSentMessageBinding binding;
        public SenderViewHolder(ItemSentMessageBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            // 初始化发送方对话框的视图组件

            binding.textSendMessage.setText("");
            binding.textDateTime.setText("");
        }
    }

    public static class ReceiverViewHolder extends RecyclerView.ViewHolder {
        // 声明接收方对话框的视图组件
        public ItemReceviedMessageBinding binding;

        public ReceiverViewHolder(ItemReceviedMessageBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            // 初始化发送方对话框的视图组件

            binding.textSendMessage.setText("");
            binding.textDateTime.setText("");
            binding.imageProfile.setImageResource(R.drawable.chat_ai);
        }
    }

    public static class ChatItem{
        public int viewType;
        public String Message;
        public String Date;
    }

    private List<ChatItem> chatItems;

    public ChatAdapter(List<ChatItem> chatItems){
        this.chatItems = chatItems;
    }

    //实现不同的viewType
    @Override
    public int getItemViewType(int position) {
        return chatItems.get(position).viewType;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (viewType == 0) {
            ItemSentMessageBinding binding = ItemSentMessageBinding.inflate(inflater, parent, false);
            return new SenderViewHolder(binding);
        }
        else{
            ItemReceviedMessageBinding binding = ItemReceviedMessageBinding.inflate(inflater, parent, false);
            return new ReceiverViewHolder(binding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatItem chatItem = chatItems.get(position);
        if (chatItem.viewType == 0){
            // 处理发送方对话框的数据和视图
            ((SenderViewHolder) holder).binding.textSendMessage.setText(chatItem.Message);
            ((SenderViewHolder) holder).binding.textDateTime.setText(chatItem.Date);
        }
        else{
            // 处理发送方对话框的数据和视图
            ((ReceiverViewHolder) holder).binding.textSendMessage.setText(chatItem.Message);
            ((ReceiverViewHolder) holder).binding.textDateTime.setText(chatItem.Date);
        }
    }

    @Override
    public int getItemCount() {
        return chatItems.size();
    }
}
