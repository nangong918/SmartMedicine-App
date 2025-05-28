package com.czy.smartmedicine.viewModel;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.czy.baseUtilsLib.network.BaseResponse;
import com.czy.baseUtilsLib.network.ResponseUtil;
import com.czy.smartmedicine.MainApplication;

public class ViewModelUtil {

    private static final String TAG = ViewModelUtil.class.getName();

    //==========Dialog

    public static void globalThrowableDialog(Throwable throwable){
        Log.w(TAG, throwable);
        Context context = MainApplication.getInstance().getApplicationContext();
        if (context != null) {
            String error = context.getString(com.czy.customviewlib.R.string.network_error);
            new Handler(Looper.getMainLooper()).post(() -> {
                MainApplication.getInstance().showGlobalDialog(error);
            });
        }
    }

    //==========Toast

    public static void globalThrowableToast(Throwable throwable){
        globalThrowableToast(throwable, com.czy.customviewlib.R.string.network_error);
    }

    public static void globalThrowableToast(Throwable throwable, int resId){
        String error = "";
        try {
            Context context = MainApplication.getInstance().getApplicationContext();
            if (context != null) {
                error = context.getString(resId);
            }
        } catch (Exception e){
            error = "";
        }
        String finalError = error;
        globalThrowableToast(throwable, finalError);
    }

    public static void globalThrowableToast(Throwable throwable, String error){
        Log.w(TAG, throwable);
        new Handler(Looper.getMainLooper()).post(() -> {
            MainApplication.getInstance().showGlobalToast(error);
        });
    }

    public static void globalToast(String error){
        new Handler(Looper.getMainLooper()).post(() -> {
            MainApplication.getInstance().showGlobalToast(error);
        });
    }

    //==========handle

    public static <T> boolean handleResponse(BaseResponse<T> response){
        return ResponseUtil.handleResponse(response, ViewModelUtil::globalThrowableToast);
    }

}
