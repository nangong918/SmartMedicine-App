package com.czy.baseUtilsLib.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.czy.baseUtilsLib.R;

public class ToastUtils {

    public static final String TAG = ToastUtils.class.getName();

    /**
     * 显示Toast消息
     * @param context   上下文
     * @param message   消息内容
     */
    public static void showToastActivity(Context context, String message) {
        if (context == null){
            return;
        }
        if (context instanceof Activity activity){
            activity.runOnUiThread(() -> Toast.makeText(activity, message, Toast.LENGTH_SHORT).show());
        }
    }

    /**
     * 显示Toast消息
     * @param context   上下文
     * @param message   消息内容
     */
    public static void showToast(Context context, String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void showLongToast(Context context, String message, int iconResId) {
        // Inflate the custom layout
        LayoutInflater inflater = LayoutInflater.from(context);
        @SuppressLint("InflateParams") View layout = inflater.inflate(R.layout.custom_toast, null);

        // Set the message
        TextView text = layout.findViewById(R.id.toast_text);
        text.setText(message);

        ImageView icon = layout.findViewById(R.id.imageView);

        if (context instanceof FragmentActivity){
            ((FragmentActivity)context).runOnUiThread(() -> {
                try {
                    icon.setImageResource(iconResId);
                } catch (Exception e){
                    Log.w(TAG, "showToast Error: iconResId is no find", e);
                }
                // Create the Toast
                Toast toast = new Toast(context);
                toast.setDuration(Toast.LENGTH_LONG); // 或 Toast.LENGTH_SHORT
                toast.setView(layout);
                toast.show();
            });
        }
        else {
            // Create the Toast
            try {
                icon.setImageResource(iconResId);
            } catch (Exception e){
                Log.w(TAG, "showToast Error: iconResId is no find", e);
            }
            Toast toast = new Toast(context);
            toast.setDuration(Toast.LENGTH_LONG); // 或 Toast.LENGTH_SHORT
            toast.setView(layout);
            toast.show();
        }
    }

}
