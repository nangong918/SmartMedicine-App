package com.czy.smartmedicine.viewModel.fragment.home;


import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;

import com.czy.appcore.network.netty.api.send.SocketMessageSender;
import com.czy.dao.networkRepository.ApiRequestImpl;
import com.czy.domain.fragmentActivityAo.home.HomeFAo;
import com.czy.smartmedicine.fragment.home.children.HomeViewPagerAdapter;

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

    //---------------------------FAo Ld---------------------------

    public HomeViewPagerAdapter viewPagerAdapter;

    public HomeFAo homeFAo = new HomeFAo();

    public void init(HomeFAo homeFAo, Fragment fragment){
        this.homeFAo = homeFAo;
        viewPagerAdapter = new HomeViewPagerAdapter(
                fragment.getChildFragmentManager(),
                fragment.getLifecycle()
        );
    }



    //---------------------------NetWork---------------------------



    //---------------------------Logic---------------------------

}
