package com.czy.customviewlib.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;

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
        SelectItemEnum sli = Optional.ofNullable(mainTopBarVo)
                .map(vo -> vo.selectItemEnum)
                .orElse(null);

        binding.searchBar.setVisibility(GONE);

        if (sli == null){
            binding.lyFriend.setVisibility(GONE);
        }
        else {
            // view
            if (mainTopBarVo.onFriendCallback != null && SelectItemEnum.FRIENDS.equals(sli)){
                binding.lyFriend.setVisibility(VISIBLE);
            }
            else {
                binding.lyFriend.setVisibility(GONE);
            }
            // data
            switch (sli){
                case HOME -> {
                    binding.searchBar.setVisibility(VISIBLE);
                }
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
    }

    public void setImageClickListener(OnClickListener listener){
        binding.cImgvFace.setOnClickListener(listener);
    }

    public void setSearchBarClickListener(OnClickListener listener){
        binding.searchBar.setOnClickListener(listener);
    }
}
