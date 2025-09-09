package com.czy.appview.view.medicine.order;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.czy.appview.R;
import com.czy.appview.databinding.OrderViewpagerBarBinding;
import com.czy.appview.view.home.HomeViewPagerEnum;
import com.czy.domain.OnPositionItemClick;

public class OrderViewPagerBar extends ConstraintLayout {
    public OrderViewPagerBar(@NonNull Context context) {
        super(context);
        init(context);
    }

    public OrderViewPagerBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public OrderViewPagerBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private OrderViewpagerBarBinding binding;

    private void init(@NonNull Context context){
        LayoutInflater inflater = LayoutInflater.from(context);
        binding = OrderViewpagerBarBinding.inflate(inflater, this, true);

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
        OrderViewPagerEnum homeViewPagerEnum = OrderViewPagerEnum.getByValue(currentPosition);
        binding.tvAppointment.setTextColor(
                OrderViewPagerEnum.APPOINTMENT_ORDER.equals(homeViewPagerEnum) ?
                        ContextCompat.getColor(getContext(), R.color.white) :
                        ContextCompat.getColor(getContext(), R.color.green_900)
        );
        binding.lyClick1.setBackgroundResource(
                OrderViewPagerEnum.APPOINTMENT_ORDER.equals(homeViewPagerEnum) ?
                        R.color.green_100 :
                        R.color.white
        );

        binding.tvPurchase.setTextColor(
                OrderViewPagerEnum.PURCHASE_ORDER.equals(homeViewPagerEnum) ?
                        ContextCompat.getColor(getContext(), R.color.white) :
                        ContextCompat.getColor(getContext(), R.color.green_900)
        );
        binding.lyClick2.setBackgroundResource(
                OrderViewPagerEnum.PURCHASE_ORDER.equals(homeViewPagerEnum) ?
                        R.color.green_100 :
                        R.color.white
        );
    }

}
