package com.czy.smartmedicine.viewModel.activity;

import android.content.Context;
import android.net.Uri;

import androidx.lifecycle.ViewModel;

import com.czy.appcore.network.netty.api.send.SocketMessageSender;
import com.czy.baseUtilsLib.file.FileUtil;
import com.czy.baseUtilsLib.network.BaseResponse;
import com.czy.baseUtilsLib.ui.ToastUtils;
import com.czy.dal.dto.http.request.PostPublishRequest;
import com.czy.dal.dto.http.response.PostPublishResponse;
import com.czy.dal.vo.fragmentActivity.post.PublishPostVo;
import com.czy.datalib.networkRepository.ApiRequestImpl;
import com.czy.smartmedicine.MainApplication;
import com.czy.smartmedicine.utils.ViewModelUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import okhttp3.MultipartBody;

public class PublishViewModel extends ViewModel {

    private static final String TAG = PublishViewModel.class.getName();

    private final ApiRequestImpl apiRequestImpl;
    private final SocketMessageSender socketMessageSender;

    public PublishViewModel(ApiRequestImpl apiRequestImpl, SocketMessageSender socketMessageSender) {
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

    public void doPostPublishFirst(String title, String content, boolean isHaveFile, Context context){
        PostPublishRequest request = new PostPublishRequest();
        request.title = title;
        request.content = content;
        request.isHaveFiles = isHaveFile;
        request.setSuperRequest(MainApplication.getInstance().getBaseNettyRequest());
        apiRequestImpl.postPublishFirst(
                request,
                response -> {
                    handlePostPublishFirstResponse(response, context, isHaveFile);
                },
                ViewModelUtil::globalThrowableToast
                );
    }

    private void handlePostPublishFirstResponse(BaseResponse<PostPublishResponse> response, Context context, boolean isHaveFile) {
        if (ViewModelUtil.handleResponse(response)) {
            PostPublishResponse postPublishResponse = response.getData();
            Long postId = postPublishResponse.snowflakeId;
            if (postId != null){
                //没有文件的情况
                if (!isHaveFile){
                    ToastUtils.showToastActivity(
                            context,
                            context.getString(com.czy.customviewlib.R.string.publish_post_success)
                    );
                    return;
                }
                // 存在文件
                // 第二次调用http上传请求
                doUploadPostFile(postId, context);
            }
            else {
                // 审核不通过
                ToastUtils.showToastActivity(context, response.getMessage());
            }
        }
    }

    private void doUploadPostFile(Long postId, Context context){
        String userAccount = MainApplication.getInstance().getUserLoginInfoAo().account;
        List<Uri> uris = new ArrayList<>();
        uris.add(selectImageUriAtomic.get());
        List<MultipartBody.Part> parts = FileUtil.getMultipartBodyByUri(context, uris);
        apiRequestImpl.uploadPostFile(
                parts,
                postId,
                userAccount,
                response -> {
                    ToastUtils.showToastActivity(context, response.getMessage());
                },
                ViewModelUtil::globalThrowableToast
                );
    }
}
