package com.czy.customviewlib.dataBinding;


import android.text.TextWatcher;
import android.view.View;
import android.widget.ProgressBar;

import androidx.databinding.BindingAdapter;
import androidx.lifecycle.MutableLiveData;

import com.czy.customviewlib.view.SendMessage;

public class BindingAdapters {

    // BindingAdapter for editMessage
    @BindingAdapter("setTextChangedListener")
    public static void setTextChangedListener(SendMessage sendMessage, TextWatcher textWatcher) {
        sendMessage.getEditText().addTextChangedListener(textWatcher);
    }

    @BindingAdapter("isLoading")
    public static void setLoading(ProgressBar progressBar, MutableLiveData<Boolean> isLoading) {
        if (isLoading != null && isLoading.getValue() != null) {
            // 根据 isLoading 的当前值设置 ProgressBar 的可见性
            progressBar.setVisibility(isLoading.getValue() ? View.VISIBLE : View.GONE);
        } else {
            progressBar.setVisibility(View.GONE); // 默认为隐藏
        }
    }
}
