package com.czy.smartmedicine.viewModel.activity;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.czy.appcore.network.api.handle.SyncRequestCallback;
import com.czy.appcore.network.netty.api.send.SocketMessageSender;
import com.czy.baseUtilsLib.file.FileUtil;
import com.czy.baseUtilsLib.network.BaseResponse;
import com.czy.baseUtilsLib.ui.ToastUtils;
import com.czy.dal.dto.http.request.PostPublishRequest;
import com.czy.dal.dto.http.response.PostPublishResponse;
import com.czy.dal.fragmentActivityAo.post.PublishPostVo;
import com.czy.datalib.networkRepository.ApiRequestImpl;
import com.czy.smartmedicine.MainApplication;
import com.czy.smartmedicine.utils.ResponseTool;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import okhttp3.MultipartBody;

public class PublishPostVm extends ViewModel {

    private static final String TAG = PublishPostVm.class.getName();

    private final ApiRequestImpl apiRequestImpl;
    private final SocketMessageSender socketMessageSender;

    public PublishPostVm(ApiRequestImpl apiRequestImpl, SocketMessageSender socketMessageSender) {
        this.apiRequestImpl = apiRequestImpl;
        this.socketMessageSender = socketMessageSender;
    }

    public AtomicReference<Uri> selectImageUriAtomic = new AtomicReference<>(null);

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

    public void doPostPublishFirst(boolean isHaveFile, Context context, SyncRequestCallback callback){
        String title = Optional.ofNullable(publishPostVo)
                .map(vo -> vo.postTitleLd)
                .map(LiveData::getValue)
                .orElse("");

        String content = Optional.ofNullable(publishPostVo)
                .map(vo -> vo.postContentLd)
                .map(LiveData::getValue)
                .orElse("");

        // 参数校验
        if (title.isEmpty() || content.isEmpty()){
            String errorMessage = context.getString(com.czy.customviewlib.R.string.publish_post_title_or_content_not_empty);
            ToastUtils.showToastActivity(context, errorMessage);
            callback.onThrowable(new Throwable(errorMessage));
            return;
        }

        PostPublishRequest request = new PostPublishRequest();
        request.title = title;
        request.content = content;
        request.isHaveFiles = isHaveFile;
        request.setSuperRequest(MainApplication.getInstance().getBaseNettyRequest());
        Boolean param = isHaveFile;
        apiRequestImpl.postPublishFirst(
                    request,
                    response ->
                            // 审核不通过则显示错误信息
                            ResponseTool.handleSyncResponseEx(
                            response,
                            context,
                            callback,
                            param,
                            this::handlePostPublishFirstResponse
                    ),
                    callback::onThrowable
                );
    }

    private void handlePostPublishFirstResponse
            (BaseResponse<PostPublishResponse> response, Context context, SyncRequestCallback callback, Object param) {
        try {
            Boolean isHaveFile = (Boolean) param;
            PostPublishResponse postPublishResponse = response.getData();
            Long postId = postPublishResponse.snowflakeId;
            if (postId != null){
                //没有文件的情况
                if (!isHaveFile){
                    ToastUtils.showToastActivity(
                            context,
                            context.getString(com.czy.customviewlib.R.string.publish_post_success)
                    );
                    callback.onAllRequestSuccess();
                    return;
                }
                // 存在文件
                // 第二次调用http上传请求
                doUploadPostFile(postId, context, callback);
            }
            else {
                callback.onThrowable(new Throwable("postId != null"));
            }
        } catch (Exception e){
            Log.e(TAG, "handlePostPublishFirstResponse error " , e);
            callback.onThrowable(e);
        }
    }

    private void doUploadPostFile(Long postId, Context context, SyncRequestCallback callback){
        try {
            Long userId = MainApplication.getInstance().getUserLoginInfoAo().userId;

            List<Uri> uris = new ArrayList<>();
            uris.add(selectImageUriAtomic.get());
            List<MultipartBody.Part> parts = FileUtil.getMultipartBodyByUri(context, uris);
            apiRequestImpl.uploadPostFile(
                    parts,
                    postId,
                    userId,
                    response -> {
                        ToastUtils.showToastActivity(context, response.getMessage());
                        // 完成
                        callback.onAllRequestSuccess();
                    },
                    callback::onThrowable
            );
        } catch (Exception e){
            callback.onThrowable(e);
        }
    }
}
