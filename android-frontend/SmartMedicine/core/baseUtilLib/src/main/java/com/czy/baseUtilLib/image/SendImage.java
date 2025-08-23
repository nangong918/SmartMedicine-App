package com.czy.baseUtilLib.image;

import com.czy.baseUtilLib.network.OnThrowableCallback;

import okhttp3.RequestBody;

public interface SendImage {
    void sendImage(RequestBody paramsBody, OnThrowableCallback callback);
}
