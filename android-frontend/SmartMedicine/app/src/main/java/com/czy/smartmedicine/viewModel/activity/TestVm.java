package com.czy.smartmedicine.viewModel.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.czy.appcore.network.netty.api.send.SocketMessageSender;
import com.czy.baseUtilLib.ui.ToastUtils;
import com.czy.dao.networkRepository.ApiRequestImpl;
import com.czy.smartmedicine.MainApplication;

import java.io.File;
import java.util.concurrent.atomic.AtomicReference;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class TestVm extends ViewModel {

    private static final String TAG = TestVm.class.getSimpleName();

    private final ApiRequestImpl apiRequestImpl;
    private final SocketMessageSender socketMessageSender;

    public TestVm(ApiRequestImpl apiRequestImpl, SocketMessageSender socketMessageSender){
        this.apiRequestImpl = apiRequestImpl;
        this.socketMessageSender = socketMessageSender;
    }

    public AtomicReference<Uri> uriAtomicReference = new AtomicReference<>(null);

    // uploadImageTest
    public void uploadImageTest(Context context){
        Bitmap bitmap = MainApplication.getInstance().getImageManager().uriToBitmapMediaStore(context, this.uriAtomicReference.get());
        File imageFile = MainApplication.getInstance().getImageManager().bitmapToFile(bitmap, uriAtomicReference.get(), context);;
        if (imageFile == null || !imageFile.exists()) {
            // 处理文件未创建或路径不正确的情况
            Log.e(TAG, "Image file creation failed");
            return;
        }
        // 获取文件名
        String originalFilename = imageFile.getName(); // 使用 getName() 获取文件名
        // 获取文件扩展名
        String fileExtension = originalFilename.contains(".") ?
                originalFilename.substring(originalFilename.lastIndexOf(".")) : ""; // 获取扩展名
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), imageFile);
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", imageFile.getName(), requestFile);

        apiRequestImpl.uploadImageTest(
                filePart,
                response -> {
                    ToastUtils.showToastActivity(context, response.getMessage());
                },
                throwable -> {

                }
        );
    }
}
