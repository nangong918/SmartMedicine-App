package com.czy.appcore.network.api.handle;

public interface WaitAllRequestFinishCallback {
    /**
     * 所有请求完成
     * @param isAllSuccess  是否所有请求成功
     */
    void allFinish(boolean isAllSuccess);
}
