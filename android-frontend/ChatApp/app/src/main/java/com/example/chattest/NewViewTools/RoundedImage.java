package com.example.chattest.NewViewTools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

import com.example.chattest.R;


public class RoundedImage extends AppCompatImageView {

    private float cornerRadius;

    public RoundedImage(Context context) {
        super(context);
        init();
    }

    public RoundedImage(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RoundedImage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        // 设置 ImageView 的 ScaleType 为 CENTER_CROP，以保证图片能够填充整个 View
        setScaleType(ScaleType.CENTER_CROP);
        // 设置矩形的圆角半径
        cornerRadius = getResources().getDimension(R.dimen.rounded_image_corner_radius);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // 获取 View 的宽度和高度
        int width = getWidth();
        int height = getHeight();

        // 创建一个路径对象，用于绘制带有圆角的矩形
        @SuppressLint("DrawAllocation") Path path = new Path();
        @SuppressLint("DrawAllocation") RectF rect = new RectF(0, 0, width, height);
        @SuppressLint("DrawAllocation") float[] radii = new float[]{
                cornerRadius, cornerRadius, // 左上角
                cornerRadius, cornerRadius, // 右上角
                cornerRadius, cornerRadius, // 右下角
                cornerRadius, cornerRadius  // 左下角
        };
        path.addRoundRect(rect, radii, Path.Direction.CW);

        // 使用路径对象裁剪画布，并绘制图片
        canvas.clipPath(path);
        super.onDraw(canvas);
    }
}
