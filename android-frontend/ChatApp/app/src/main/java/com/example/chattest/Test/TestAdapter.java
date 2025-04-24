package com.example.chattest.Test;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chattest.Chat.recyclerview.ChatAdapter;
import com.example.chattest.R;
import com.example.chattest.databinding.ItemReceviedMessageBinding;
import com.example.chattest.databinding.MyviewRecommendCardBinding;

public class TestAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    //-------------------------------ItemList----------------------------------



    public static class Item{
        public static final int TestNum = 30;
    }



    //--------------------------------Adapter----------------------------------


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemReceviedMessageBinding binding = ItemReceviedMessageBinding.inflate(inflater, parent, false);
        return new ReceiverViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((ReceiverViewHolder) holder).binding.textSendMessage.setText("1");
        ((ReceiverViewHolder) holder).binding.textDateTime.setText("2");
    }

    @Override
    public int getItemCount() {
        return Item.TestNum;
    }


    //----------------------------ViewHolder-------------------------------------


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


    private static class MyViewHolder extends RecyclerView.ViewHolder{
        public MyviewRecommendCardBinding binding;
        public MyViewHolder(@NonNull MyviewRecommendCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }


}
