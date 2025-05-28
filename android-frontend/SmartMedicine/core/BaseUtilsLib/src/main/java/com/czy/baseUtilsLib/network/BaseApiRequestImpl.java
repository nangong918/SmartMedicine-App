package com.czy.baseUtilsLib.network;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class BaseApiRequestImpl {

    protected CompositeDisposable disposables;

    protected static final String TAG = "ApiRequestImpl";

    public BaseApiRequestImpl(){
        disposables = new CompositeDisposable();
    }

    /**
     * RxJava响应式执行 + LiveData订阅
     * @param apiCall   通过网络请求获得的订阅对象
     * @param liveData  响应网络请求的liveData
     * @param callback  异常报错的callback
     * @return          订阅对象
     * @param <T>       响应体类型
     */
    protected <T> Disposable getDisposableOperate(
            Observable<T> apiCall,
            MutableLiveData<T> liveData,
            OnThrowableCallback callback) {
        return apiCall
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        liveData::postValue,
                        throwable -> {
                            if (callback != null) {
                                callback.callback(throwable);
                            }
                        }
                );
    }

    /**
     * RxJava响应式执行 + Callback订阅（无需ViewModel）
     * @param apiCall           通过网络请求获得的订阅对象
     * @param responseCallback  响应之后的Callback
     * @return                  订阅对象
     * @param <T>               T响应体类型
     */
    protected <T> Disposable getDisposableOperate(
            Observable<T> apiCall,
            OnResponseCallback<T> responseCallback) {
        return apiCall
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        response -> {
                            if (responseCallback != null) {
                                responseCallback.onSuccess(response);  // 返回成功的响应
                            }
                        },
                        throwable -> {
                            if (responseCallback != null) {
                                responseCallback.onError(throwable);  // 返回错误信息
                            }
                        }
                );
    }

    // LiveData的网络数据被改变之后变成假网络请求
    protected <T> Disposable getDisposableOperate(
            Observable<T> apiCall,
            OnSuccessCallback<T> successCallback,
            OnThrowableCallback throwableCallback) {
        return apiCall
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        response -> {
                            if (successCallback != null) {
                                successCallback.onResponse(response);  // 返回成功的响应
                            }
                        },
                        throwable -> {
                            if (throwableCallback != null) {
                                throwableCallback.callback(throwable);  // 返回错误信息
                            }
                        }
                );
    }

    /**
     * 发送与观察
     * @param observable                泛型O观察对象
     * @param liveData                  liveData
     * @param callback                  异常callback
     * @param owner                     观察者的lifeCycleOwner
     * @param onBaseSuccessCallback        响应之后的Callback
     * @param <T>                       T响应体类型
     */
    public <T> void sendRequestObserve(Observable<BaseResponse<T>> observable,
                                       OnThrowableCallback callback,
                                       MutableLiveData<BaseResponse<T>> liveData,
                                       LifecycleOwner owner,
                                       OnBaseSuccessCallback<T> onBaseSuccessCallback){
        // 订阅RxJava
        this.disposables.add(
                getDisposableOperate(observable, liveData, callback)
        );
        // LiveData订阅响应 (此处liveData总是非空)
        liveData.observe(owner, onBaseSuccessCallback::onResponse);
    }

    public <T> void sendRequestCallback(Observable<BaseResponse<T>> observable,
                                       OnResponseCallback<BaseResponse<T>> callback){
        this.disposables.add(
                getDisposableOperate(observable, callback)
        );
    }
    public <T> void sendRequestCallback(Observable<BaseResponse<T>> observable,
                                        OnSuccessCallback<BaseResponse<T>> successCallback,
                                        OnThrowableCallback throwableCallback){
        this.disposables.add(
                getDisposableOperate(
                        observable,
                        successCallback,
                        throwableCallback
                )
        );
    }

    /**
     * 关闭订阅
     */
    public void dispose(){
        if(disposables != null){
            disposables.clear();
        }
    }
}
