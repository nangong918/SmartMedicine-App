package com.czy.customviewlib.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.czy.customviewlib.databinding.ViewInfoBarBinding;


public class InfoBarView extends ConstraintLayout {

    public InfoBarView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public InfoBarView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public InfoBarView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private ViewInfoBarBinding binding;

    private void init(Context context){
        LayoutInflater inflater = LayoutInflater.from(context);
        binding = ViewInfoBarBinding.inflate(inflater, this, true);
    }

    public void setBack(OnClickListener onClickListener){
        binding.imgvBack.setOnClickListener(onClickListener);
    }

    public void setTitle(String title){
        binding.tvTitle.setText(title);
    }
}
