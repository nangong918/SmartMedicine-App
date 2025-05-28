package com.czy.baseUtilsLib.network;

import io.reactivex.disposables.Disposable;

public interface NetInterface<T> {

    void onResSubscribe(Disposable d);
    void onResError(Throwable e);
    void onResNext(T response);
    void onResComplete();
}
