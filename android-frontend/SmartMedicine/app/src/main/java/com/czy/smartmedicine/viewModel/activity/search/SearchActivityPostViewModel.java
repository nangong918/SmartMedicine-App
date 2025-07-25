package com.czy.smartmedicine.viewModel.activity.search;

import androidx.lifecycle.ViewModel;

import com.czy.appcore.network.netty.api.send.SocketMessageSender;
import com.czy.datalib.networkRepository.ApiRequestImpl;

public class SearchActivityPostViewModel extends ViewModel {

    private static final String TAG = SearchActivityPostViewModel.class.getName();

    private final ApiRequestImpl apiRequestImpl;
    private final SocketMessageSender socketMessageSender;

    public SearchActivityPostViewModel(ApiRequestImpl apiRequestImpl, SocketMessageSender socketMessageSender){
        this.apiRequestImpl = apiRequestImpl;
        this.socketMessageSender = socketMessageSender;
    }

    //---------------------------Vo Ld---------------------------
}
