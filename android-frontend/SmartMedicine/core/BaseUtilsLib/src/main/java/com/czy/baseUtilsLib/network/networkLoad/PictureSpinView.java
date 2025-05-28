package com.czy.baseUtilsLib.network.networkLoad;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import com.czy.baseUtilsLib.R;

public class PictureSpinView extends androidx.appcompat.widget.AppCompatImageView implements PictureIndeterminate{

    private float mRotateDegrees;
    private int mFrameTime;
    private boolean mNeedToUpdateView;
    private Runnable mUpdateViewRunnable;


    public PictureSpinView(Context context) {
        super(context);
        init();
    }

    public PictureSpinView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    private void init() {
        setImageResource(R.drawable.icon_dialog_loading);
        mFrameTime = 1000 / 12;
        mUpdateViewRunnable = new Runnable() {
            @Override
            public void run() {
                mRotateDegrees += 30;
                mRotateDegrees = mRotateDegrees < 360 ? mRotateDegrees : mRotateDegrees - 360;
                invalidate();
                if (mNeedToUpdateView) {
                    postDelayed(this, mFrameTime);
                }
            }
        };
    }




    // 接口 : 旋转速度

    @Override
    public void setAnimationSpeed(float scale) {
        mFrameTime = (int) (1000 / 12 / scale);
    }

    // View

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.rotate(mRotateDegrees, (float) getWidth() / 2, (float) getHeight() / 2);
        super.onDraw(canvas);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mNeedToUpdateView = true;
        post(mUpdateViewRunnable);
    }

    @Override
    protected void onDetachedFromWindow() {
        mNeedToUpdateView = false;
        super.onDetachedFromWindow();
    }
}
