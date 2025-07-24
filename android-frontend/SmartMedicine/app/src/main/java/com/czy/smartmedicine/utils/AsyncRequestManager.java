package com.czy.smartmedicine.utils;



import android.util.Log;

import com.czy.appcore.network.api.handle.AsyncRequestCallback;
import com.czy.appcore.network.api.handle.WaitAllRequestFinishCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 异步请求的同步管理者
 * 需要初始化SyncAllRequestFinish
 */
public class AsyncRequestManager {
    private static final String TAG = AsyncRequestManager.class.getName();

    private int allRequestCount = 0;
    private final AtomicInteger requestFinishCount;
    private final List<Throwable> errorList;
    private final AsyncRequestCallback asyncRequestCallback;
    private WaitAllRequestFinishCallback waitAllRequestFinishCallback;

    public AsyncRequestManager(int allRequestCount){
        if (allRequestCount <= 0){
            throw new IllegalArgumentException("allRequestCount must > 0");
        }
        this.allRequestCount = allRequestCount;
        this.requestFinishCount = new AtomicInteger(allRequestCount);
        this.errorList = new ArrayList<>();
        this.clear();
        this.asyncRequestCallback = new AsyncRequestCallback() {
            @Override
            public synchronized void onThrowable(Throwable throwable) {
                errorList.add(throwable);
                int count = requestFinishCount.incrementAndGet();
                checkFinish(count);
            }

            @Override
            public synchronized void onSingleRequestSuccess() {
                int count = requestFinishCount.incrementAndGet();
                checkFinish(count);
            }
        };
    }

    public void setSyncAllRequestFinish(WaitAllRequestFinishCallback waitAllRequestFinishCallback){
        this.waitAllRequestFinishCallback = waitAllRequestFinishCallback;
    }

    public synchronized int getFinishCount(){
        return requestFinishCount.get();
    }

    public void clear(){
        this.errorList.clear();
        this.requestFinishCount.set(0);
    }

    private synchronized void checkFinish(int currentFinishCount){
        if (currentFinishCount >= allRequestCount){
            if (this.waitAllRequestFinishCallback != null){
                this.waitAllRequestFinishCallback.allFinish(errorList.isEmpty());
                if (!errorList.isEmpty()){
                    for (Throwable throwable : errorList){
                        if (throwable != null){
                            Log.e(TAG, "throwable: " + throwable.getMessage());
                        }
                    }
                }
                clear();
            }
        }
    }

    public AsyncRequestCallback getSyncRequestManagerCallback(){
        return asyncRequestCallback;
    }
}
