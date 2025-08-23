package com.czy.baseUtilLib.thread;

public interface OnRxJavaCallback <T> {
    void onSuccess(T t);
    void onFailed(Throwable e);
}
