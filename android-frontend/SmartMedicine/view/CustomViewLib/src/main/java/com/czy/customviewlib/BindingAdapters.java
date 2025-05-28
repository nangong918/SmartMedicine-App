package com.czy.customviewlib;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.databinding.BindingAdapter;
import androidx.lifecycle.MutableLiveData;

import java.util.Optional;

/**
 * @author 13225
 */
public class BindingAdapters {

    @BindingAdapter("srcInt")
    public static void setImageResource(ImageView imageView, MutableLiveData<Integer> resource) {
        Integer resourceValue = Optional.ofNullable(resource)
                .map(MutableLiveData::getValue)
                        .orElse(null);
        Log.i("BindingAdapters", "setImageResource: " + resourceValue);
        // 设置图像资源
        imageView.setImageResource(Optional.ofNullable(resourceValue)
                .orElse(R.mipmap.logo));
    }

    @BindingAdapter("backgroundInt")
    public static void setBackgroundResource(View view, MutableLiveData<Integer> resource) {
        Integer resourceValue = Optional.ofNullable(resource)
                .map(MutableLiveData::getValue)
                .orElse(null);

        // 设置背景资源
        view.setBackgroundResource(Optional.ofNullable(resourceValue)
                .orElse(R.mipmap.logo));
    }
}
