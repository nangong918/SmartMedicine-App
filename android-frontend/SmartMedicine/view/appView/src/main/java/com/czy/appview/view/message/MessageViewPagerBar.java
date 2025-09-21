package com.czy.appview.view.message;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.czy.appview.R;
import com.czy.appview.databinding.MessageViewpagerBarBinding;
import com.czy.appview.view.home.HomeViewPagerEnum;
import com.czy.domain.OnPositionItemClick;

public class MessageViewPagerBar extends ConstraintLayout {
    public MessageViewPagerBar(@NonNull Context context) {
        super(context);
        init(context);
    }

    public MessageViewPagerBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MessageViewPagerBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private MessageViewpagerBarBinding binding;

    private void init(@NonNull Context context){
        LayoutInflater inflater = LayoutInflater.from(context);
        binding = MessageViewpagerBarBinding.inflate(inflater, this, true);

        setOnClickListener();
    }

    private void setOnClickListener(){
        LinearLayout[] linearLayouts = new LinearLayout[]{
                binding.lyClick1,
                binding.lyClick2
        };
        for (int i = 0; i < linearLayouts.length; i++){
            int finalI = i;
            linearLayouts[i].setOnClickListener(
                    v -> {
                        currentPosition = MessageViewPagerEnum.values()[finalI].getIndex();
                        if (onViewPagerBarClickListener != null){
                            onViewPagerBarClickListener.onPositionItemClick(
                                    MessageViewPagerEnum.values()[finalI].getIndex()
                            );
                            updateUI();
                        }
                    }
            );
        }
    }

    private int currentPosition = MessageViewPagerEnum.MESSAGE.getIndex();

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
        binding.tvMessage.setTextColor(
                HomeViewPagerEnum.RECOMMEND.equals(homeViewPagerEnum) ?
                        ContextCompat.getColor(getContext(), R.color.green_1000) :
                        ContextCompat.getColor(getContext(), R.color.green_900)
        );
        binding.vBar1.setVisibility(
                HomeViewPagerEnum.RECOMMEND.equals(homeViewPagerEnum) ?
                        VISIBLE :
                        GONE
        );

        binding.tvAddressBook.setTextColor(
                HomeViewPagerEnum.POPULAR.equals(homeViewPagerEnum) ?
                        ContextCompat.getColor(getContext(), R.color.green_1000) :
                        ContextCompat.getColor(getContext(), R.color.green_900)
        );
        binding.vBar2.setVisibility(
                HomeViewPagerEnum.POPULAR.equals(homeViewPagerEnum) ?
                        VISIBLE :
                        GONE
        );
    }

}
