package com.czy.baseUtilsLib.network;


import androidx.lifecycle.MutableLiveData;

import java.io.IOException;
import java.util.Map;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class BaseApi {

    //--------------------统一泛型

    public <T> Disposable makeApiRequest(
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

    //--------------------请求头/响应头 统一泛型  (请求头设置好像在ViewModel中设置而不是这里)

    public <T> Disposable makeApiRequest(
            Observable<T> apiCall,
            MutableLiveData<T> liveData,
            OnThrowableCallback callback,
            Map<String, String> headersParams // 添加请求头参数
    ) {
        // 设置请求头
        Headers headers = setHeaders(headersParams);

        // 处理请求成功的结果
        return apiCall
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        liveData::postValue,
                        throwable -> {
                            // 处理请求错误
                            if (callback != null) {
                                callback.callback(throwable);
                            }
                        }
                );
    }

    //--------------------统一泛型 (更多选项)

    /**
     * 更细粒度控制网络请求：Scheduler控制，请求完成onComplete控制
     * @param apiCall           函数指针    （执行网络请求的接口）
     * @param subscribe         订阅线程    I/O 操作: Schedulers.io()、计算操作: Schedulers.computation()、新线程：Schedulers.newThread()
     * @param observe           观察线程    主线程：AndroidSchedulers.mainThread()、新线程：Schedulers.newThread()
     * @param netInterface      实现接口    onSubscribe订阅对象   onNext数据接受    onError错误处理    onComplete处理完成事件
     * @param <T>               响应泛型（请求泛型在apiCall中）
     */
    public <T> void makeApiRequestExtra(
            Observable<T> apiCall,
            Scheduler subscribe,
            Scheduler observe,
            NetInterface<T> netInterface
    ) {
        assert netInterface != null;
        apiCall
                .subscribeOn(subscribe)
                .observeOn(observe)
                .subscribe(new Observer<>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        // 可选：保存 Disposable，便于后续取消订阅
                        // Return Disposable
                        netInterface.onResSubscribe(d);
                    }

                    @Override
                    public void onNext(@NonNull T response) {
                        // 更新 LiveData
                        // liveData.postValue(response);
                        netInterface.onResNext(response);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        // 处理错误
                        // OnThrowableCallback callback.callback(e);
                        netInterface.onResError(e);
                    }

                    @Override
                    public void onComplete() {
                        // 可选：处理完成事件
                        netInterface.onResComplete();
                    }
                });
    }

    //--------------------设置请求头

    public Headers setHeaders(Map<String, String> headersParams) {
        okhttp3.Headers.Builder headersBuilder = new okhttp3.Headers.Builder();

        if (headersParams != null) {
            for (Map.Entry<String, String> entry : headersParams.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (value != null) {  // 可以选择对 null 值进行处理
                    headersBuilder.add(key, value);
                }
            }
        }

        return headersBuilder.build();
    }

    private void okPostBody(String url, String data, ObservableEmitter<String> emitter, Map<String, String> headersParams) {
        OkHttpClient client = BaseApiRequestProvider.createUploadOkHttpClient(10,10,10,30);
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(mediaType, data);

        Headers headers = setHeaders(headersParams);

        Request request = new Request.Builder()
                .url(url)
                .headers(headers)
                .post(body)
                .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@androidx.annotation.NonNull Call call, @androidx.annotation.NonNull IOException e) {
                emitter.onError(e);
            }

            @Override
            public void onResponse(@androidx.annotation.NonNull Call call, @androidx.annotation.NonNull Response response) throws IOException {
                if(response.body() == null){return;}
                emitter.onNext(response.body().string());
                emitter.onComplete();
            }
        });
    }

}
