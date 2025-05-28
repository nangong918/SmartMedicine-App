package com.czy.customviewlib.view.contact;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.czy.baseUtilsLib.image.ImageLoadUtil;
import com.czy.customviewlib.databinding.ViewContactItemBinding;
import com.czy.dal.OnPositionItemClick;
import com.czy.dal.ao.chat.ChatContactItemAo;
import com.czy.dal.vo.entity.contact.ContactItemVo;


/**
 * @author 13225
 */
public class ContactItemViewHolder extends RecyclerView.ViewHolder{

    private final ViewContactItemBinding binding;

    public ContactItemViewHolder(@NonNull ViewContactItemBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(ChatContactItemAo chatContactItemAo){
        if (chatContactItemAo == null){
            return;
        }
        ImageLoadUtil.loadImageViewByResource(chatContactItemAo.chatContactItemVo.avatarUrlOrUri, binding.imvgAvatar);
        binding.tvName.setText(chatContactItemAo.chatContactItemVo.name);
    }

    public void setPositionClick(OnPositionItemClick onPositionItemClick){
        binding.getRoot().setOnClickListener(v -> onPositionItemClick.onPositionItemClick(getAdapterPosition()));
    }

}
