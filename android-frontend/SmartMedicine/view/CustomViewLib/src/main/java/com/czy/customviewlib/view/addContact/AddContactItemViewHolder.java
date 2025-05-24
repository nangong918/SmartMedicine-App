package com.czy.customviewlib.view.addContact;

import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.czy.baseUtilsLib.image.ImageLoadUtil;
import com.czy.customviewlib.databinding.ViewAddContactCardItemBinding;
import com.czy.dal.OnPositionItemClick;
import com.czy.dal.constant.newUserGroup.ApplyButtonStatusEnum;
import com.czy.dal.constant.newUserGroup.HandleButtonStatusEnum;
import com.czy.dal.constant.newUserGroup.HandleStatusEnum;
import com.czy.dal.vo.entity.addContact.AddContactItemVo;

import java.util.List;
import java.util.Optional;


/**
 * @author 13225
 */
public class AddContactItemViewHolder extends RecyclerView.ViewHolder{

    private final ViewAddContactCardItemBinding binding;

    public AddContactItemViewHolder(@NonNull ViewAddContactCardItemBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(AddContactItemVo vo){
        if (vo == null){
            return;
        }

        // 设置初始的 account 值
        binding.tvAccount.setText(vo.account);
        ImageLoadUtil.loadImageViewByUrl(vo.avatarUrlOrUri, binding.imvgAvatar);
        binding.tvName.setText(vo.name);
        binding.tvAccount.setText(vo.account);
        updateAddFriendButton(vo.buttonStates, vo);
    }

    private void updateAddFriendButton(Integer[] buttonState, AddContactItemVo addContactItemVo) {
        if (buttonState == null || buttonState.length == 0){
            Log.w(AddContactItemViewHolder.class.getName(), "isAddedStates is null or empty");
            return;
        }


        if (buttonState.length == 1){
            setButton(binding.btn1, addContactItemVo.isBeAdd, buttonState[0], addContactItemVo, "");
            binding.btn2.setVisibility(View.GONE);
            binding.btn3.setVisibility(View.GONE);
        }
        else {
            setButton(binding.btn1, addContactItemVo.isBeAdd, buttonState[0], addContactItemVo, "");
            try {
                setButton(binding.btn2, addContactItemVo.isBeAdd, buttonState[1], addContactItemVo, "");
            } catch (Exception e){
                Log.e(AddContactItemViewHolder.class.getName(), "binding.btn2 set Error");
            }
            try {
                setButton(binding.btn3, addContactItemVo.isBeAdd, buttonState[2], addContactItemVo, "");
            } catch (Exception e){
                Log.e(AddContactItemViewHolder.class.getName(), "binding.btn3 set Error");
            }
        }
    }

    // TODO 添加内容content
    private void setButton(Button button, boolean isBeAdd, Integer buttonState, AddContactItemVo addContactItemVo, String content){
        int buttonId;
        String buttonContent;
        button.setVisibility(View.VISIBLE);
        // view是被添加的
        if (isBeAdd){
            // 按钮内容
            ApplyButtonStatusEnum buttonType = ApplyButtonStatusEnum.getByCode(buttonState);
            buttonId = buttonType == null ? ApplyButtonStatusEnum.APPLY_ADD.code : buttonType.code;
            buttonContent = buttonType == null ? ApplyButtonStatusEnum.APPLY_ADD.name : buttonType.name;
            Log.i("TAG", "isBeAdd::buttonContent = " + buttonContent);
            binding.btn1.setText(buttonContent);

            // 监听
            setButtonListener(button, buttonId, content, addContactItemVo);
        }
        else {
            // 按钮内容
            HandleButtonStatusEnum buttonType = HandleButtonStatusEnum.getByCode(buttonState);
            buttonId = buttonType == null ? HandleButtonStatusEnum.AGREE.code : buttonType.code;
            buttonContent = buttonType == null ? HandleButtonStatusEnum.AGREE.name : buttonType.name;
            Log.i("TAG", "!isBeAdd::buttonContent = " + buttonContent);
            button.setText(buttonContent);

            // 监听
            setButtonListener(button, buttonId, content, addContactItemVo);
        }
    }

    private void setButtonListener(Button button, int buttonId, String content, AddContactItemVo addContactItemVo){
        // 监听
        button.setOnClickListener(v -> {
            Optional.of(addContactItemVo)
                    .map(vo -> vo.onPositionButtonContentClick)
                    .ifPresent(callback -> {
                        callback.onPositionItemButtonClick(
                                getAdapterPosition(),
                                buttonId,
                                content);
                    });
        });
    }

    public void setPositionClick(OnPositionItemClick onPositionItemClick){
        binding.getRoot().setOnClickListener(v -> onPositionItemClick.onPositionItemClick(getAdapterPosition()));
    }

}
