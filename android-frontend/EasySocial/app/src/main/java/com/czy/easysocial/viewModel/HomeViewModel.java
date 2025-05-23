package com.czy.easysocial.viewModel;

import androidx.lifecycle.ViewModel;

import com.czy.appcore.network.netty.api.send.SocketMessageSender;
import com.czy.dal.vo.viewModeVo.home.HomeVo;
import com.czy.datalib.networkRepository.ApiRequestImpl;

public class HomeViewModel extends ViewModel {

    private static final String TAG = HomeViewModel.class.getName();

    private final ApiRequestImpl apiRequestImpl;
    private final SocketMessageSender socketMessageSender;

    public HomeViewModel(ApiRequestImpl apiRequestImpl, SocketMessageSender socketMessageSender) {
        this.apiRequestImpl = apiRequestImpl;
        this.socketMessageSender = socketMessageSender;
    }

    //---------------------------Vo Ld---------------------------

    public HomeVo homeVo = new HomeVo();

    public void init(HomeVo homeVo){
        initVo(homeVo);
        initialNetworkRequest();
    }

    private void initVo(HomeVo homeVo){
        this.homeVo = homeVo;
    }

    //---------------------------NetWork---------------------------

    // 初始化网络请求
    private void initialNetworkRequest() {
    }

}
