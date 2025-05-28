package com.czy.customviewlib.view.contact;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.czy.customviewlib.databinding.ViewContactItemBinding;

/**
 * @author 13225
 */
public class ContactItem extends ConstraintLayout {


    public ContactItem(@NonNull Context context) {
        super(context);
        init(context);
    }

    public ContactItem(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ContactItem(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private ViewContactItemBinding binding;

    private void init(Context context){
        LayoutInflater inflater = LayoutInflater.from(context);
        binding = ViewContactItemBinding.inflate(inflater, this, true);
    }

}
