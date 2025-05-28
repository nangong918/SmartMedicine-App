package com.czy.baseUtilsLib.view;

import android.view.View;
import android.view.ViewGroup;

public class AdaptiveViewUtil {

    /**
     * 根据屏幕宽度自适应设置 View 的大小和位置
     *
     * @param view         需要适配的 View
     * @param screenWidthDp  屏幕宽度(dp)
     * @param screenHeightDp 屏幕高度(dp)
     * @param widthRatio    View 宽度占屏幕宽度的比例(0~1)
     */
    public static void setInScreenByWidth(View view, float screenWidthDp, float screenHeightDp, float widthRatio) {
        float viewWidth = screenWidthDp * widthRatio;
        float viewHeight = viewWidth / view.getWidth() * view.getHeight();

        // 计算 View 的大小和位置
        int newWidth = (int) viewWidth;
        int newHeight = (int) viewHeight;
        int newX = 0;
        int newY = (int) ((screenHeightDp - newHeight) / 2.0f);

        // 设置 View 的大小和位置
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.width = newWidth;
        params.height = newHeight;
        view.setLayoutParams(params);
        view.setX(newX);
        view.setY(newY);
    }

    /**
     * 根据屏幕高度自适应设置 View 的大小和位置
     *
     * @param view         需要适配的 View
     * @param screenWidthDp  屏幕宽度(dp)
     * @param screenHeightDp 屏幕高度(dp)
     * @param heightRatio   View 高度占屏幕高度的比例(0~1)
     */
    public static void setInScreenByHeight(View view, float screenWidthDp, float screenHeightDp, float heightRatio) {
        float viewHeight = screenHeightDp * heightRatio;
        float viewWidth = viewHeight / view.getHeight() * view.getWidth();

        // 计算 View 的大小和位置
        int newWidth = (int) viewWidth;
        int newHeight = (int) viewHeight;
        int newX = (int) ((screenWidthDp - newWidth) / 2.0f);
        int newY = 0;

        // 设置 View 的大小和位置
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.width = newWidth;
        params.height = newHeight;
        view.setLayoutParams(params);
        view.setX(newX);
        view.setY(newY);
    }
}
