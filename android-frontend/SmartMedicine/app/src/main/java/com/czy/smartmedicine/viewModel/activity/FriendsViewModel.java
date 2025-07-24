package com.czy.smartmedicine.viewModel.activity;


import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.czy.appcore.network.netty.api.receive.FriendApiHandler;
import com.czy.appcore.network.netty.api.send.SocketMessageSender;
import com.czy.baseUtilsLib.network.BaseResponse;
import com.czy.dal.constant.Constants;
import com.czy.dal.dto.http.request.BaseHttpRequest;
import com.czy.dal.dto.netty.response.AddUserToTargetUserResponse;
import com.czy.dal.dto.netty.response.HandleAddUserResponse;
import com.czy.dal.vo.fragmentActivity.FriendsVo;
import com.czy.datalib.networkRepository.ApiRequestImpl;
import com.czy.smartmedicine.MainApplication;
import com.czy.smartmedicine.fragment.friends.FriendsFragment;
import com.czy.smartmedicine.manager.HttpRequestManager;
import com.czy.smartmedicine.utils.ViewModelUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Optional;


public class FriendsViewModel extends ViewModel {

    private static final String TAG = FriendsViewModel.class.getSimpleName();

    private final ApiRequestImpl apiRequestImpl;
    private final SocketMessageSender socketMessageSender;

    public FriendsViewModel(ApiRequestImpl apiRequestImpl, SocketMessageSender socketMessageSender) {
        this.apiRequestImpl = apiRequestImpl;
        this.socketMessageSender = socketMessageSender;
    }

    //---------------------------Vo Ld---------------------------

    public FriendsVo friendsVo = new FriendsVo();

    public void init(FriendsVo friendsVo) {
        initVo(friendsVo);
        initReceiveAddUserApi();
        initialNetworkRequest();
    }

    private void initVo(FriendsVo friendsVo){
        this.friendsVo = friendsVo;
    }

    //---------------------------NetWork---------------------------

    private FriendApiHandler friendApiHandler;

    // 消息队列
    private final Handler messageHandler = new Handler(Looper.getMainLooper());

    // 首次进入好友列表申请根本就不在FriendsFragment，而是在ViewPager的Fragment中
    private void initReceiveAddUserApi(){
        initEventBus();
        friendApiHandler = new FriendApiHandler() {
            @Override
            public void receiveAddedFriend(@NonNull AddUserToTargetUserResponse response) {
                Log.i(TAG, "receiveAddedFriend: " + response.toJsonString());
                messageHandler.post(() -> {
                    processFriendsMessage();
                });
                // TODO 消息弹窗提示
            }

            @Override
            public void receiveBeDeleted(@NonNull AddUserToTargetUserResponse response) {

            }

            @Override
            public void receiveAddFriendResult(@NonNull HandleAddUserResponse response) {
                Log.i(TAG, "receiveAddFriendResult: " + response.toJsonString());
                messageHandler.post(() -> {
                    processFriendsMessage();
                });
                // 消息弹窗提示
            }
        };
    }

    private synchronized void processFriendsMessage(){
        int newFriends = Optional.ofNullable(friendsVo)
                .map(vo -> vo.newFriends)
                .map(LiveData::getValue)
                .orElse(0);
        newFriends += 1;
        int finalNewFriends = newFriends;
        Optional.ofNullable(friendsVo)
                .map(vo -> vo.newFriends)
                .ifPresent(ld -> ld.postValue(finalNewFriends));
    }

    private void initialNetworkRequest(){
        // 首次打开：Http请求
        if (HttpRequestManager.getIsFirstOpen(FriendsFragment.class.getName())){
            BaseHttpRequest request = new BaseHttpRequest();
            request.senderId = Optional.ofNullable(MainApplication.getInstance().getUserLoginInfoAo())
                    .map(ao -> ao.userId)
                    .orElse(Constants.ERROR_ID);
            if (Constants.ERROR_ID.equals(request.senderId)){
                Log.w(TAG, "doGetUserNewMessage: senderId is empty");
                return;
            }
            doGetMyFriendApplyList(request);
        }
        else {
            int num = MainApplication.getInstance().friendsApplyNum;
            friendsVo.newFriends.postValue(num);
        }
    }

    //==========获取与我相关的添加请求

    private void doGetMyFriendApplyList(BaseHttpRequest request){
        apiRequestImpl.getMyFriendApplyList(
                request,
                this::handleGetMyFriendApplyList,
                ViewModelUtil::globalThrowableToast
        );
    }

    private void handleGetMyFriendApplyList(BaseResponse<Integer> response) {
        if (ViewModelUtil.handleResponse(response)){
            Optional.ofNullable(friendsVo)
                    .map(vo -> vo.newFriends)
                    .ifPresent(ld -> ld.postValue(response.getData()));
        }
    }

    //---------------------------EventBus---------------------------

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onMessageReceived(AddUserToTargetUserResponse response) {
        if (response != null){
            friendApiHandler.receiveAddedFriend(response);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onMessageReceived(HandleAddUserResponse response) {
        if (response != null){
            friendApiHandler.receiveAddFriendResult(response);
        }
    }

    private void initEventBus() {
        EventBus.getDefault().register(this);
    }

    private void unInitEventBus() {
        EventBus.getDefault().unregister(this);
    }

    //---------------------------logic---------------------------

    public void storage(){
        MainApplication.getInstance().friendsApplyNum = Optional.ofNullable(friendsVo)
                .map(vo -> vo.newFriends)
                .map(LiveData::getValue)
                .orElse(0);
    }

    public void onDestroy() {
        storage();
        unInitEventBus();
    }
}
