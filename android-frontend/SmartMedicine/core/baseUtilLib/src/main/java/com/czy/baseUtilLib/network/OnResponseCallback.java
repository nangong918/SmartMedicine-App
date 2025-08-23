package com.czy.baseUtilLib.network;

public interface OnResponseCallback<T> {
    void onSuccess(T response);
    void onError(Throwable throwable);
}