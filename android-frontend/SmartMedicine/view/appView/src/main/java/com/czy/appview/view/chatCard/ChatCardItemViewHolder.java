package com.czy.appview.view.chatCard;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.czy.baseutil.image.ImageLoadUtil;
import com.czy.appview.databinding.ViewChatCardItemBinding;
import com.czy.domain.OnPositionItemClick;
import com.czy.domain.ao.chat.ChatContactItemAo;


/**
 * @author 13225
 */
public class ChatCardItemViewHolder extends RecyclerView.ViewHolder{

    private final ViewChatCardItemBinding binding;

    public ChatCardItemViewHolder(@NonNull ViewChatCardItemBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(ChatContactItemAo chatContactItemAo){
        ImageLoadUtil.loadImageViewByNetWork(chatContactItemAo.chatContactItemVo.avatarUrlOrUri, binding.imvgAvatar);
        binding.tvName.setText(chatContactItemAo.chatContactItemVo.name);
        binding.tvMessagePreview.setText(chatContactItemAo.chatContactItemVo.messagePreview);
        binding.tvTime.setText(chatContactItemAo.chatContactItemVo.time);
        // 未读消息数量
        binding.vMessagePrompt.setMessageNum(chatContactItemAo.chatContactItemVo.unreadCount);
    }

    public void setPositionClick(OnPositionItemClick onPositionItemClick){
        binding.lyMain.setOnClickListener(v -> {
            if (onPositionItemClick != null){
                onPositionItemClick.onPositionItemClick(getAdapterPosition());
            }
        });
    }
}
