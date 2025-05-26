package com.czy.smartmedicine.viewModel;

import androidx.lifecycle.ViewModel;

import com.czy.appcore.network.netty.api.send.SocketMessageSender;
import com.czy.baseUtilsLib.network.BaseResponse;
import com.czy.dal.dto.http.response.SinglePostResponse;
import com.czy.datalib.networkRepository.ApiRequestImpl;

public class PostViewModel extends ViewModel {

    private static final String TAG = PostViewModel.class.getName();

    private final ApiRequestImpl apiRequestImpl;
    private final SocketMessageSender socketMessageSender;

    public PostViewModel(ApiRequestImpl apiRequestImpl, SocketMessageSender socketMessageSender) {
        this.apiRequestImpl = apiRequestImpl;
        this.socketMessageSender = socketMessageSender;
    }

    //---------------------------Vo Ld---------------------------

    //---------------------------NetWork---------------------------

    public void getSinglePost(Long postId, Long pageNum){
        apiRequestImpl.getSinglePost(
                postId, pageNum,
                this::handleSinglePost,
                ViewModelUtil::globalThrowableToast
        );
    }

    private void handleSinglePost(BaseResponse<SinglePostResponse> response){
        //TODO
    }
}
