package com.czy.appview.view.medicine;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.czy.appview.R;
import com.czy.appview.databinding.AppointmentBarBinding;
import com.czy.domain.OnPositionItemClick;
import com.czy.domain.vo.entity.medicine.AppointmentDoctorDataVo;

import java.util.List;
import java.util.Optional;

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

    public List<AppointmentDoctorDataVo> dataVos;

    private int currentPosition = 0;

    private OnPositionItemClick onViewPagerBarClickListener;

    public void setOnViewPagerBarClickListener(@NonNull OnPositionItemClick onViewPagerBarClickListener) {
        this.onViewPagerBarClickListener = onViewPagerBarClickListener;
    }

    public void setCurrentPosition(int currentSelected){
        this.currentPosition = currentSelected;
        updateUI();
    }

    public void updateUiDate(){
        TextView[] dateText = new TextView[]{
                binding.tvDate1,
                binding.tvDate2,
                binding.tvDate3,
        };
        TextView[] priceText = new TextView[]{
                binding.tvPrice1,
                binding.tvPrice2,
                binding.tvPrice3,
        };
        TextView[] remainText = new TextView[]{
                binding.tvRemain1,
                binding.tvRemain2,
                binding.tvRemain3,
        };
        Optional.ofNullable(dataVos)
                .filter(vos -> !vos.isEmpty())
                .ifPresent(vos -> {
                    for (int i = 0; i < vos.size(); i++){
                        dateText[i].setText(vos.get(i).date);
                        priceText[i].setText(vos.get(i).minCost);
                        remainText[i].setText(vos.get(i).remainCount);
                    }
                });
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
        updateUiDate();
    }

}
