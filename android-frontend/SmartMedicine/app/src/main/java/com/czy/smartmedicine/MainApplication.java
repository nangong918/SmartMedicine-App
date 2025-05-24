package com.czy.smartmedicine;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.MutableLiveData;

import com.czy.appcore.BaseConfig;
import com.czy.appcore.netty.IMessageListener;
import com.czy.appcore.network.api.ApiRequest;
import com.czy.appcore.network.netty.api.SocketApiResponseHandler;
import com.czy.appcore.network.netty.api.send.SocketMessageSender;
import com.czy.appcore.network.netty.queue.SocketMessageQueue;
import com.czy.appcore.network.netty.service.NettySocketServiceInitiator;
import com.czy.appcore.service.UserModel;
import com.czy.baseUtilsLib.file.SecuritySharedPreferencesUtils;
import com.czy.baseUtilsLib.image.ImageManager;
import com.czy.baseUtilsLib.network.BaseResponse;
import com.czy.baseUtilsLib.ui.ToastUtils;
import com.czy.customviewlib.view.GlobalDialogFragment;
import com.czy.dal.ao.chat.ChatContactItemAo;
import com.czy.dal.ao.chat.UserLoginInfoAo;
import com.czy.dal.constant.Constants;
import com.czy.dal.dto.netty.response.FileDownloadBytesResponse;
import com.czy.dal.netty.Message;
import com.czy.datalib.networkRepository.ApiRequestImpl;
import com.czy.appcore.network.api.ApiRequestProvider;
import com.czy.appcore.network.netty.service.NettySocketService;
import com.czy.smartmedicine.manager.HttpRequestManager;
import com.czy.smartmedicine.viewModel.ViewModelUtil;

import java.util.ArrayList;
import java.util.List;

public class MainApplication extends Application {

    public static final String TAG = MainApplication.class.getName();

    private static MainApplication mApp;

    //----------------------------启动APP调用----------------------------
    @Override
    public void onCreate() {
        super.onCreate();
        mApp = this;
        registerActivity();
        initGlobal();
    }

    //----------------------------init----------------------------

    public static MainApplication getInstance(){
        if (mApp == null){
            throw new RuntimeException("MainApplication is null");
        }
        return mApp;
    }

    //----------------------------global----------------------------

    private void initGlobal(){
        apiRequestInstance = getApiRequestInstance();
    }

    private ImageManager imageManager;

    public ImageManager getImageManager() {
        if (imageManager == null){
            imageManager = new ImageManager();
        }
        return imageManager;
    }

    //==========ApiRequest

    // 请求接口
    private static ApiRequest apiRequestInstance;

    public static synchronized ApiRequest getApiRequestInstance(){
        if (apiRequestInstance == null){
            apiRequestInstance = ApiRequestProvider.getApiRequest();
        }
        return apiRequestInstance;
    }

    // 请求接口实现
    private static ApiRequestImpl apiRequestImplInstance;

    public static synchronized ApiRequestImpl getApiRequestImplInstance(){
        if (apiRequestImplInstance == null){
            apiRequestImplInstance = new ApiRequestImpl(getApiRequestInstance());
        }
        return apiRequestImplInstance;
    }

    //==========SocketMessageQueue

    private static SocketMessageQueue socketMessageQueue;

    public static SocketMessageQueue getSocketMessageQueue() {
        if (socketMessageQueue == null){
            socketMessageQueue = new SocketMessageQueue();
        }
        return socketMessageQueue;
    }

    //==========NettySocketService

    private static IMessageListener messageListener;

    public static synchronized IMessageListener getMessageListener(){
        if (messageListener == null){
            messageListener = new IMessageListener.Stub() {
                @Override
                public void onMessageReceived(Message message) throws RemoteException {
                    Log.i(NettySocketService.TAG, "onMessageReceived: " + message.toJsonString());
                    SocketApiResponseHandler.handleMessage(message, socketMessageQueue);
                }

                @Override
                public void onConnectionStatusChanged(String netWorkState) throws RemoteException {
                    Log.i(NettySocketService.TAG, "onConnectionStatusChanged: " + netWorkState);
                    if (!TextUtils.isEmpty(netWorkState)){
                        if (netWorkState.equals(Constants.CONNECTED)){
                            Log.d(TAG, "onConnectionStatusChanged: " + Constants.CONNECTED);
                        }
                        else if (netWorkState.equals(Constants.DISCONNECTED)){
                            Log.d(TAG, "onConnectionStatusChanged: " + Constants.DISCONNECTED);
                            // 清空全部首次打开的缓存
                            HttpRequestManager.refreshAllValue();
                        }
                    }
                }
            };
        }
        return messageListener;
    }

    //==========ServiceInitiator
    private NettySocketServiceInitiator nettySocketServiceInitiator;

    // 连接WebSocket
    public void startNettySocketService(String senderAccount){
        nettySocketServiceInitiator = new NettySocketServiceInitiator();
        nettySocketServiceInitiator.initRemoteService(
                this,
                senderAccount,
                MainApplication.getMessageListener()
        );
        // 初始化MessageQueue
        socketMessageQueue = getSocketMessageQueue();
    }

