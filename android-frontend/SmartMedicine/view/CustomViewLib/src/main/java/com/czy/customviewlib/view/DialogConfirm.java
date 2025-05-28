package com.czy.customviewlib.view;


import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.czy.customviewlib.databinding.ViewDialogConfirmBinding;

public class DialogConfirm {

    private final Dialog dialog;
    private final ViewDialogConfirmBinding binding;

    public DialogConfirm(Context context){
        LayoutInflater inflater = LayoutInflater.from(context);
        binding = ViewDialogConfirmBinding.inflate(inflater);

        dialog = new Dialog(context);
        dialog.setContentView(binding.getRoot());
        dialog.setCancelable(false);

        setCloseClickListener();
    }

    public void setContent(String content, int colorRes){
        binding.tvContent.setText(content);
        binding.tvContent.setTextColor(colorRes);
    }

    public void setButtonClickListener(View.OnClickListener clickListener){
        binding.btvConfirm.setOnClickListener(clickListener);
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
