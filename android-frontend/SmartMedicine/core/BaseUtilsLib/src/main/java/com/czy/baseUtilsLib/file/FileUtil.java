package com.czy.baseUtilsLib.file;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.loader.content.CursorLoader;


import com.bumptech.glide.Glide;
import com.czy.baseUtilsLib.debug.DebugMyUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;


public class FileUtil {


    /**
     * 删除文件
     * @param filePath  文件路径
     */
    public static void deleteFile(String[] filePath){
        File file;
        file = new File(filePath[0]);
        if(file.exists()){
            boolean deleteResult = file.delete();
            DebugMyUtil.logR("deleteResult:" + deleteResult);
        }
        // 通过指针重置内容
        filePath[0] = "";
    }

    protected static final String TAG = FileUtil.class.getSimpleName();

    /**
     * okhttp3响应体 -> 保存文件到当前环境路径
     * @param response  okhttp3响应体
     * @param context   上下文，用于显示Toast
     * @return  保存是否成功
     */
    public static boolean saveFileByEnvironment(ResponseBody response, Context context){
        try {
            // 获取文件保存路径
            File file = new File(Environment.getExternalStorageDirectory(), "fileName");

            // 写入文件
            InputStream inputStream = response.byteStream();
            OutputStream outputStream = Files.newOutputStream(file.toPath());

            byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }

            outputStream.flush();
            outputStream.close();
            inputStream.close();

            // 提示文件保存成功
            Toast.makeText(context, "文件已保存至: " + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
            return true;
        }catch (IOException e){
            Toast.makeText(context, "文件保存失败", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    /**
     * 保存到相册
     * @param response      okhttp3响应体
     * @param context       上下文，用于显示Toast
     * @param galleryPath   相册保存路径
     * @return  保存是否成功
     */
    // 保存到相册 String galleryPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath();
    public static boolean saveFileToGallery(ResponseBody response, Context context, String galleryPath) {
        try {
            // 获取相册保存路径
            File file = new File(galleryPath, "fileName");

            // 写入文件
            InputStream inputStream = response.byteStream();
            OutputStream outputStream = Files.newOutputStream(file.toPath());

            byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }

            outputStream.flush();
            outputStream.close();
            inputStream.close();

            // 通知系统更新相册
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            // 提示文件保存成功
            Toast.makeText(context, "文件已保存至相册: " + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
            return true;
        } catch (IOException e) {
            Toast.makeText(context, "文件保存失败", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    /**
     * 判断文件是否为图片文件
     * @param file  文件
     * @return  true为图片文件，false为非图片文件
     */
    private static boolean isImageFile(File file) {
        String mimeType = URLConnection.guessContentTypeFromName(file.getName());
        return mimeType != null && mimeType.startsWith("image/");
    }

    /**
     * 创建自定义文件夹
     * @param activity  Activity
     * @return  自定义文件夹路径
     */
    public static String createDirectory(Activity activity){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Android 11 及以上版本
            if (Environment.isExternalStorageManager()) {
                // 已经获得全部存储访问权限
                String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
                Log.i(TAG,"Android 11 及以上版本");
                createDirectory(path);
                return path;
            } else {
                // 请求全部存储访问权限
                requestAllStoragePermission(activity);
                return null;
            }
        } else {
            // Android 10 及以下版本
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Test";
            Log.i(TAG,"Android 10 及以下版本");
            createDirectory(path);
            return path;
        }
    }

    /**
     * 创建自定义文件夹
     * @param directoryPath 文件夹路径
     */
    private static void createDirectory(String directoryPath) {
        File downloadDir = new File(directoryPath);
        if (!downloadDir.exists()) {
            boolean r = downloadDir.mkdirs();
            Log.d(TAG, "文件夹:"+ directoryPath + "创建情况：" + r);
        }
    }

    private static final int REQUEST_CODE_STORAGE_PERMISSION = 302;


    // TODO 待完善文件存储权限获取
    @RequiresApi(api = Build.VERSION_CODES.R)
    private static void requestAllStoragePermission(Activity activity) {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.MANAGE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.MANAGE_EXTERNAL_STORAGE}, REQUEST_CODE_STORAGE_PERMISSION);
        }
    }

    /**
     * 用File创建MultipartBody.Part
     * @param file  文件
     * @return MultipartBody.Part
     */
    public static MultipartBody.Part createMultipartBodyPart(File file, @NonNull String fileRequestParamName) {
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        return MultipartBody.Part.createFormData(fileRequestParamName, file.getName(), requestFile);
    }

    /**
     * 从ContentResolver中获取文件路径
     * @param selectedVideoUri  选择文件的Uri
     * @param contentResolver   资源处理者
     * @return  文件路径
     */
    public static String getFilePathFromContentUri(Uri selectedVideoUri, ContentResolver contentResolver){
        String filePath;
        String[] filePathColumn = {MediaStore.MediaColumns.DATA};
        Cursor cursor = contentResolver.query(selectedVideoUri, filePathColumn, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            filePath = cursor.getString(columnIndex);
            cursor.close();
        } else {
            filePath = null;
        }
        return filePath;
    }


    /**
     * 通过File获取图片的Uri
     * @param context   上下文
     * @param imageFile 图片文件
     * @return  图片的Uri
     */
    @SuppressLint("Range")
    public static Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        try (Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID},
                MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath},
                null
        )) {

            if (cursor != null && cursor.moveToFirst()) {
                int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
                Uri baseUri = Uri.parse("content://media/external/images/media");
                return Uri.withAppendedPath(baseUri, "" + id);
            } else {
                if (imageFile.exists()) {
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.DATA, filePath);
                    return context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                } else {
                    return null;
                }
            }
        }
    }

    /**
     * 将 Bitmap 数据保存到自定义文件夹中
     * @param bitmap 要保存的 Bitmap 数据
     * @param fileName 文件名
     * @param file_storage_path 文件存储路径
     * @return 保存文件的完整路径
     */
    public static String saveBitmapToCustomFolder(Bitmap bitmap, String fileName, String file_storage_path) {
        File storageDir = createCustomFolder(file_storage_path);
        if (storageDir != null) {
            File file = new File(storageDir, fileName);
            try {
                FileOutputStream out = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.flush();
                out.close();
                Log.i(TAG, "存储成功，存储路径："+file.getPath());
                return file.getAbsolutePath();
            } catch (IOException e) {
                Log.e(TAG, "Error saving bitmap: " + e.getMessage());
            }
        }
        return null;
    }

    /**
     * 创建自定义文件夹
     * @param file_storage_path 文件存储路径
     * @return 创建的文件夹对象
     */
    public static File createCustomFolder(String file_storage_path) {
        File storageDir = null;

        // 检查是否有外部存储权限
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            storageDir = new File(file_storage_path);
            if (!storageDir.exists()) {
                if (!storageDir.mkdirs()) {
                    Log.d(TAG, "Failed to create directory");
                    storageDir = null;
                }
            }
        } else {
            Log.d(TAG, "External storage is not mounted.");
        }

        if(storageDir != null){
            Log.d(TAG, "storageDir："+storageDir.getPath());
        }

        return storageDir;
    }


    /**
     * 生成Okhttp3的RequestBody，用于上传图片
     * @param file              文件
     * @param fileParamName         文件参数名
     * @param parameterMap      参数Map
     * @return                  包含图片数据的RequestBody
     */
    public static RequestBody getUploadRequestBody(File file, String fileParamName, Map<String, String> parameterMap) {
        if (file == null){
            Log.e(TAG, "File is null");
            return null;
        }

        // 创建一个MultipartBody.Builder
        MultipartBody.Builder builder = new MultipartBody.Builder()
                // 设置请求类型为表单类型(FORM-DATA)
                .setType(MultipartBody.FORM);

        // 添加文件部分
        if (file.exists()) {
            RequestBody fileBody = RequestBody.create(MediaType.parse("image/jpeg"), file); // 根据文件类型设置MediaType
            builder.addFormDataPart(fileParamName, file.getName(), fileBody); // "file" 是服务器接收文件的字段名
        }

        // 添加其他参数
        if (parameterMap != null) {
            for (Map.Entry<String, String> entry : parameterMap.entrySet()) {
                builder.addFormDataPart(entry.getKey(), entry.getValue());
            }
        }

        // 构建并返回RequestBody
        return builder.build();
    }

    /**
     * 使用Glide加载图片并生成OkHttp3的RequestBody，用于上传
     * @param context           上下文
     * @param imageFile         图片文件
     * @param parameterMap      参数Map
     * @param fileParamName     文件参数名
     * @return                  包含图片数据的RequestBody
     */
    public static RequestBody getUploadRequestBody(Context context, File imageFile, Map<String, String> parameterMap, String fileParamName) {
        if (imageFile == null){
            Log.e(TAG, "imageFile is null");
            return null;
        }

        try {
            // 使用Glide加载文件
            File file = Glide.with(context)
                    .asFile()
                    .load(imageFile)
                    .submit()
                    .get();

            // 创建MultipartBody.Builder
            MultipartBody.Builder builder = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM); // 设置请求类型为表单类型

            // 添加文件部分
            if (file != null && file.exists()) {
                RequestBody fileBody = RequestBody.create(MediaType.parse("image/jpeg"), file); // 根据实际文件类型设置MediaType
                builder.addFormDataPart(fileParamName, file.getName(), fileBody); // 添加文件到请求体
            }

            // 添加其他参数
            if (parameterMap != null) {
                for (Map.Entry<String, String> entry : parameterMap.entrySet()) {
                    builder.addFormDataPart(entry.getKey(), entry.getValue());
                }
            }

            // 构建并返回RequestBody
            return builder.build();
        } catch (Exception e) {
            Log.e(TAG, "Glide加载上传图片异常 : " , e);
            return null; // 处理异常，返回null
        }
    }

    public static List<MultipartBody.Part> getMultipartBodyByUri(Context context, List<Uri> uris){
        List<MultipartBody.Part> parts = new ArrayList<>();
        for (Uri uri : uris) {
            File file = new File(getRealPathFromURI(context, uri));
            RequestBody requestFile = RequestBody.create(
                    MediaType.parse("multipart/form-data"),
                    file
            );
            MultipartBody.Part body = MultipartBody.Part.createFormData(
                    "files", file.getName(), requestFile
            );
            parts.add(body);
        }
        return parts;
    }

    // 辅助方法：获取文件路径
// 辅助方法：获取文件路径
    private static String getRealPathFromURI(Context context, Uri contentUri) {
        String[] proj;

        if (contentUri.toString().startsWith("content://media/external/images")) {
            proj = new String[]{MediaStore.Images.Media.DATA};
        } else if (contentUri.toString().startsWith("content://media/external/video")) {
            proj = new String[]{MediaStore.Video.Media.DATA};
        } else if (contentUri.toString().startsWith("content://media/external/audio")) {
            proj = new String[]{MediaStore.Audio.Media.DATA};
        } else {
            return null; // 不支持的类型
        }

        CursorLoader loader = new CursorLoader(context, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(proj[0]);
            cursor.moveToFirst();
            String path = cursor.getString(column_index);
            cursor.close(); // 关闭游标
            return path;
        }
        return null;
    }

}
