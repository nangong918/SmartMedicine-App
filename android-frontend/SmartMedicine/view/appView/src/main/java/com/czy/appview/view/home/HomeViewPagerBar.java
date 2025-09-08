package com.czy.appview.view.home;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.czy.appview.R;
import com.czy.appview.databinding.HomeViewpagerBarBinding;
import com.czy.domain.OnPositionItemClick;

public class HomeViewPagerBar extends ConstraintLayout {
    public HomeViewPagerBar(@NonNull Context context) {
        super(context);
        init(context);
    }

    public HomeViewPagerBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public HomeViewPagerBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private HomeViewpagerBarBinding binding;

    private void init(@NonNull Context context){
        LayoutInflater inflater = LayoutInflater.from(context);
        binding = HomeViewpagerBarBinding.inflate(inflater, this, true);

        setOnClickListener();
    }

    private void setOnClickListener(){
        LinearLayout[] linearLayouts = new LinearLayout[]{
                binding.lyClick1,
                binding.lyClick2,
                binding.lyClick3,
                binding.lyClick4
        };
        for (int i = 0; i < linearLayouts.length; i++){
            int finalI = i;
            linearLayouts[i].setOnClickListener(
                    v -> {
                        currentPosition = HomeViewPagerEnum.values()[finalI].getIndex();
                        if (onViewPagerBarClickListener != null){
                            onViewPagerBarClickListener.onPositionItemClick(
                                    HomeViewPagerEnum.values()[finalI].getIndex()
                            );
                            updateUI();
                        }
                    }
            );
        }
    }

    private int currentPosition = HomeViewPagerEnum.RECOMMEND.getIndex();

    private OnPositionItemClick onViewPagerBarClickListener;

    public void setOnViewPagerBarClickListener(@NonNull OnPositionItemClick onViewPagerBarClickListener) {
        this.onViewPagerBarClickListener = onViewPagerBarClickListener;
    }

    public void setCurrentPosition(int currentSelected){
        this.currentPosition = currentSelected;
        updateUI();
    }

    private void updateUI() {
        HomeViewPagerEnum homeViewPagerEnum = HomeViewPagerEnum.getEnumByIndex(currentPosition);
        binding.tvRecommend.setTextColor(
                HomeViewPagerEnum.RECOMMEND.equals(homeViewPagerEnum) ?
                        ContextCompat.getColor(getContext(), R.color.green_1000) :
                        ContextCompat.getColor(getContext(), R.color.green_900)
        );
        binding.vBar1.setVisibility(
                HomeViewPagerEnum.RECOMMEND.equals(homeViewPagerEnum) ?
                        VISIBLE :
                        GONE
        );

        binding.tvPopular.setTextColor(
                HomeViewPagerEnum.POPULAR.equals(homeViewPagerEnum) ?
                        ContextCompat.getColor(getContext(), R.color.green_1000) :
                        ContextCompat.getColor(getContext(), R.color.green_900)
        );
        binding.vBar2.setVisibility(
                HomeViewPagerEnum.POPULAR.equals(homeViewPagerEnum) ?
                        VISIBLE :
                        GONE
        );

        binding.tvFollow.setTextColor(
                HomeViewPagerEnum.FOLLOW.equals(homeViewPagerEnum) ?
                        ContextCompat.getColor(getContext(), R.color.green_1000) :
                        ContextCompat.getColor(getContext(), R.color.green_900)
        );
        binding.vBar3.setVisibility(
                HomeViewPagerEnum.FOLLOW.equals(homeViewPagerEnum) ?
                        VISIBLE :
                        GONE
        );

        binding.tvFriendsCircle.setTextColor(
                HomeViewPagerEnum.FRIEND_CIRCLE.equals(homeViewPagerEnum) ?
                        ContextCompat.getColor(getContext(), R.color.green_1000) :
                        ContextCompat.getColor(getContext(), R.color.green_900)
        );
        binding.vBar4.setVisibility(
                HomeViewPagerEnum.FRIEND_CIRCLE.equals(homeViewPagerEnum) ?
                        VISIBLE :
                        GONE
        );
    }

}
