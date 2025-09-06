package com.czy.smartmedicine.viewModel.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.czy.appcore.BaseConfig;
import com.czy.appcore.network.api.handle.SyncRequestCallback;
import com.czy.appcore.network.netty.api.send.SocketMessageSender;
import com.czy.baseutil.file.FileUtil;
import com.czy.baseutil.image.ImageManager;
import com.czy.baseutil.network.BaseResponse;
import com.czy.baseutil.photo.SelectPhotoUtil;
import com.czy.baseutil.ui.ToastUtils;
import com.czy.dao.networkRepository.ApiRequestImpl;
import com.czy.domain.dto.http.request.PostPublishRequest;
import com.czy.domain.dto.http.response.PostPublishResponse;
import com.czy.domain.fragmentActivityAo.post.PublishPostAAo;
import com.czy.domain.vo.entity.home.PostVo;
import com.czy.smartmedicine.MainApplication;
import com.czy.smartmedicine.utils.ResponseTool;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import okhttp3.MultipartBody;

public class PublishPostVm extends ViewModel {

    private static final String TAG = PublishPostVm.class.getName();

    private final ApiRequestImpl apiRequestImpl;
    private final SocketMessageSender socketMessageSender;

    public PublishPostVm(ApiRequestImpl apiRequestImpl, SocketMessageSender socketMessageSender) {
        this.apiRequestImpl = apiRequestImpl;
        this.socketMessageSender = socketMessageSender;
    }

    //---------------------------Vo Ld---------------------------

    public PublishPostAAo aao = new PublishPostAAo();

    public void init(PublishPostAAo aao) {
        this.aao = aao;

        initialNetworkRequest();
    }

    //---------------------------NetWork---------------------------

    // 初始化网络请求
    private void initialNetworkRequest() {
    }

    public void doPostPublishFirst(Context context, SyncRequestCallback callback){
        String title = Optional.ofNullable(aao)
                .map(vo -> vo.postTitleLd)
                .map(LiveData::getValue)
                .orElse("");

        String content = Optional.ofNullable(aao)
                .map(vo -> vo.postContentLd)
                .map(LiveData::getValue)
                .orElse("");

        // 参数校验
        if (title.isEmpty() || content.isEmpty()){
            String errorMessage = context.getString(com.czy.appview.R.string.publish_post_title_or_content_not_empty);
            ToastUtils.showToastActivity(context, errorMessage);
            callback.onThrowable(new Throwable(errorMessage));
            return;
        }

        boolean isHaveFile = Optional.ofNullable(aao.imageUriArList.get(0))
                .map(Objects::nonNull)
                .orElse(false);

        Log.i(TAG, "isHaveFile1: " + isHaveFile + "\n" + "uri: " + aao.imageUriArList.get(0).get());

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

            Log.i(TAG, "isHaveFile2: " + isHaveFile);

            PostPublishResponse postPublishResponse = response.getData();
            Long postId = postPublishResponse.snowflakeId;
            if (postId != null){
                //没有文件的情况
                if (!isHaveFile){
                    ToastUtils.showToastActivity(
                            context,
                            context.getString(com.czy.appview.R.string.publish_post_success)
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

            // 获取图片
            List<Uri> uris = aao.imageUriArList.stream()
                            .map(AtomicReference::get)
                            .collect(Collectors.toList());

            List<MultipartBody.Part> parts = FileUtil.getMultipartBodyByUri(context, uris);
            apiRequestImpl.uploadPostFile(
                    parts,
                    postId,
                    userId,
                    response ->
                            ResponseTool.handleSyncResponseEx(
                            response,
                            context,
                            callback,
                            this::handleUploadPostFile
                    ),
                    callback::onThrowable
            );
        } catch (Exception e){
            callback.onThrowable(e);
        }
    }

    private void handleUploadPostFile(BaseResponse<PostVo> response, Context context, SyncRequestCallback callback){
        ToastUtils.showToastActivity(context, response.getMessage());
        // 完成
        callback.onAllRequestSuccess();
    }

    //---------------------------logic---------------------------

    public final List<ActivityResultLauncher<Intent>> selectImageLaunchers = new ArrayList<>(BaseConfig.MAX_POST_IMAGE_COUNT);
    private final ImageManager imageManager = new ImageManager();
    public void initSelectImageLaunchers(
            @NonNull ConstraintLayout[] layouts,
            @NonNull ImageView[] imageViews,
            @NonNull View[] addViews,
            AppCompatActivity activity){
        for (int i = 0; i < imageViews.length; i++){
            int finalI = i;
            ActivityResultLauncher<Intent> launcher = SelectPhotoUtil.initActivityResultLauncher(
                    activity,
                    imageViews[i],
                    aao.imageUriArList.get(i),
                    imageManager,
                    () -> {
                        // 每次重新获取当前图片数量, 保证时效性
                        int imageCount = aao.getImageCount();
                        // 设置所有布局和视图的可见性
                        for (int j = 0; j < layouts.length; j++) {
                            Log.i(TAG, "handleImageCountChange: " + imageCount);
                            if (j <= imageCount) {
                                layouts[j].setVisibility(View.VISIBLE);
                                imageViews[j].setVisibility(View.VISIBLE);
                                addViews[j].setVisibility(j <= imageCount - 1 ? View.GONE : View.VISIBLE);
                            } else {
                                layouts[j].setVisibility(View.GONE);
                                imageViews[j].setVisibility(View.GONE);
                                addViews[j].setVisibility(View.GONE);
                            }
                        }
                    }
            );
            selectImageLaunchers.add(launcher);
        }
    }
}
