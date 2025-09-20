package com.czy.appview.view.medicine;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.czy.appview.R;
import com.czy.appview.databinding.MedicineViewpagerBarBinding;
import com.czy.domain.OnPositionItemClick;

public class MedicineViewPagerBar extends ConstraintLayout {
    public MedicineViewPagerBar(@NonNull Context context) {
        super(context);
        init(context);
    }

    public MedicineViewPagerBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MedicineViewPagerBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private MedicineViewpagerBarBinding binding;

    private void init(@NonNull Context context){
        LayoutInflater inflater = LayoutInflater.from(context);
        binding = MedicineViewpagerBarBinding.inflate(inflater, this, true);

        setOnClickListener();
    }

    private void setOnClickListener(){
        LinearLayout[] linearLayouts = new LinearLayout[]{
                binding.lyClick1,
                binding.lyClick2,
                binding.lyClick3,
                binding.lyClick4,
                binding.lyClick5
        };
        for (int i = 0; i < linearLayouts.length; i++){
            int finalI = i;
            linearLayouts[i].setOnClickListener(
                    v -> {
                        currentPosition = MedicineViewPagerEnum.values()[finalI].getIndex();
                        if (onViewPagerBarClickListener != null){
                            onViewPagerBarClickListener.onPositionItemClick(
                                    MedicineViewPagerEnum.values()[finalI].getIndex()
                            );
                            updateUI();
                        }
                    }
            );
        }
    }

    private int currentPosition = MedicineViewPagerEnum.APPOINTMENT.getIndex();

    private OnPositionItemClick onViewPagerBarClickListener;

    public void setOnViewPagerBarClickListener(@NonNull OnPositionItemClick onViewPagerBarClickListener) {
        this.onViewPagerBarClickListener = onViewPagerBarClickListener;
    }

    public void setCurrentPosition(int currentSelected){
        this.currentPosition = currentSelected;
        updateUI();
    }

    private void updateUI() {
        MedicineViewPagerEnum medicineViewPagerEnum = MedicineViewPagerEnum.getEnumByIndex(currentPosition);
        binding.tvAppointment.setTextColor(
                MedicineViewPagerEnum.APPOINTMENT.equals(medicineViewPagerEnum) ?
                        ContextCompat.getColor(getContext(), R.color.green_1000) :
                        ContextCompat.getColor(getContext(), R.color.green_900)
        );
        binding.vBar1.setVisibility(
                MedicineViewPagerEnum.APPOINTMENT.equals(medicineViewPagerEnum) ?
                        VISIBLE :
                        GONE
        );

        binding.tvAiQuestion.setTextColor(
                MedicineViewPagerEnum.AI_QUESTION.equals(medicineViewPagerEnum) ?
                        ContextCompat.getColor(getContext(), R.color.green_1000) :
                        ContextCompat.getColor(getContext(), R.color.green_900)
        );
        binding.vBar2.setVisibility(
                MedicineViewPagerEnum.AI_QUESTION.equals(medicineViewPagerEnum) ?
                        VISIBLE :
                        GONE
        );

        binding.tvMedicalWiki.setTextColor(
                MedicineViewPagerEnum.MEDICAL_WIKI.equals(medicineViewPagerEnum) ?
                        ContextCompat.getColor(getContext(), R.color.green_1000) :
                        ContextCompat.getColor(getContext(), R.color.green_900)
        );
        binding.vBar3.setVisibility(
                MedicineViewPagerEnum.MEDICAL_WIKI.equals(medicineViewPagerEnum) ?
                        VISIBLE :
                        GONE
        );

        binding.tvMedicalShopping.setTextColor(
                MedicineViewPagerEnum.MEDICAL_SHOPPING.equals(medicineViewPagerEnum) ?
                        ContextCompat.getColor(getContext(), R.color.green_1000) :
                        ContextCompat.getColor(getContext(), R.color.green_900)
        );
        binding.vBar4.setVisibility(
                MedicineViewPagerEnum.MEDICAL_SHOPPING.equals(medicineViewPagerEnum) ?
                        VISIBLE :
                        GONE
        );

        binding.tvHealthReminder.setTextColor(
                MedicineViewPagerEnum.HEALTH_REMINDER.equals(medicineViewPagerEnum) ?
                        ContextCompat.getColor(getContext(), R.color.green_1000) :
                        ContextCompat.getColor(getContext(), R.color.green_900)
        );
        binding.vBar5.setVisibility(
                MedicineViewPagerEnum.HEALTH_REMINDER.equals(medicineViewPagerEnum) ?
                        VISIBLE :
                        GONE
        );
    }

}
