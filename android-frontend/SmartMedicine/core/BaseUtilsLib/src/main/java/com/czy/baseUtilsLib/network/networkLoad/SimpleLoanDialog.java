package com.czy.baseUtilsLib.network.networkLoad;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.StyleRes;

import com.czy.baseUtilsLib.R;

public class SimpleLoanDialog {
    public Dialog mDialog;
    private final AnimationDrawable animationDrawable = null;

    public SimpleLoanDialog(Context context, String message) {
        this(context, message, true, 0, 0);
    }

    public SimpleLoanDialog(Context context, String message, boolean cancelable) {
        this(context, message, cancelable, 0, 0);
    }

    public SimpleLoanDialog(Context context, String message, boolean cancelable, int bgResId, @StyleRes int styleId) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.progress_simple_view, null);

        View container = view.findViewById(R.id.ll_progress_custom_container);
        if (bgResId != 0) {
            container.setBackgroundResource(bgResId);
        }

        TextView text = view.findViewById(R.id.tv_progress_message);
        if (TextUtils.isEmpty(message)) {
            text.setVisibility(View.GONE);
        } else {
            text.setVisibility(View.VISIBLE);
        }
        text.setText(message);
        ImageView loadingImage = view.findViewById(R.id.progress_view);
        int style = styleId == 0 ? R.style.My_AppCompat_Dialog_Theme : styleId;
        mDialog = new Dialog(context, style);
        mDialog.setCancelable(cancelable);
        mDialog.setContentView(view);
        mDialog.setCanceledOnTouchOutside(cancelable);


    }

    public void show() {
        mDialog.show();
    }

    public void setCanceledOnTouchOutside(boolean cancel) {
        mDialog.setCanceledOnTouchOutside(cancel);
    }

    public void dismiss() {
        if (mDialog.isShowing()) {
            mDialog.dismiss();
//            if (animationDrawable != null){
//                animationDrawable.stop();
//            }
        }
    }

    public boolean isShowing() {
        return mDialog.isShowing();
    }


    public Dialog getDialog() {
        return mDialog;
    }
}
