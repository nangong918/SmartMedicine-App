package com.czy.smartmedicine.viewModel;

import androidx.lifecycle.ViewModel;

import com.czy.appcore.network.netty.api.send.SocketMessageSender;
import com.czy.dal.vo.viewModelVo.post.PublishPostVo;
import com.czy.datalib.networkRepository.ApiRequestImpl;

public class PublishViewModel extends ViewModel {

    private static final String TAG = PublishViewModel.class.getName();

    private final ApiRequestImpl apiRequestImpl;
    private final SocketMessageSender socketMessageSender;

    public PublishViewModel(ApiRequestImpl apiRequestImpl, SocketMessageSender socketMessageSender) {
        this.apiRequestImpl = apiRequestImpl;
        this.socketMessageSender = socketMessageSender;
    }

    //---------------------------Vo Ld---------------------------

    public PublishPostVo publishPostVo = new PublishPostVo();

    public void init(PublishPostVo publishPostVo) {
        this.publishPostVo = publishPostVo;

        initialNetworkRequest();
    }

    //---------------------------NetWork---------------------------

    // 初始化网络请求
    private void initialNetworkRequest() {
    }
}
