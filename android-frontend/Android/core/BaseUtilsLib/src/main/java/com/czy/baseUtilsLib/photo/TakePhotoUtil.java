package com.czy.baseUtilsLib.photo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.czy.baseUtilsLib.R;
import com.czy.baseUtilsLib.image.ImageManager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicReference;

/**
 *  拍照工具：
 *      1.拍照之后图片通过uri读取到imageview
 *      2.拍照之后图片保存到相册文件夹下
 *      3.拍照之后，图片通过ImageUtils转化为Bitmap缓存在内存（压缩上传到Spring Boot）
 */
// TODO 图片拍照之后压缩
public class TakePhotoUtil {
    private static final String TAG = TakePhotoUtil.class.getSimpleName();

    protected File mImageFile;
    protected Uri mImageUri;

    public File getmImageFile() {
        return mImageFile;
    }
    public Uri getmImageUri(){
        return mImageUri;
    }

    // 新版

    private CameraDevice cameraDevice;

    private CameraDevice.StateCallback initStateCallback(){

        return new CameraDevice.StateCallback() {
            @Override
            public void onOpened(@NonNull CameraDevice camera) {
                cameraDevice = camera;
                Log.i(TAG, "相机已打开: " + camera.getId());
                // 在这里可以开始相机预览或其他操作
            }

            @Override
            public void onDisconnected(@NonNull CameraDevice camera) {
                camera.close();
                cameraDevice = null;
                Log.i(TAG, "相机已断开连接: " + camera.getId());
            }

            @Override
            public void onError(@NonNull CameraDevice camera, int error) {
                camera.close();
                cameraDevice = null;
                Log.e(TAG, "相机发生错误: " + error);
            }
        };
    };

