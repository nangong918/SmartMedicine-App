package com.czy.customviewlib.view.chatMessage;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.czy.customviewlib.databinding.ViewSendMessageItemBinding;

/**
 * @author 13225
 */
public class SendMessageItem extends ConstraintLayout {

    public SendMessageItem(@NonNull Context context) {
        super(context);
        init(context);
    }

    public SendMessageItem(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SendMessageItem(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private ViewSendMessageItemBinding binding;

    private void init(@NonNull Context context){
        LayoutInflater inflater = LayoutInflater.from(context);
        binding = ViewSendMessageItemBinding.inflate(inflater, this, true);
    }
}
