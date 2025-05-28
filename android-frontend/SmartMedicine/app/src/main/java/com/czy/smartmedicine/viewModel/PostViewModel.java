package com.czy.smartmedicine.viewModel;

import androidx.lifecycle.ViewModel;

import com.czy.appcore.network.netty.api.send.SocketMessageSender;
import com.czy.baseUtilsLib.network.BaseResponse;
import com.czy.dal.dto.http.response.SinglePostResponse;
import com.czy.dal.vo.viewModelVo.post.PostActivityVo;
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

    public PostActivityVo postActivityVo = new PostActivityVo();

    public void init(PostActivityVo postActivityVo) {
        this.postActivityVo = postActivityVo;

        initialNetworkRequest();
    }

    //---------------------------NetWork---------------------------

    // 初始化网络请求
    private void initialNetworkRequest() {
    }

    public void getSinglePost(Long postId, Long pageNum){
        apiRequestImpl.getSinglePost(
                postId, pageNum,
                this::handleSinglePost,
                ViewModelUtil::globalThrowableToast
        );
    }

    private void handleSinglePost(BaseResponse<SinglePostResponse> response){
        if (ViewModelUtil.handleResponse(response)) {
            SinglePostResponse singlePostResponse = response.getData();
            postActivityVo.postVoLd.initByPostVo(singlePostResponse.postVo);
            postActivityVo.commentVos = singlePostResponse.commentVos;
            postActivityVo.commentNumLd.setValue(
                    singlePostResponse.commentVos.size()
            );
        }
    }
}
