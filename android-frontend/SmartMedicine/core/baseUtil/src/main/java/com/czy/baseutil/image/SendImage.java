package com.czy.baseutil.image;

import com.czy.baseutil.network.OnThrowableCallback;

import okhttp3.RequestBody;

public interface SendImage {
    void sendImage(RequestBody paramsBody, OnThrowableCallback callback);
}
