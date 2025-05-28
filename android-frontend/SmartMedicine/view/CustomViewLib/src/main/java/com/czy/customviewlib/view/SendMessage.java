package com.czy.customviewlib.view;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.BindingAdapter;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.czy.customviewlib.databinding.ViewSendMessageBinding;

/**
 * @author 13225
 */
public class SendMessage extends ConstraintLayout {


    public SendMessage(@NonNull Context context) {
        super(context);
        init(context);
    }

    public SendMessage(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SendMessage(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private ViewSendMessageBinding binding;

    public void init(@NonNull Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        binding = ViewSendMessageBinding.inflate(inflater, this, true);
    }

    public EditText getEditText() {
        return binding.edtv;
    }

    public String getEditMessage(){
        return binding.edtv.getText().toString();
    }

    public void setEditMessage(String message){
        binding.edtv.setText(message != null ? message : "");
    }

    public void setSendClickListener(OnClickListener listener){
        binding.btnSend.setOnClickListener(listener);
    }

    public void setCallClickListener(OnClickListener listener){
        binding.btnCall.setOnClickListener(listener);
    }

    public void setImgClickListener(OnClickListener listener){
        binding.btnPicture.setOnClickListener(listener);
    }
}
