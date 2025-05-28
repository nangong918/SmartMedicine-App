package com.czy.customviewlib.view.chatCard;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.czy.customviewlib.databinding.ViewChatCardItemBinding;

/**
 * @author 13225
 */
public class ChatCardItem extends ConstraintLayout {

    public ChatCardItem(@NonNull Context context) {
        super(context);
        init(context);
    }

    public ChatCardItem(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ChatCardItem(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private ViewChatCardItemBinding binding;

    private void init(Context context){
        LayoutInflater inflater = LayoutInflater.from(context);
        binding = ViewChatCardItemBinding.inflate(inflater, this, true);
    }
}
