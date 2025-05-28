package com.czy.baseUtilsLib.network.networkLoad;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

import androidx.annotation.StyleRes;
import androidx.multidex.MultiDexApplication;

// TODO 思考为什么要使用 MultiDexApplication
// TODO 全局网络请求的NetworkLoadUtils封装
public class NetworkLoadUtils extends MultiDexApplication {

    private static SimpleLoanDialog sProcessDialog = null;

    protected static final String TAG = NetworkLoadUtils.class.getSimpleName();

    public static void showDialog(Context context) {
        showDialog(context, null);
    }

    public static void showDialog(Context context, String msg) {
        showDialog(context, msg, true);
    }

    public static void showDialog(Context context, String msg, boolean cancelable) {
        showDialog(context,msg,cancelable,null);
    }

    public static void showDialog(Context context, String msg, boolean cancelable, int bgResId) {
        showDialog(context,msg,cancelable, bgResId, 0, null);
    }

    public static void showDialog(Context context, String msg, boolean cancelable, DialogInterface.OnDismissListener listener){
        showDialog(context, msg, cancelable, 0, 0, listener);
    }

    public static void showDialog(Context context, String msg, boolean cancelable, int bgResId, DialogInterface.OnDismissListener listener){
        showDialog(context, msg, cancelable, bgResId, 0, listener);
    }

    public static void showDialog(
            final Context context,
            String msg,
            boolean cancelable,
            int bgResId,
            @StyleRes int styleResId,
            DialogInterface.OnDismissListener listener){
        if (sProcessDialog == null || !sProcessDialog.mDialog.isShowing()) {
            sProcessDialog = new SimpleLoanDialog(context, msg, false, bgResId, styleResId);
            try {
                sProcessDialog.show();
            } catch (Exception e) {
                Log.e(TAG, "显示对话框时发生异常: " + e.getMessage(), e);
            }

            if(listener != null){
                sProcessDialog.getDialog().setOnDismissListener(listener);
            }
            // 取消的操作
            sProcessDialog.getDialog().setOnCancelListener(dialog -> {
                // UtilsApplication.getInstance().disposable();
            });
        }
    }



    public static void dismissDialog() {
        if (sProcessDialog != null) {
            try {
                if(sProcessDialog.mDialog != null){
                    sProcessDialog.mDialog.dismiss();
                }
            }
            catch (Throwable ex) {
                Log.e(TAG, "关闭对话框时发生异常: " + ex.getMessage(), ex);
            }
            sProcessDialog = null;
        }
    }

}