    /**
     * 创建用来存储图片的文件，以时间来命名就不会产生命名冲突
     *
     * @return 创建的图片文件
     */
    @SuppressLint("SimpleDateFormat")
    private static File createImageFile(Activity activity) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = null;
        try {
            imageFile = File.createTempFile(imageFileName, ".jpg", storageDir);
        } catch (IOException e) {
            Log.e(TAG, "创建临时文件失败: " + e.getMessage(), e);
        }
        return imageFile;
    }

    @SuppressLint("SimpleDateFormat")
    private void createImageFileV2(AppCompatActivity activity) {
        // 创建一个用于保存图像的文件
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        try {
            mImageFile = File.createTempFile(imageFileName, ".jpg", storageDir);
            String authority = activity.getApplicationInfo().packageName + ".fileprovider";
            mImageUri = FileProvider.getUriForFile(activity, authority, mImageFile);
        } catch (IOException e) {
            Log.e(TAG, "创建图像文件失败: " + e.getMessage(), e);
        }
    }

    private String getFrontCameraId(AppCompatActivity activity) {
        String frontCameraId = null;
        CameraManager manager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
        try {
            for (String id : manager.getCameraIdList()) {
                CameraCharacteristics characteristics = manager.getCameraCharacteristics(id);
                Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
                if (facing != null && facing == CameraCharacteristics.LENS_FACING_FRONT) {
                    frontCameraId = id;
                    break;
                }
            }
        } catch (CameraAccessException e) {
            Log.e(TAG, "获取摄像头信息时发生异常:" + e.getMessage(), e);
        }
        return frontCameraId;
    }

    private void openCamera(AppCompatActivity activity) {
        String cameraId = getFrontCameraId(activity);
        if (cameraId != null) {
            CameraManager manager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
            try {
                CameraDevice.StateCallback stateCallback = initStateCallback();
                manager.openCamera(cameraId, stateCallback, null);
            } catch (CameraAccessException e) {
                Log.e(TAG, "相机访问异常，摄像头 ID: " + cameraId, e);
            } catch (SecurityException e) {
                Log.e(TAG, "相机权限被拒绝。", e);
            }
        } else {
            Log.e(TAG, "未找到前置摄像头。");
        }
    }
    public void takePhoto_new(AppCompatActivity activity) {
        // 检查相机权限
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, 1);
        }
        else {
            // 创建图像文件
            createImageFileV2(activity);
            // 打开前置相机
            openCamera(activity);
        }
    }

    public void releaseCamera() {
        if (cameraDevice != null) {
            cameraDevice.close();
            cameraDevice = null;
        }
    }

    // 旧版

    public static final int REQUEST_PHOTO_CODE = 985;

    /**
     * 拍照old
     * @param activity  activity
     * @param code      请求码
     */
    @SuppressLint("QueryPermissionsNeeded")
    public void takePhoto_old(Activity activity, int code){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //这句作用是如果没有相机则该应用不会闪退，要是不加这句则当系统没有相机应用的时候该应用会闪退
        if (intent.resolveActivity(activity.getPackageManager()) != null){
            //创建用来保存照片的文件
            mImageFile = createImageFile(activity);
            if(mImageFile != null){
                //7.0以上要通过FileProvider将File转化为Uri
                String authority = activity.getApplicationInfo().packageName + ".fileprovider";
                // FileProvider需要在Manifest种申明
                mImageUri = FileProvider.getUriForFile(activity, authority, mImageFile);
                Log.d("Runtime","mImageUri:" + mImageUri);
                //将用于输出的文件Uri传递给相机
                intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
                if (code == REQUEST_PHOTO_CODE) {
                    intent.putExtra("android.intent.extras.CAMERA_FACING", Camera.CameraInfo.CAMERA_FACING_FRONT);
                    intent.putExtra("android.intent.extra.USE_FRONT_CAMERA", true);
                }
                //打开相机
                activity.startActivityForResult(intent, code);
            }
        }
    }

    /**
     * 拍照old V2
     * （优化：剥离工具类的uri，不使用startActivityForResult和code作为响应调用）
     * @param activity      activity
     * @param isFrontCamera 是否前置摄像头
     * @param launcher      拍照ResultLauncher
     * @param imageUriRef   用于接收拍照结果的Uri
     */
    public static void takePhotoV2_old(Activity activity, boolean isFrontCamera,
                                       ActivityResultLauncher<Intent> launcher,
                                       AtomicReference<Uri> imageUriRef){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // 确保设备上有可用的相机
        if (intent.resolveActivity(activity.getPackageManager()) != null) {
            // 创建用来保存照片的文件
            File mImageFile = createImageFile(activity);
            if (mImageFile != null) {
                // 7.0及以上版本通过FileProvider获取Uri
                String authority = activity.getApplicationInfo().packageName + ".fileprovider";

                // 将URI存入AtomicReference
                imageUriRef.set(FileProvider.getUriForFile(activity, authority, mImageFile));

                // 将用于输出的文件Uri传递给相机
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUriRef.get());
                // 根据boolean值设置摄像头
                if (isFrontCamera) {
                    intent.putExtra("android.intent.extras.CAMERA_FACING", Camera.CameraInfo.CAMERA_FACING_FRONT);
                    intent.putExtra("android.intent.extra.USE_FRONT_CAMERA", true);
                } else {
                    intent.putExtra("android.intent.extras.CAMERA_FACING", Camera.CameraInfo.CAMERA_FACING_BACK);
                    intent.putExtra("android.intent.extra.USE_FRONT_CAMERA", false);
                }
                // 使用ActivityResultLauncher启动相机
                launcher.launch(intent);
            }
            else {
                Log.w(TAG, "保存数据的文件为空");
            }
        }
    }

    /**
     * 初始化用于从相册选择图像的 ActivityResultLauncher。
     * @param activity                  AppCompatActivity 上下文
     * @param imageView                 ImageView。
     * @param selectedImageUri          用于保存选中图像 URI 的 AtomicReference。[如果知识传递Uri的话，只是传递形参，并不是地址，无法改变Activity中Uri的值]
     * @param imageManager                 用于将 URI 转换为 Bitmap 以便显示的 ImageUtil 实例。
     * @return 返回一个 ActivityResultLauncher<Intent>，可用于启动图像选择的 Intent。
     */
    public static ActivityResultLauncher<Intent> getTakePhotoActivityResultLauncher(AppCompatActivity activity, ImageView imageView, AtomicReference<Uri> selectedImageUri, ImageManager imageManager){
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
     *                          <p> 此处的url、i是拍照之后设置的值，该函数中的作用是将uri的值设置到image中 <p/>
     * @param imageManager         用于将 URI 转换为 Bitmap 以便显示的 ImageUtil 实例。
     */
    private static void handleImageResult(ActivityResult result, Activity activity, ImageView imageView, AtomicReference<Uri> selectedImageUri, ImageManager imageManager){
//        Intent data = result.getData();
//        Log.i("Runtime", "data:" + data);
//        Uri uri = data == null ? null : data.getData();
//        Log.i("Runtime", "uri:" + uri);
        Log.i("Runtime", "result.code" + result.getResultCode());
        Log.i("Runtime", "selectedImageUri.get():" + selectedImageUri.get());
        // 更新 Uri
        Bitmap bitmap = imageManager.uriToBitmapMediaStore(activity, selectedImageUri.get());
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        }
    }

    public Uri handleResultAndLoad_old(int requestCode, int resultCode, Activity activity, ImageView imageView){
        if(resultCode == Activity.RESULT_OK){
            if (requestCode == REQUEST_PHOTO_CODE){
                Uri imageUri = mImageUri;
                try {
                    // 使用 InputStream 读取图片
                    InputStream inputStream = activity.getContentResolver().openInputStream(imageUri);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    imageView.setImageBitmap(bitmap);
                    return imageUri;
                } catch (IOException e) {
                    imageView.setImageResource(R.drawable.icon_default_acatar);
                    Log.e(TAG, "加载图片时发生异常: " + e.getMessage(), e);
                }
            }
        }
        return null;
    }
}
