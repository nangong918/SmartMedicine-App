package com.czy.baseUtilsLib.network;

import android.content.Context;

import com.czy.baseUtilsLib.ui.ToastUtils;

public class ResponseUtil {

    public static final String SUCCESS_CODE_STRING = "200";
    public static final Integer SUCCESS_CODE = 200;

    public static <T> boolean handleResponse(BaseResponse<T> response, Context context){
        if(response != null && response.getCode() != null){
            if (SUCCESS_CODE_STRING.equals(response.getCode())){
                return true;
            }
            else {
                ToastUtils.showToastActivity(context, response.getMessage());
                return false;
            }
        }
        else {
            ToastUtils.showToastActivity(context, "Internet Error");
            return false;
        }
    }

    public static <T> boolean handleResponse(BaseResponse<T> response, OnThrowableCallback onThrowableCallback){
        if(response != null && response.getCode() != null){
            if (SUCCESS_CODE_STRING.equals(response.getCode())){
                return true;
            }
            else {
                onThrowableCallback.callback(new Throwable(response.getMessage()));
                return false;
            }
        }
        else {
            onThrowableCallback.callback(new Throwable("Internet Error"));
            return false;
        }
    }
}
