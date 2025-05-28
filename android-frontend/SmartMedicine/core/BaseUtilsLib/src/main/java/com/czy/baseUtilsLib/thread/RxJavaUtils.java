package com.czy.baseUtilsLib.thread;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.android.schedulers.AndroidSchedulers;


public class RxJavaUtils {

    /**
     * 线程调度
     * @param observable      被观察者 [一般由Runnable返回生成，RxJava会对生成observable的Runnable执行任务]
     * @param schedulers      线程执行位置：Schedulers; AndroidSchedulers
     * @param callback        回调
     * @return                Disposable
     * @param <T>            被观察者返回值类型
     */
    public static <T> Disposable launch(
            @Nullable Observable<T> observable,
            @NonNull Scheduler[] schedulers,
            @Nullable OnRxJavaCallback<T> callback){
        if (observable == null){
            return null;
        }
        return observable
                .subscribeOn(schedulers[0])
                .observeOn(schedulers[1])
                .subscribe(
                        result -> {
                            if (callback != null){
                                callback.onSuccess(result);
                            }
                        },
                        throwable -> {
                            if (callback != null){
                                callback.onFailed(throwable);
                            }
                        }
                );
    }

}
