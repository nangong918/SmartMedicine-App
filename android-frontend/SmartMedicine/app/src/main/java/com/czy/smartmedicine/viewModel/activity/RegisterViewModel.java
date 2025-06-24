package com.czy.smartmedicine.viewModel.activity;

import androidx.lifecycle.ViewModel;

import com.czy.appcore.network.netty.api.send.SocketMessageSender;
import com.czy.dal.vo.fragmentActivity.RegisterVo;
import com.czy.datalib.networkRepository.ApiRequestImpl;

public class RegisterViewModel extends ViewModel {

    private final static String TAG = RegisterViewModel.class.getName();

    private final ApiRequestImpl apiRequestImpl;
    private final SocketMessageSender socketMessageSender;

    public RegisterViewModel (ApiRequestImpl apiRequestImpl, SocketMessageSender socketMessageSender) {
        this.apiRequestImpl = apiRequestImpl;
        this.socketMessageSender = socketMessageSender;
    }

    //---------------------------Vo Ld---------------------------

    public RegisterVo registerVo = new RegisterVo();

}
