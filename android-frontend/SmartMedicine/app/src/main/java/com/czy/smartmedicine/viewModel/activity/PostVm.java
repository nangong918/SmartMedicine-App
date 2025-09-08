package com.czy.smartmedicine.viewModel.activity;

import android.content.Context;
import android.util.Log;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModel;

import com.czy.appcore.network.api.handle.SyncRequestCallback;
import com.czy.appcore.network.netty.api.send.SocketMessageSender;
import com.czy.baseutil.network.BaseResponse;
import com.czy.baseutil.network.networkLoad.NetworkLoadUtils;
import com.czy.baseutil.ui.ToastUtils;
import com.czy.dao.networkRepository.ApiRequestImpl;
import com.czy.domain.constant.NettyConstants;
import com.czy.domain.dto.http.request.GetSinglePostRequest;
import com.czy.domain.dto.http.request.RecommendPostRequest;
import com.czy.domain.dto.http.response.SinglePostResponse;
import com.czy.domain.fragmentActivityAo.post.PostAAo;
import com.czy.smartmedicine.MainApplication;
import com.czy.smartmedicine.utils.ResponseTool;
import com.czy.smartmedicine.utils.ViewModelUtil;

import java.util.List;
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

    public PostAAo postAAo = new PostAAo();

    public void init(PostAAo postAAo, FragmentActivity activity) {
        this.postAAo = postAAo;
        initialNetworkRequest(activity);
    }

    //---------------------------NetWork---------------------------

    // 初始化网络请求
    private void initialNetworkRequest(FragmentActivity activity) {
        // 利用postId去网络请求帖子信息（先请求1页的评论内容）
        NetworkLoadUtils.showDialog(activity);
        // 获取帖子信息；初始化请求分页为1
        getSinglePost(1, activity, new SyncRequestCallback() {
            @Override
            public void onThrowable(Throwable throwable) {
                NetworkLoadUtils.dismissDialogSafe(activity);
            }

            @Override
            public void onAllRequestSuccess() {
                NetworkLoadUtils.dismissDialogSafe(activity);
            }
        });
    }

    public void getSinglePost(Integer pageNum, Context context, SyncRequestCallback callback){
        Long postId = Optional.ofNullable(postAAo)
                        .map(aao -> aao.postId)
                        .orElse(null);

        if (postId == null){
            ToastUtils.showToast(context, context.getString(com.czy.appview.R.string.system_error));
            callback.onThrowable(new Throwable("post id is null"));
            return;
        }

        Long userId = Optional.ofNullable(MainApplication.getInstance().getUserLoginInfoAo())
                .map(ao -> ao.userId)
                .orElse(null);

        GetSinglePostRequest request = new GetSinglePostRequest();
        request.postId = postId;
        request.pageNum = pageNum;
        request.userId = userId;

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
        postAAo.postAVo.initByResponse(
                singlePostResponse.postVo,
                singlePostResponse.commentAos
        );
        postAAo.postAVo.commentNumLd.setValue(
                Optional.ofNullable(singlePostResponse.commentAos)
                        .map(List::size)
                        .orElse(0)
        );
        callback.onAllRequestSuccess();
    }
}
