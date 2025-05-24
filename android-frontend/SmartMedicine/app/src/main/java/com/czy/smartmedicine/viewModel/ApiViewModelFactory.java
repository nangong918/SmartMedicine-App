package com.czy.smartmedicine.viewModel;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.czy.appcore.network.netty.api.send.SocketMessageSender;
import com.czy.datalib.networkRepository.ApiRequestImpl;

import java.lang.reflect.Constructor;

public class ApiViewModelFactory implements ViewModelProvider.Factory {
    private static final String TAG = ApiViewModelFactory.class.getSimpleName();

    private final ApiRequestImpl apiRequestImpl;
    private final SocketMessageSender socketMessageSender;

    public ApiViewModelFactory(ApiRequestImpl apiRequest, SocketMessageSender socketMessageSender) {
        this.apiRequestImpl = apiRequest;
        this.socketMessageSender = socketMessageSender;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        try {
            // 获取 ViewModel 的构造器，参数为 ApiRequest
            Constructor<T> constructor = modelClass.getConstructor(ApiRequestImpl.class, SocketMessageSender.class);
            // 创建实例并返回
            return constructor.newInstance(apiRequestImpl, socketMessageSender);
        } catch (Exception e) {
            Log.e(TAG, "Unknown ViewModel class: " + modelClass.getName(), e);
            throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName(), e);
        }
    }
}
