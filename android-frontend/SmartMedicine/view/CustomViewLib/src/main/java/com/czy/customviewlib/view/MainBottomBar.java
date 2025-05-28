package com.czy.customviewlib.view;

import android.content.Context;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.czy.customviewlib.R;
import com.czy.customviewlib.databinding.ViewMainBottomBarBinding;
import com.czy.dal.OnPositionItemClick;
import com.czy.dal.constant.SelectItemEnum;

/**
 * @author 13225
 */
public class MainBottomBar extends ConstraintLayout {


    public MainBottomBar(@NonNull Context context) {
        super(context);
        init(context);
    }

    public MainBottomBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MainBottomBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private ViewMainBottomBarBinding binding;

    private void init(Context context){
        LayoutInflater inflater = LayoutInflater.from(context);
        binding = ViewMainBottomBarBinding.inflate(inflater, this, true);
        clickLys = new LinearLayout[]{binding.lyClick1, binding.lyClick2, binding.lyClick3, binding.lyClick4, binding.lyClick5, binding.lyClick6};
        imageViews = new ImageView[]{binding.imgvHome, binding.imgvSearch, binding.imgvAi, binding.imgvFriends, binding.imgvNotifications, binding.imgvMessage};
        updateUi(0);
    }

    private LinearLayout[] clickLys;
    private ImageView[] imageViews;

    public void clickListener(OnPositionItemClick click) {
        for (int i = 0; i < clickLys.length; i++){
            int finalI = i;
            clickLys[i].setOnClickListener(v -> {
                updateUi(finalI);
                click.onPositionItemClick(finalI);
            });
        }
    }

    private void updateUi(int clickPosition) {
        boolean[] isClick = new boolean[imageViews.length];
        isClick[clickPosition] = true;
        for (int i = 0; i < imageViews.length; i++) {
            int color = isClick[i] ? R.color.red_800 : R.color.dark_red_900;
            imageViews[i].setColorFilter(ContextCompat.getColor(getContext(), color), PorterDuff.Mode.SRC_IN);
        }
    }

    private SelectItemEnum currentSelected;

    public void setSelected(SelectItemEnum position) {
        this.currentSelected = position;
        updateUi(position.getPosition());
    }
}
