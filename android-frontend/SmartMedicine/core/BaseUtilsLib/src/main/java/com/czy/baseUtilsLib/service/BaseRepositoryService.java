package com.czy.baseUtilsLib.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.czy.baseUtilsLib.network.OnThrowableCallback;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class BaseRepositoryService extends Service {

    protected CompositeDisposable disposables;

    protected static final String TAG = Service.class.getSimpleName();

    public BaseRepositoryService() {
        disposables = new CompositeDisposable();
    }

    /**
     * 获取避免重复观察的liveData
     * @param liveData  被观察的liveData
     * @return          MutableLiveData<T>
     * @param <T>       观察的类型
     */
    protected <T> MutableLiveData<T> getLiveData(MutableLiveData<T> liveData) {
        if (liveData == null) {
            liveData = new MutableLiveData<>();
        }
        return liveData;
    }

    /**
     * 订阅
     * @param operation             操作函数，返回Observable<T>
     * @param handleLiveData        响应操作的接口
     * @param throwableCallback     异常报错的callback
     * @return          订阅对象
     * @param <T>       响应体类型
     */
    public <T> Disposable getDisposableOperate(
            @NonNull Observable<T> operation,
            HandleLiveData<T> handleLiveData,
            OnThrowableCallback throwableCallback) {
        return operation
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        data -> {
                            if(handleLiveData != null){
                                handleLiveData.handle(data);
                            }
                        },
                        throwable -> {
                            if (throwableCallback != null) {
                                throwableCallback.callback(throwable);
                            }
                        }
                );
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disposables.clear();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
