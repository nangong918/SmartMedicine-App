package com.czy.customviewlib.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;


import com.czy.customviewlib.databinding.ViewTopActivityBarBinding;

/**
 * @author 13225
 */
public class TopActivityBar extends ConstraintLayout {
    public TopActivityBar(@NonNull Context context) {
        super(context);
        init(context,null);
    }

    public TopActivityBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public TopActivityBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private ViewTopActivityBarBinding binding;

    private void init(Context context, @Nullable AttributeSet attrs){
        LayoutInflater inflater = LayoutInflater.from(context);
        binding = ViewTopActivityBarBinding.inflate(inflater, this, true);
    }

    public void setBackImageButton(OnClickListener listener){
        if(listener != null){
            binding.basicBack.setOnClickListener(listener);
        }
    }

    public void setTitleText(String title){
        if (title != null){
            binding.basicTitle.setText(title);
        }
    }

}
