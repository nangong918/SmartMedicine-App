package com.czy.customviewlib.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.czy.customviewlib.databinding.ViewMainTopBarBinding;
import com.czy.dal.constant.SelectItemEnum;
import com.czy.dal.vo.view.mainTop.MainTopBarVo;

import java.util.Optional;

/**
 * @author 13225
 */
public class MainTopBar extends ConstraintLayout {


    public MainTopBar(@NonNull Context context) {
        super(context);
        init(context);
    }

    public MainTopBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MainTopBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private ViewMainTopBarBinding binding;

    private void init(Context context){
        LayoutInflater inflater = LayoutInflater.from(context);
        binding = ViewMainTopBarBinding.inflate(inflater, this, true);
    }

    /**
     * 设置头像 上传下载需要压缩；资源来自于网络或者本地缓存
     * @param bitmap    bitmap
     */
    public void setImageResource(Bitmap bitmap){
        if (bitmap == null){
            return;
        }
        binding.cImgvFace.setImageBitmap(bitmap);
    }

    public void setView(MainTopBarVo mainTopBarVo){
        Optional.ofNullable(mainTopBarVo)
                .map(vo -> vo.selectItemEnum)
                .ifPresent(
                        sli -> {
                            // view
                            binding.lyFriend.setVisibility(sli == SelectItemEnum.FRIENDS ? View.VISIBLE : View.GONE);
                            // data
                            switch (sli){
                                case HOME -> {}
                                case SEARCH -> {}
                                case AI -> {}
                                case FRIENDS -> {
                                    if (mainTopBarVo.onFriendCallback == null){
                                        binding.lyFriend.setOnClickListener(v -> {});
                                    }
                                    else {
                                        binding.lyFriend.setOnClickListener(v -> {
                                            mainTopBarVo.onFriendCallback.onSearchFriendClick();
                                        });
                                    }
                                }
                                case NOTIFICATIONS -> {}
                                case MESSAGE -> {}
                            }
                        }
                );
    }

    public void setImageClickListener(OnClickListener listener){
        binding.cImgvFace.setOnClickListener(listener);
    }
}
