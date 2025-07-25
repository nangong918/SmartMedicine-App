package com.czy.smartmedicine.viewModel.activity;

import android.content.Context;
import android.text.TextUtils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.czy.appcore.network.api.handle.SyncRequestCallback;
import com.czy.appcore.network.netty.api.send.SocketMessageSender;
import com.czy.baseUtilsLib.network.BaseResponse;
import com.czy.dal.ao.chat.UserLoginInfoAo;
import com.czy.dal.constant.Constants;
import com.czy.dal.dto.http.request.UserBriefRequest;
import com.czy.dal.dto.http.response.UserBriefResponse;
import com.czy.dal.vo.entity.home.PostVo;
import com.czy.dal.vo.fragmentActivity.UserBriefVo;
import com.czy.datalib.networkRepository.ApiRequestImpl;
import com.czy.smartmedicine.MainApplication;
import com.czy.smartmedicine.utils.ResponseTool;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserBriefViewModel extends ViewModel {

    private static final String TAG = UserBriefViewModel.class.getName();

    private final ApiRequestImpl apiRequestImpl;
    private final SocketMessageSender socketMessageSender;

    public UserBriefViewModel(ApiRequestImpl apiRequestImpl, SocketMessageSender socketMessageSender){
        this.apiRequestImpl = apiRequestImpl;
        this.socketMessageSender = socketMessageSender;
    }

    //---------------------------Vo Ld---------------------------

    public UserBriefVo userBriefVo = new UserBriefVo();

    public void init(UserBriefVo vo) {
        this.userBriefVo = vo;
    }

    //---------------------------NetWork---------------------------

    public void doGetUserBrief(Context context, SyncRequestCallback callback){
        UserLoginInfoAo userLoginInfoAo = MainApplication.getInstance().getUserLoginInfoAo();
        Long senderId = Optional.ofNullable(userLoginInfoAo)
                .map(ao -> ao.userId)
                .orElse(Constants.ERROR_ID);
        Long receiverId = Optional.ofNullable(userBriefVo)
                .map(vo -> vo.userId)
                .orElse(Constants.ERROR_ID);
        UserBriefRequest request = new UserBriefRequest();
        request.senderId = senderId;
        request.receiverId = receiverId;
        apiRequestImpl.getUserBrief(request,
                response -> {
                    ResponseTool.handleSyncResponseEx(
                            response,
                            context,
                            callback,
                            this::handleGetUserBrief
                    );
                },
                callback::onThrowable
        );
    }

    private void handleGetUserBrief(BaseResponse<UserBriefResponse> response,
                                    Context context,
                                    SyncRequestCallback callback
                                    ){
        String userName = Optional.ofNullable(response.getData())
                .map(data -> data.userView)
                .map(view -> view.userName)
                .orElse("");
        String userAccount = Optional.ofNullable(response.getData())
                .map(data -> data.userView)
                .map(view -> view.userAccount)
                .orElse("");
        String avatarUrl = Optional.ofNullable(response.getData())
                .map(data -> data.userView)
                .map(view -> view.avatarUrl)
                .orElse("");
        String userRemark = Optional.ofNullable(response.getData())
                .map(data -> data.userRemark)
                .orElse("");

        List<PostVo> userPosts = Optional.ofNullable(response.getData())
                .map(data -> data.userPosts)
                .orElse(new ArrayList<>());

        Long avatarFileId = Optional.ofNullable(response.getData())
                .map(data -> data.userView)
                .map(view -> view.avatarFileId)
                .orElse(null);

        Long userId = Optional.ofNullable(response.getData())
                .map(data -> data.userView)
                .map(view -> view.userId)
                .orElse(Constants.ERROR_ID);

        userBriefVo.userName.setValue(userName);
        userBriefVo.userAccount.setValue(userAccount);
        if (!TextUtils.isEmpty(avatarUrl)){
            userBriefVo.avatarUrl.setValue(avatarUrl);
        }
        if (!TextUtils.isEmpty(userRemark)){
            userBriefVo.userRemark.setValue(userRemark);
        }

        userBriefVo.userPosts = userPosts;
        userBriefVo.avatarFileId = avatarFileId;
        userBriefVo.userId = userId;
    }

    //---------------------------logic---------------------------

}
