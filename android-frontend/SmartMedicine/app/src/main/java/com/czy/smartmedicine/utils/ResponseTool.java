package com.czy.smartmedicine.utils;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.czy.appcorelib.network.api.AsynRequestCallback;
import com.czy.appcorelib.network.api.FourConsumer;
import com.czy.appcorelib.network.api.SyncRequestCallback;
import com.czy.appcorelib.network.api.TriConsumer;
import com.czy.baseUtilsLib.activity.ActivityLaunchUtils;
import com.czy.baseUtilsLib.network.ResponseUtil;
import com.czy.dal.constant.CodeConstant;
import com.czy.dal.dto.base.BaseHttpResponse;
import com.czy.view.CustomToast;
import com.loan.scorecash.MainApplication;
import com.loan.scorecash.activity.SignActivity;

import java.util.function.BiConsumer;

public abstract class ResponseTool extends ResponseUtil {

    public static boolean handleResponse(BaseHttpResponse response, Context context){
        if(response != null && response.getCode() != null){
            boolean needLoginAgain = false;
            for (String code : CodeConstant.RESIGN_CODE) {
                if (code.equals(response.getCode())){
                    needLoginAgain = true;
                    break;
                }
            }
            if (response.getCode().equals(CodeConstant.SUCCESS_CODE)){
                return true;
            }
            else if(needLoginAgain){
                MainApplication.getInstance().clearAllSharePreferences();
                if(context instanceof Activity){
                    ((Activity) context).runOnUiThread(() -> CustomToast.showToast(context, response.getMessage()));
                }
                if(context instanceof Activity){
                    ActivityLaunchUtils.launchNewTask(context, SignActivity.class, null);
                }
                return false;
            }
            else {
                if(context instanceof Activity){
                    ((Activity) context).runOnUiThread(() -> CustomToast.showToast(context, response.getMessage()));
                }
                return false;
            }
        }
        else {
            if(context instanceof Activity){
                ((Activity) context).runOnUiThread(() -> Toast.makeText(context, context.getString(com.czy.view.R.string.please_check_your_network), Toast.LENGTH_LONG).show());
            }
            return false;
        }
    }

    public static boolean handleSyncResponse(BaseHttpResponse response, Context context, AsynRequestCallback callback){
        boolean result = handleResponse(response, context);
        if (result){
            callback.onSingleRequestSuccess();
        }
        else {
            callback.onThrowable(new Throwable(AsynRequestCallback.RESPONSE_BASE_ERROR));
        }
        return result;
    }

    public static <T extends BaseHttpResponse> void handleAsynResponseEx(
            T response,
            Context context,
            AsynRequestCallback callback,
            BiConsumer<T, Context> handler) {

        boolean result = handleResponse(response, context);
        if (result) {
            handler.accept(response, context);
            callback.onSingleRequestSuccess();
        } else {
            callback.onThrowable(new Throwable(AsynRequestCallback.RESPONSE_BASE_ERROR));
        }
    }

    public static <T extends BaseHttpResponse> void handleSyncResponseEx(
            T response,
            Context context,
            SyncRequestCallback callback,
            TriConsumer<T, Context, SyncRequestCallback> handler) {

        boolean result = handleResponse(response, context);
        if (result) {
            handler.accept(response, context, callback); // 传递三个参数
            // 同步调用需要在最后一个请求成功后手动调用
//        callback.onAllRequestSuccess();
        } else {
            callback.onThrowable(new Throwable(SyncRequestCallback.RESPONSE_BASE_ERROR));
        }
    }

    public static <T extends BaseHttpResponse> void handleSyncResponseEx(
            T response,
            Context context,
            SyncRequestCallback callback,
            Object param,
            FourConsumer<T, Context, SyncRequestCallback, Object> handler) {

        boolean result = handleResponse(response, context);
        if (result) {
            handler.accept(response, context, callback, param); // 传递三个参数
            // 同步调用需要在最后一个请求成功后手动调用
//        callback.onAllRequestSuccess();
        } else {
            callback.onThrowable(new Throwable(SyncRequestCallback.RESPONSE_BASE_ERROR));
        }
    }

}
