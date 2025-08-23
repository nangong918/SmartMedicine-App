package com.czy.smartmedicine.viewModel.activity;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.czy.appcore.network.api.handle.SyncRequestCallback;
import com.czy.appcore.network.netty.api.send.SocketMessageSender;
import com.czy.baseUtilsLib.network.BaseResponse;
import com.czy.baseUtilsLib.ui.ToastUtils;
import com.czy.domain.constant.NettyConstants;
import com.czy.domain.dto.http.request.GetSinglePostRequest;
import com.czy.domain.dto.http.request.RecommendPostRequest;
import com.czy.domain.dto.http.response.SinglePostResponse;
import com.czy.domain.fragmentActivityAo.post.PostActivityVo;
import com.czy.datalib.networkRepository.ApiRequestImpl;
import com.czy.smartmedicine.MainApplication;
import com.czy.smartmedicine.utils.ResponseTool;
import com.czy.smartmedicine.utils.ViewModelUtil;

import java.util.Optional;

public class PostVm extends ViewModel {

    private static final String TAG = PostVm.class.getName();

    private final ApiRequestImpl apiRequestImpl;
    private final SocketMessageSender socketMessageSender;

    public PostVm(ApiRequestImpl apiRequestImpl, SocketMessageSender socketMessageSender) {
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

    public void getSinglePost(Integer pageNum, Context context, SyncRequestCallback callback){
        Long postId = Optional.ofNullable(postActivityVo)
                        .map(vo -> vo.postVoLd)
                        .map(pvo -> pvo.postIdLd)
                        .map(LiveData::getValue)
                        .orElse(null);

        if (postId == null){
            ToastUtils.showToast(context, context.getString(com.czy.appview.R.string.system_error));
            callback.onThrowable(new Throwable("post id is null"));
            return;
        }

        GetSinglePostRequest request = new GetSinglePostRequest();
        request.postId = postId;
        request.pageNum = pageNum;

        apiRequestImpl.getSinglePost(
                request,
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

    public void testGetRandomPosts(Context context, SyncRequestCallback callback){
        RecommendPostRequest request = new RecommendPostRequest();
        request.userId = Optional.ofNullable(MainApplication.getInstance().getUserLoginInfoAo())
                .map(ao -> ao.userId)
                .orElse(NettyConstants.ERROR_ID);
        apiRequestImpl.recommendTestGetRandomPost(
                request,
                response -> {},
                throwable -> {
                    callback.onThrowable(throwable);
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
