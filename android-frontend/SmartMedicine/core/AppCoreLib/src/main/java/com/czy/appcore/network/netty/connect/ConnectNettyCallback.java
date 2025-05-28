package com.czy.appcore.network.netty.connect;

public interface ConnectNettyCallback {
    void onConnectSuccess();
    void onConnectFailure(String errorMessage);
}
