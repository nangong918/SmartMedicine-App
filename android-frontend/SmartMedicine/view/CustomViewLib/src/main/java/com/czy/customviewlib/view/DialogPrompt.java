package com.czy.customviewlib.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import com.czy.customviewlib.databinding.DialogPromptBinding;


public class DialogPrompt {

    private final Dialog dialog;
    private final DialogPromptBinding binding;

    public DialogPrompt(Context context){
        LayoutInflater inflater = LayoutInflater.from(context);
        binding = DialogPromptBinding.inflate(inflater);

        dialog = new Dialog(context);
        dialog.setContentView(binding.getRoot());
        dialog.setCancelable(false);

        // 设置对话框背景透明
        Window window = dialog.getWindow();
        if (window != null){
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        binding.vClose.setOnClickListener(v -> dialog.dismiss());
    }

    public void setButtonClickListener(View.OnClickListener clickListener){
        binding.btnRegister.setOnClickListener(clickListener);
    }

    public void dismiss() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    public void setView(String title1, String title2, String button){
        if (TextUtils.isEmpty(title1)){
            binding.tv2.setVisibility(View.GONE);
        }
        else {
            binding.tv2.setVisibility(View.VISIBLE);
            binding.tv2.setText(title1);
        }
        if (TextUtils.isEmpty(title2)){
            binding.tv3.setVisibility(View.GONE);
        }
        else {
            binding.tv3.setVisibility(View.VISIBLE);
            binding.tv3.setText(title2);
        }
        if (TextUtils.isEmpty(button)){
            binding.btnRegister.setVisibility(View.GONE);
        }
        else {
            binding.btnRegister.setVisibility(View.VISIBLE);
            binding.btnRegister.setText(button);
        }
    }

    public void show(){
        if (dialog != null && !dialog.isShowing()){
            dialog.show();
        }
    }
}
