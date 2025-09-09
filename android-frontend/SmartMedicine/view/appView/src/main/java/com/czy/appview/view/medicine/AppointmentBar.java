package com.czy.appview.view.medicine;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.czy.appview.R;
import com.czy.appview.databinding.AppointmentBarBinding;
import com.czy.appview.view.home.HomeViewPagerEnum;
import com.czy.domain.OnPositionItemClick;

public class AppointmentBar extends ConstraintLayout {
    public AppointmentBar(@NonNull Context context) {
        super(context);
        init(context);
    }

    public AppointmentBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AppointmentBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private AppointmentBarBinding binding;

    private void init(@NonNull Context context){
        LayoutInflater inflater = LayoutInflater.from(context);
        binding = AppointmentBarBinding.inflate(inflater, this, true);

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
                        currentPosition = finalI;
                        if (onViewPagerBarClickListener != null){
                            onViewPagerBarClickListener.onPositionItemClick(
                                    finalI
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
        LinearLayout[] linearLayouts = new LinearLayout[]{
                binding.lyClick1,
                binding.lyClick2,
                binding.lyClick3,
                binding.lyClick4
        };
        for (int i = 0; i < linearLayouts.length; i++){
            linearLayouts[i].setBackgroundResource(
                    i == currentPosition ?
                            R.drawable.round_corners_bg_commend :
                            android.R.color.transparent
            );
        }
    }

}
