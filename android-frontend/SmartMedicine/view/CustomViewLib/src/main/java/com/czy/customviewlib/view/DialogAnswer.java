package com.czy.customviewlib.view;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.czy.customviewlib.databinding.DialogAnwserBinding;

public class DialogAnswer {

    private final Dialog dialog;
    private final DialogAnwserBinding binding;

    public DialogAnswer(Context context){
        LayoutInflater inflater = LayoutInflater.from(context);
        binding = DialogAnwserBinding.inflate(inflater);

        dialog = new Dialog(context);
        dialog.setContentView(binding.getRoot());
        dialog.setCancelable(false);

        setCloseClickListener();
    }

    public void setContent(String question, String answer, View.OnClickListener onViewDetailsClickListener) {
        binding.tvQuestion.setText(question);
        binding.tvAnswer.setText(answer);
        binding.btnViewConfirm.setOnClickListener(onViewDetailsClickListener);
    }

    private void setCloseClickListener(){
        binding.vClose.setOnClickListener(v -> dismiss());
    }

    public void dismiss() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    public void show(){
        if (dialog != null && !dialog.isShowing()){
            dialog.show();
        }
    }

}
