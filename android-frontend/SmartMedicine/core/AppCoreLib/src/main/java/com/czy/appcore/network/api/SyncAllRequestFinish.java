package com.czy.appcore.network.api;

public interface SyncAllRequestFinish {
    /**
     * 所有请求完成
     * @param isAllSuccess  是否所有请求成功
     */
    void allFinish(boolean isAllSuccess);
}
