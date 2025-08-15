package com.czy.smartmedicine;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.czy.appcore.netty.IMessageListener;
import com.czy.appcore.network.api.api.ApiRequest;
import com.czy.appcore.network.api.api.ApiRequestProvider;
import com.czy.appcore.network.netty.api.SocketApiResponseHandler;
import com.czy.appcore.network.netty.api.send.SocketMessageSender;
import com.czy.appcore.network.netty.queue.SocketMessageQueue;
import com.czy.appcore.network.netty.service.NettySocketService;
import com.czy.appcore.network.netty.service.NettySocketServiceInitiator;
import com.czy.appcore.service.chat.ChatMessageManager;
import com.czy.appcore.service.post.PostDataManager;
import com.czy.baseUtilsLib.file.SecuritySharedPreferencesUtils;
import com.czy.baseUtilsLib.image.ImageManager;
import com.czy.baseUtilsLib.ui.ToastUtils;
import com.czy.customviewlib.view.GlobalDialogFragment;
import com.czy.dal.ao.chat.ChatContactItemAo;
import com.czy.dal.ao.chat.UserLoginInfoAo;
import com.czy.dal.ao.login.LoginTokenAo;
import com.czy.dal.constant.NettyConstants;
import com.czy.dal.dto.http.request.BaseHttpRequest;
import com.czy.dal.netty.Message;
import com.czy.datalib.networkRepository.ApiRequestImpl;
import com.czy.smartmedicine.manager.HttpRequestManager;
import com.google.gson.Gson;

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
        this.chatMessageManager = getChatMessageManager();
        this.postDataManager = getPostDataManager();
    }

    private ImageManager imageManager;

    public ImageManager getImageManager() {
        if (imageManager == null){
            imageManager = new ImageManager();
        }
        return imageManager;
    }

    // ChatMessageManager

    private ChatMessageManager chatMessageManager;

    public ChatMessageManager getChatMessageManager(){
        if (chatMessageManager == null){
            chatMessageManager = new ChatMessageManager();
            // 启动轮询 可以考虑取消thread使用线程池
            chatMessageManager.start();
        }
        return chatMessageManager;
    }

    // PostDataManager

    private PostDataManager postDataManager;

    public PostDataManager getPostDataManager(){
        if (postDataManager == null){
            postDataManager = new PostDataManager();
        }
        return postDataManager;
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

    public static void setToken(){
        LoginTokenAo loginTokenAo = MainApplication.getInstance().getLoginTokenAo();
        ApiRequestProvider.getAuthInterceptor(loginTokenAo).setLoginTokenAo(loginTokenAo);
    }

    // 请求接口实现
    private static ApiRequestImpl apiRequestImplInstance;

    public static synchronized ApiRequestImpl getApiRequestImplInstance(){
        if (apiRequestImplInstance == null){
            apiRequestImplInstance = new ApiRequestImpl(getApiRequestInstance());
        }
        if (!ApiRequestProvider.isLogin()){
            Log.i(TAG, "LoginToken空了");
            setToken();
        }
        return apiRequestImplInstance;
    }

    //==========baseNettyRequest

    public BaseHttpRequest getBaseNettyRequest(){
        BaseHttpRequest request = new BaseHttpRequest();
        request.senderId = getUserLoginInfoAo().userId;
        request.receiverId = NettyConstants.SERVER_ID;
        request.timestamp = System.currentTimeMillis();
        return request;
    }

    public BaseHttpRequest getBaseNettyRequest(Long receiverId){
        BaseHttpRequest request = new BaseHttpRequest();
        request.senderId = getUserLoginInfoAo().userId;
        request.receiverId = receiverId;
        request.timestamp = System.currentTimeMillis();
        return request;
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
                        if (netWorkState.equals(NettyConstants.CONNECTED)){
                            Log.d(TAG, "onConnectionStatusChanged: " + NettyConstants.CONNECTED);
                        }
                        else if (netWorkState.equals(NettyConstants.DISCONNECTED)){
                            Log.d(TAG, "onConnectionStatusChanged: " + NettyConstants.DISCONNECTED);
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

    // 启动连接Netty的Service
    public void startNettySocketService(Long senderId){
        this.nettySocketServiceInitiator = new NettySocketServiceInitiator();
        this.nettySocketServiceInitiator.initRemoteService(
                this,
                senderId,
                MainApplication.getMessageListener()
        );
        // 初始化MessageQueue
        socketMessageQueue = getSocketMessageQueue();
    }

    // disconnect
    public void disconnectNettySocketService(){
        if (nettySocketServiceInitiator != null){
            nettySocketServiceInitiator.disconnectNetty();
            Log.i(TAG, "断开链接netty的service");
        }
    }

    //==========SocketMessageSender

    // sendMessage
    public SocketMessageSender getMessageSender(){
        Log.w("check_netty", "nettySocketServiceInitiator: " + this.nettySocketServiceInitiator);
        Log.i(TAG, "getMessageSender, nettySocketServiceInitiator = " + this.nettySocketServiceInitiator);
        if (this.nettySocketServiceInitiator == null){
            Log.w(TAG, "远程发送消息的Service未启动");
            return null;
        }
        return this.nettySocketServiceInitiator.getMessageSender();
    }

    //==========user

    private UserLoginInfoAo userLoginInfoAo;

    public UserLoginInfoAo getUserLoginInfoAo() {
        if (userLoginInfoAo == null){
            userLoginInfoAo = new UserLoginInfoAo();
            try {
                // SharePreferences
                SharedPreferences sp = SecuritySharedPreferencesUtils.getSecuritySharedPreferences(
                        UserLoginInfoAo.class.getName(),
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
        this.userLoginInfoAo = null;
        try {
            // SharePreferences
            SecuritySharedPreferencesUtils.clearSecuritySharedPreferences(
                    UserLoginInfoAo.class.getName(),
                    this
            );
        } catch (Exception e) {
            Log.e(TAG, "clearUserLoginInfoAo error", e);
        }
    }

    public void setUserLoginInfoAo(UserLoginInfoAo userLoginInfoAo){
        this.userLoginInfoAo = userLoginInfoAo;
        try {
            // SharePreferences
            SharedPreferences sp = SecuritySharedPreferencesUtils.getSecuritySharedPreferences(
                    UserLoginInfoAo.class.getName(),
                    this
            );
            userLoginInfoAo.saveToSharePreferences(sp);
        } catch (Exception e) {
            Log.e(TAG, "setUserLoginInfoAo error", e);
        }
    }

    public void clearAllSharePreferences() {
        clearLoginTokenAo();
        clearUserLoginInfoAo();
    }

    private LoginTokenAo loginTokenAo;

    public LoginTokenAo getLoginTokenAo() {
        if (loginTokenAo == null || loginTokenAo.isEmpty()){
            loginTokenAo = new LoginTokenAo();
            try {
                // SharePreferences
                SharedPreferences sp = SecuritySharedPreferencesUtils.getSecuritySharedPreferences(
                        LoginTokenAo.class.getName(),
                        this
                );
                loginTokenAo.getFromSharePreferences(sp);
            } catch (Exception e) {
                Log.e(TAG, "getUserLoginInfoAo error", e);
            }
        }
        return loginTokenAo;
    }

    public void setLoginTokenAo(LoginTokenAo loginTokenAo) {
        this.loginTokenAo = loginTokenAo;
        try {
            // SharePreferences
            SharedPreferences sp = SecuritySharedPreferencesUtils.getSecuritySharedPreferences(
                    LoginTokenAo.class.getName(),
                    this
            );
            loginTokenAo.saveToSharePreferences(sp);
        } catch (Exception e) {
            Log.e(TAG, "setUserLoginInfoAo error", e);
        }
    }

    public void clearLoginTokenAo(){
        this.loginTokenAo = null;
        try {
            // SharePreferences
            SecuritySharedPreferencesUtils.clearSecuritySharedPreferences(
                    LoginTokenAo.class.getName(),
                    this
            );
        } catch (Exception e) {
            Log.e(TAG, "clearLoginTokenAo error", e);
        }
    }

    //==========messageList

//    public List<ChatContactItemAo> chatContactList = new ArrayList<>();

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

    //----------------------------utils----------------------------

    private static Gson GSON;

    public static Gson getGson() {
        if (GSON == null) {
            GSON = new Gson();
        }
        return GSON;
    }

    //----------------------------APP终止的时候调用----------------------------
    @Override
    public void onTerminate() {
        super.onTerminate();
    }


}
