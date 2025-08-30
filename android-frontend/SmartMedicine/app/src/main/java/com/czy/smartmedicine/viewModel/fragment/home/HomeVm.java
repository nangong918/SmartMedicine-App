package com.czy.smartmedicine.viewModel.fragment.home;


import androidx.lifecycle.ViewModel;

import com.czy.appcore.network.netty.api.send.SocketMessageSender;
import com.czy.dao.networkRepository.ApiRequestImpl;
import com.czy.domain.fragmentActivityAo.HomeVo;

public class HomeVm extends ViewModel {

    private static final String TAG = HomeVm.class.getName();

    private final ApiRequestImpl apiRequestImpl;
    private final SocketMessageSender socketMessageSender;

    public HomeVm(ApiRequestImpl apiRequestImpl, SocketMessageSender socketMessageSender) {
        this.apiRequestImpl = apiRequestImpl;
        this.socketMessageSender = socketMessageSender;
    }

    public SocketMessageSender getSocketMessageSender(){
        return socketMessageSender;
    }

    //---------------------------Vo Ld---------------------------

    public HomeVo homeVo = new HomeVo();

    public void init(HomeVo homeVo){
        this.homeVo = homeVo;
    }

    //==========RecyclerView



    //---------------------------NetWork---------------------------



    //---------------------------Logic---------------------------

}
