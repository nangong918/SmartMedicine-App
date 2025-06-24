package com.czy.smartmedicine.viewModel.activity;

import androidx.lifecycle.ViewModel;

import com.czy.appcore.network.netty.api.send.SocketMessageSender;
import com.czy.dal.vo.viewModelVo.userBrief.UserBriefVo;
import com.czy.datalib.networkRepository.ApiRequestImpl;

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

    //---------------------------logic---------------------------
}
