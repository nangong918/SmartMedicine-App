package com.czy.appview.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.czy.appview.databinding.ViewMainTopBarBinding;
import com.czy.domain.vo.view.mainTop.MainTopBarVo;

/**
 * @author 13225
 */
public class HomeTopBar extends ConstraintLayout {


    public HomeTopBar(@NonNull Context context) {
        super(context);
        init(context);
    }

    public HomeTopBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public HomeTopBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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

    public ImageView getImageView(){
        return binding.cImgvFace;
    }

    public void setView(MainTopBarVo mainTopBarVo){

    }

    public void setImageClickListener(OnClickListener listener){
        binding.cImgvFace.setOnClickListener(listener);
    }

    public void setSearchBarClickListener(OnClickListener listener){
        binding.searchBar.setOnClickListener(listener);
    }
}
