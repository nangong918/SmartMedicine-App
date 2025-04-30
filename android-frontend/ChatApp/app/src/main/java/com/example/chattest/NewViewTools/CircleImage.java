package com.example.chattest.NewViewTools;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.util.AttributeSet;

public class CircleImage extends androidx.appcompat.widget.AppCompatImageView {

    public float originalX;
    public float originalY;
    public float originalWidth;
    public float originalHeight;
    public CircleImage(Context context) {
        super(context);
        init();
    }

    public CircleImage(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircleImage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        // 设置 ImageView 的 ScaleType 为 CENTER_CROP，以保证图片能够填充整个 View
        setScaleType(ScaleType.CENTER_CROP);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 将 View 的宽度和高度设置成相等的值，以保证 View 是正方形
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // 获取 View 的宽度和高度
        int width = getWidth();
        int height = getHeight();

        // 计算圆形的半径
        int radius = Math.min(width, height) / 2;

        // 创建一个圆形的 Path 对象
        Path path = new Path();
        path.addCircle(width / 2f, height / 2f, radius, Path.Direction.CW);

        // 使用 Path 对象裁剪画布，并绘制图片
        canvas.clipPath(path);
        super.onDraw(canvas);
    }
}
