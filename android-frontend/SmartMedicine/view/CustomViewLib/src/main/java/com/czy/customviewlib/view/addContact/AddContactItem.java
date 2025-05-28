package com.czy.customviewlib.view.addContact;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.czy.customviewlib.databinding.ViewAddContactCardItemBinding;


/**
 * @author 13225
 */
public class AddContactItem extends ConstraintLayout {


    public AddContactItem(@NonNull Context context) {
        super(context);
        init(context);
    }

    public AddContactItem(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AddContactItem(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private ViewAddContactCardItemBinding binding;

    private void init(Context context){
        LayoutInflater inflater = LayoutInflater.from(context);
        binding = ViewAddContactCardItemBinding.inflate(inflater, this, true);
    }

}
