package com.czy.customviewlib.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.czy.customviewlib.databinding.ViewMessagePromptBinding;

public class MessagePrompt extends ConstraintLayout {

    public MessagePrompt(@NonNull Context context) {
        super(context);
        init(context);
    }

    public MessagePrompt(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public MessagePrompt(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private ViewMessagePromptBinding binding;

    private void init(@NonNull Context context){
        LayoutInflater inflater = LayoutInflater.from(context);
        binding = ViewMessagePromptBinding.inflate(inflater, this, true);
        // init view
        setMessageNum(currentNum);
    }

    private int currentNum = 0;

    public void setMessageNum(int num){
        currentNum = num;
        if (num <= 0){
//            binding.lyMain.setVisibility(GONE);
            binding.imgvBackground.setVisibility(GONE);
            binding.tvMessageNum.setVisibility(GONE);
        }
        else if (num < 100){
//            binding.lyMain.setVisibility(VISIBLE);
            binding.imgvBackground.setVisibility(VISIBLE);
            binding.tvMessageNum.setVisibility(VISIBLE);
            binding.tvMessageNum.setText(String.valueOf(num));
        }
        else {
//            binding.lyMain.setVisibility(VISIBLE);
            binding.imgvBackground.setVisibility(VISIBLE);
            binding.tvMessageNum.setVisibility(VISIBLE);
            String numStr = "99+";
            binding.tvMessageNum.setText(numStr);
        }
    }

    public int getCurrentNum(){
        return currentNum;
    }
}
