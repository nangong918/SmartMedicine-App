package com.czy.customviewlib.view.viewPager;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.czy.customviewlib.R;
import com.czy.customviewlib.databinding.ViewViewpagerBarBinding;
import com.czy.dal.OnPositionItemClick;


public class ViewPagerBar extends ConstraintLayout {


    public ViewPagerBar(@NonNull Context context) {
        super(context);
        init(context);
    }

    public ViewPagerBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ViewPagerBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private ViewViewpagerBarBinding binding;

    private void init(@NonNull Context context){
        LayoutInflater inflater = LayoutInflater.from(context);
        binding = ViewViewpagerBarBinding.inflate(inflater, this, true);

        setOnClickListener();

        // 使用 ContextCompat 获取颜色
        red_1000 = ContextCompat.getColor(context, R.color.red_1000);
        red_800 = ContextCompat.getColor(context, R.color.red_800);
        red_10_id = R.color.red_10;
        white_id = R.color.white;

//        updateUI();
    }

    private void setOnClickListener(){
        binding.lyClick1.setOnClickListener(v -> {
            currentPosition = ViewPagerConstant.USER.getIndex();
            if (onViewPagerBarClickListener != null){
                onViewPagerBarClickListener.onPositionItemClick(ViewPagerConstant.USER.getIndex());
                updateUI();
            }
        });
        binding.lyClick2.setOnClickListener(v -> {
            currentPosition = ViewPagerConstant.GROUP.getIndex();
            if (onViewPagerBarClickListener != null){
                onViewPagerBarClickListener.onPositionItemClick(ViewPagerConstant.GROUP.getIndex());
                updateUI();
            }
        });
    }

    private int currentPosition = ViewPagerConstant.USER.getIndex();

    private int red_1000 = 0xFF3544;
    private int red_800 = 0xFF5A66;
    private int red_10_id = R.color.red_10;
    private int white_id = R.color.white;

    private void updateUI(){
        binding.fragmentBarTv1.setTextColor(
                currentPosition == ViewPagerConstant.USER.getIndex() ? red_1000 : red_800
        );
        binding.fragmentBarTv2.setTextColor(
                currentPosition == ViewPagerConstant.GROUP.getIndex() ? red_1000 : red_800
        );

        binding.fragmentBarV1.setVisibility(
                currentPosition == ViewPagerConstant.USER.getIndex() ? View.VISIBLE : View.GONE
        );
        binding.fragmentBarV2.setVisibility(
                currentPosition == ViewPagerConstant.GROUP.getIndex() ? View.VISIBLE : View.GONE
        );

        binding.lyClick1.setBackgroundResource(
                currentPosition == ViewPagerConstant.USER.getIndex() ? R.color.red_10 : R.color.white
        );
        binding.lyClick2.setBackgroundResource(
                currentPosition == ViewPagerConstant.GROUP.getIndex() ? R.color.red_10 : R.color.white
        );
    }

    private OnPositionItemClick onViewPagerBarClickListener;

    public void setOnViewPagerBarClickListener(@NonNull OnPositionItemClick onViewPagerBarClickListener) {
        this.onViewPagerBarClickListener = onViewPagerBarClickListener;
    }

    public void setText(@NonNull String[] text){
        assert text.length >= 2;
        binding.fragmentBarTv1.setText(text[0]);
        binding.fragmentBarTv2.setText(text[1]);
    }

    public void setCurrentPosition(int currentSelected){
        this.currentPosition = currentSelected;
        updateUI();
    }
}
