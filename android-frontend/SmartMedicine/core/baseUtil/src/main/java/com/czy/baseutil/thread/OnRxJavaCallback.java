package com.czy.baseutil.thread;

public interface OnRxJavaCallback <T> {
    void onSuccess(T t);
    void onFailed(Throwable e);
}
