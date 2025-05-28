package com.czy.baseUtilsLib.image;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.ViewGroup;
import android.widget.ImageView;

public class ImageViewUtil {

    public static void setImageWidthMatchParent(ImageView imageView, Context context, int drawableId){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; // 仅获取图片的边界
        BitmapFactory.decodeResource(context.getResources(), drawableId, options);

        int imageWidth = options.outWidth;
        int imageHeight = options.outHeight;

        // 计算比例
        float aspectRatio = (float) imageHeight / imageWidth;

        // 获取设备宽度
        int deviceWidth = context.getResources().getDisplayMetrics().widthPixels;

        // 计算高度
        int calculatedHeight = (int) (deviceWidth * aspectRatio);

        // 设置 ImageView 的布局参数
        ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.height = calculatedHeight;
        imageView.setLayoutParams(layoutParams);

        // 设置图片资源
        imageView.setImageResource(drawableId);
    }

}
