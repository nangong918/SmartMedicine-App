package com.czy.smartmedicine.viewModel.activity;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.czy.appcore.network.api.handle.SyncRequestCallback;
import com.czy.appcore.network.netty.api.send.SocketMessageSender;
import com.czy.baseUtilsLib.network.BaseResponse;
import com.czy.baseUtilsLib.ui.ToastUtils;
import com.czy.dal.dto.http.response.SinglePostResponse;
import com.czy.dal.vo.fragmentActivity.post.PostActivityVo;
import com.czy.datalib.networkRepository.ApiRequestImpl;
import com.czy.smartmedicine.utils.ResponseTool;
import com.czy.smartmedicine.utils.ViewModelUtil;

import java.util.Optional;

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

    public void getSinglePost(Long pageNum, Context context, SyncRequestCallback callback){
        Long postId = Optional.ofNullable(postActivityVo)
                        .map(vo -> vo.postVoLd)
                        .map(pvo -> pvo.postIdLd)
                        .map(LiveData::getValue)
                        .orElse(null);

        if (postId == null){
            ToastUtils.showToast(context, context.getString(com.czy.customviewlib.R.string.system_error));
            callback.onThrowable(new Throwable("post id is null"));
            return;
        }

        apiRequestImpl.getSinglePost(
                postId, pageNum,
                response -> ResponseTool.handleSyncResponseEx(
                        response,
                        context,
                        callback,
                        this::handleSinglePost
                ),
                throwable -> {
                    Log.w(TAG, throwable);
                    ViewModelUtil.globalThrowableToast(throwable);
                }
        );
    }

    private void handleSinglePost(BaseResponse<SinglePostResponse> response, Context context, SyncRequestCallback callback){
        SinglePostResponse singlePostResponse = response.getData();
        postActivityVo.postVoLd.initByPostVo(singlePostResponse.postVo);
        postActivityVo.commentVos = singlePostResponse.commentVos;
        postActivityVo.commentNumLd.setValue(
                singlePostResponse.commentVos.size()
        );
        callback.onAllRequestSuccess();
    }
}
