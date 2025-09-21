package com.czy.appview.view.contact;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.czy.appview.databinding.ViewContactItemBinding;
import com.czy.baseutil.image.ImageLoadUtil;
import com.czy.domain.OnPositionItemClick;
import com.czy.domain.ao.message.ContactItemAo;


/**
 * @author 13225
 */
public class ContactItemViewHolder extends RecyclerView.ViewHolder{

    private final ViewContactItemBinding binding;

    public ContactItemViewHolder(@NonNull ViewContactItemBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(ContactItemAo contactItemAo){
        if (contactItemAo == null){
            return;
        }
        ImageLoadUtil.loadImageViewByResource(contactItemAo.contactItemVo.avatarUrl, binding.imvgAvatar);
        String name = contactItemAo.contactItemVo.remark;
        if (TextUtils.isEmpty(contactItemAo.contactItemVo.remark)){
            name = contactItemAo.contactItemVo.name;
            if (TextUtils.isEmpty(contactItemAo.contactItemVo.name)){
                name = "";
            }
        }
        binding.tvName.setText(name);
    }

    public void setPositionClick(OnPositionItemClick onPositionItemClick){
        binding.getRoot().setOnClickListener(v -> onPositionItemClick.onPositionItemClick(getAdapterPosition()));
    }

}
