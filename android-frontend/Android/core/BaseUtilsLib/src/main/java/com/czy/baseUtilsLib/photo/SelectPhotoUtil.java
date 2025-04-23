package com.czy.baseUtilsLib.photo;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;


import com.czy.baseUtilsLib.image.ImageManager;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Content Provider选择照片，获取Uri，通过Uri转化为Bitmap（ImageUtils）
 */
public class SelectPhotoUtil {

    public static final String TAG = SelectPhotoUtil.class.getSimpleName();

    /**
     * 从相册选择图像。
     * @param selectImageLauncher    用于启动图像选择的 ActivityResultLauncher。
     */
    @SuppressLint("IntentReset")
    public static void selectImageFromAlbum(ActivityResultLauncher<Intent> selectImageLauncher){
        try {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            selectImageLauncher.launch(intent);
        } catch (Exception e){
            Log.e(TAG, "打开相册异常：" + e.getMessage(), e);
        }
    }

    /**
     * 初始化用于从相册选择图像的 ActivityResultLauncher。
     * @param activity                  AppCompatActivity 上下文
     * @param imageView                 ImageView。
     * @param selectedImageUri          用于保存选中图像 URI 的 AtomicReference。[如果知识传递Uri的话，只是传递形参，并不是地址，无法改变Activity中Uri的值]
     *                                  <p> 此处的uri是null，用于保存从相册中获取的uri <p/>
     * @param imageManager                 用于将 URI 转换为 Bitmap 以便显示的 ImageUtil 实例。
     * @return 返回一个 ActivityResultLauncher<Intent>，可用于启动图像选择的 Intent。
     */
    public static ActivityResultLauncher<Intent> initActivityResultLauncher(AppCompatActivity activity, ImageView imageView, AtomicReference<Uri> selectedImageUri, ImageManager imageManager) {
        return activity.registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> handleImageResult(result, activity, imageView, selectedImageUri, imageManager));
    }

    /**
     * 处理图像选择活动的结果。
     * @param result            图像选择活动的结果，包括结果码和数据。
     * @param activity          AppCompatActivity 上下文。
     * @param imageView         ImageView。
     * @param selectedImageUri  用于保存选中图像 URI 的 AtomicReference。[如果知识传递Uri的话，只是传递形参，并不是地址，无法改变Activity中Uri的值]
     * @param imageManager         用于将 URI 转换为 Bitmap 以便显示的 ImageUtil 实例。
     */
    private static void handleImageResult(ActivityResult result, Activity activity, ImageView imageView, AtomicReference<Uri> selectedImageUri, ImageManager imageManager) {
        // if (result.getResultCode() == Activity.RESULT_OK){};
        Intent data = result.getData();
        if (data != null) {
            Uri imageUri = data.getData();
            // 更新 Uri
            selectedImageUri.set(imageUri);
            Bitmap bitmap = imageManager.uriToBitmapMediaStore(activity, imageUri);
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
            }
        }
    }

    public static int SELECT_IMAGE_CODE = 211;

    @SuppressLint("IntentReset")
    public static void selectImageFromAlbum_old(Activity activity){
        try {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            activity.startActivityForResult(intent,SELECT_IMAGE_CODE);
        }catch (Exception e){
            Log.e(TAG, "打开相册异常：" + e.getMessage(), e);
        }
    }

    public static Uri handleImageResult_old(int requestCode, int resultCode, Intent data,Activity activity, ImageView imageView, ImageManager imageManager){
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == SELECT_IMAGE_CODE){
                if(data != null){
                    Uri imagUri = data.getData();
                    Bitmap bitmap = imageManager.uriToBitmapMediaStore(activity, imagUri);
                    if(bitmap != null){
                        imageView.setImageBitmap(bitmap);
                        return imagUri;
                    }
                }
            }
        }
        return null;
    }

}