    // disconnect
    public void disconnectNettySocketService(){
        if (nettySocketServiceInitiator != null){
            nettySocketServiceInitiator.disconnectNetty();
        }
    }

    //==========SocketMessageSender

    // sendMessage
    public SocketMessageSender getMessageSender(){
        if (nettySocketServiceInitiator == null){
            Log.w(TAG, "远程发送消息的Service未启动");
            return null;
        }
        return nettySocketServiceInitiator.getMessageSender();
    }

    //==========user

    private UserLoginInfoAo userLoginInfoAo;

    public UserLoginInfoAo getUserLoginInfoAo() {
        if (userLoginInfoAo == null){
            userLoginInfoAo = new UserLoginInfoAo();
            try {
                // SharePreferences
                SharedPreferences sp = SecuritySharedPreferencesUtils.getSecuritySharedPreferences(
                        UserModel.USER_INFO_FILE_NAME,
                        this
                );
                userLoginInfoAo.getFromSharePreferences(sp);
            } catch (Exception e) {
                Log.e(TAG, "getUserLoginInfoAo error", e);
            }
        }
        return userLoginInfoAo;
    }

    public void clearUserLoginInfoAo(){
        userLoginInfoAo = null;
        try {
            // SharePreferences
            SecuritySharedPreferencesUtils.clearSecuritySharedPreferences(
                    UserModel.USER_INFO_FILE_NAME,
                    this
            );
        } catch (Exception e) {
            Log.e(TAG, "clearUserLoginInfoAo error", e);
        }
    }

    public void setUserLoginInfoAo(UserLoginInfoAo userLoginInfoAo){
        this.userLoginInfoAo = userLoginInfoAo;
    }

    //==========messageList

    public List<ChatContactItemAo> chatContactList = new ArrayList<>();

    //==========friendsApplyNum

    public int friendsApplyNum = 0;

    //==========friendsList

    public List<ChatContactItemAo> friendList = new ArrayList<>();

    //----------------------------Global View----------------------------

    private FragmentActivity currentActivity;

    private void registerActivity() {
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {
                if (activity instanceof FragmentActivity) {
                    currentActivity = (FragmentActivity) activity;
                }
            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {

            }

            @Override
            public void onActivityPaused(@NonNull Activity activity) {

            }

            @Override
            public void onActivityStopped(@NonNull Activity activity) {
                if (currentActivity == activity) {
                    currentActivity = null;
                }
            }

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {

            }
        });
    }

    public void showGlobalDialog(String message) {
        if (currentActivity != null) {
            GlobalDialogFragment dialogFragment = GlobalDialogFragment.newInstance(message);
            dialogFragment.show(currentActivity.getSupportFragmentManager(), "GlobalDialog");
        }
        else {
            Log.e(TAG, "showGlobalDialog: currentActivity is null");
        }
    }

    public void showGlobalToast(String message){
        if (currentActivity != null) {
            ToastUtils.showLongToast(currentActivity, message, R.mipmap.ic_launcher);
        }
        else {
            Log.e(TAG, "showGlobalToast: currentActivity is null");
        }
    }

    public void showGlobalToast(int resId){
        if (currentActivity != null) {
            String message = "";
            try {
                Context context = MainApplication.getInstance().getApplicationContext();
                if (context != null) {
                    message = context.getString(resId);
                }
            } catch (Exception e) {
                message = "";
                Log.w(TAG, "showGlobalToast::resId is not exist " + resId, e);
            }
            ToastUtils.showLongToast(currentActivity, message, R.mipmap.ic_launcher);
        }
        else {
            Log.e(TAG, "showGlobalToast: currentActivity is null");
        }
    }

    public String getStringByResId(int resId){
        String message = "";
        try {
            Context context = MainApplication.getInstance().getApplicationContext();
            if (context != null) {
                message = context.getString(resId);
                return message;
            }
        } catch (Exception e) {
            message = "";
            Log.w(TAG, "showGlobalToast::resId is not exist " + resId, e);
        }
        return message;
    }

    //----------------------------Global Network----------------------------

    private void downloadImage(String url, MutableLiveData<Bitmap> bitmapLd){
        getApiRequestImplInstance().downloadImage(url,
                response -> {
                    handleDownloadImage(response, bitmapLd);
                },
                ViewModelUtil::globalThrowableToast
        );
    }

    private void handleDownloadImage(BaseResponse<FileDownloadBytesResponse> response, MutableLiveData<Bitmap> bitmapLd) {
        if (ViewModelUtil.handleResponse(response)) {
            Bitmap bitmap = getImageManager().bytesToBitmap(response.getData().getFileBytes());
            bitmap = getImageManager().processImage(bitmap, BaseConfig.BITMAP_MAX_SIZE);
            bitmapLd.postValue(bitmap);
        }
    }

    //----------------------------APP终止的时候调用----------------------------
    @Override
    public void onTerminate() {
        super.onTerminate();
    }

}
