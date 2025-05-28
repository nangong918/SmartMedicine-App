package com.czy.baseUtilsLib.image;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import com.czy.baseUtilsLib.file.FileUtil;
import com.czy.baseUtilsLib.ui.ToastUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.Optional;


// TODO 图片压缩工具
public class ImageManager {

    protected static final String TAG = ImageManager.class.getSimpleName();

    /**
     * 将uri转为Bitmap：BitmapFactory转化
     * @param context 上下文，获取系统的内容处理者（ContentResolver）
     * @param uri 资源路径
     * @return Bitmap
     */
    public Bitmap uriToBitmapBitmapFactory(Context context, Uri uri) {
        ContentResolver resolver = context.getContentResolver();
        InputStream inputStream;
        try {
            inputStream = resolver.openInputStream(uri);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "文件未找到: " + uri, e);
            return null;
        }
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
        if(inputStream != null){
            try {
                inputStream.close();
            } catch (IOException e) {
                Log.e(TAG, "关闭输入流时发生异常", e);
                return null;
            }
        }
        return bitmap;
    }

    /**
     * 将uri转为Bitmap：MediaStore转化
     * @param context 上下文，获取系统的内容处理者（ContentResolver）
     * @param uri 资源路径
     * @return Bitmap
     */
    public Bitmap uriToBitmapMediaStore(Context context, Uri uri){
        ContentResolver resolver = context.getContentResolver();
        InputStream inputStream = null;
        Bitmap bitmap = null;

        try {
            inputStream = resolver.openInputStream(uri);
            bitmap = BitmapFactory.decodeStream(inputStream);
        } catch (IOException e) {
            Log.e(TAG, "从 URI 获取 Bitmap 时发生异常: " + uri, e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    Log.e(TAG, "关闭输入流时发生异常", e);
                }
            }
        }
        return bitmap;
    }

    /**
     * 将Bitmap转化为byte[]数组
     * @param bitmap 位图
     * @return byte[]
     */
    public byte[] bitmapToBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        try {
            stream.close();
        } catch (IOException e) {
            Log.e(TAG, "关闭输入流时发生异常", e);
        }

        return byteArray;
    }

    final float imageSizeLimit = 0.1f;//超过100kb就裁剪

    /**
     * 将Bitmap转化为byte[]数组 + 大小限制
     * @param bitmap 位图
     * @return byte[]
     */
    public byte[] bitmapToBytesLimit(Bitmap bitmap) {
        byte[] imageBytes = bitmapToBytes(bitmap);
        int imageSize = imageBytes.length;
        //超过100kb就裁剪
        boolean isImageSizeExceeded = (imageSize > imageSizeLimit * 1024 * 1024);

        if (isImageSizeExceeded) {
            double scale = Math.sqrt((imageSizeLimit * 1024 * 1024) / (double) imageSize);

            int width = (int) (bitmap.getWidth() * scale);
            int height = (int) (bitmap.getHeight() * scale);
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            scaledBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);

            imageBytes = outputStream.toByteArray();
        }
        return imageBytes;
    }

    /**
     * 将Bitmap转化为byte[]数组 + 大小限制
     * @param bitmap        位图
     * @param limitSize     限制大小（MB）
     * @return byte[]
     */
    public byte[] bitmapToBytesLimit(Bitmap bitmap, int limitSize) {
        byte[] imageBytes = bitmapToBytes(bitmap);
        int imageSize = imageBytes.length;
        boolean isImageSizeExceeded = (imageSize > limitSize * 1024 * 1024);//大小限制

        if (isImageSizeExceeded) {
            double scale = Math.sqrt((limitSize * 1024 * 1024) / (double) imageSize);

            int width = (int) (bitmap.getWidth() * scale);
            int height = (int) (bitmap.getHeight() * scale);
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            scaledBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);

            imageBytes = outputStream.toByteArray();
        }
        return imageBytes;
    }

    /**
     * 将byte[]数组转化为Bitmap
     * @param byteImage byte[]数组
     * @return Bitmap
     */
    public Bitmap bytesToBitmap(byte[] byteImage){
        if(byteImage != null && byteImage.length > 0){
            return BitmapFactory.decodeByteArray(byteImage, 0, byteImage.length);
        }
        else {
            return null;
        }
    }

    /**
     * 将inputStream变为Bitmap
     * @param inputStream 输入流
     * @return Bitmap
     */
    public Bitmap inputStreamToBitmap(InputStream inputStream){
        if(inputStream != null){
            return BitmapFactory.decodeStream(inputStream);
        }
        else {
            return null;
        }
    }

    public void setImageByByte(ImageView image, byte[] byteImage, int resourceDefault) {
        if(byteImage != null && byteImage.length > 0){
            Bitmap bitmap = BitmapFactory.decodeByteArray(byteImage, 0, byteImage.length);
            image.setImageBitmap(bitmap);
        }
        else {
            image.setImageResource(resourceDefault);
        }
    }

    /**
     * Drawable的id转化为 Bitmap
     * @param drawableId 资源的Id
     * @param context 上下文，用于通过context获取Drawable
     * @return Bitmap
     */
    @SuppressLint("UseCompatLoadingForDrawables")
    public Bitmap drawableIdToBitmap(int drawableId, Context context) {
        try {
            Drawable drawable = context.getDrawable(drawableId);
            if (drawable != null) {
                return drawableToBitmap(drawable);
            } else {
                return null;
            }
        } catch (Resources.NotFoundException e) {
            // 资源找不到的情况
            return null;
        }
    }

    /**
     * Drawable 转化为 Bitmap
     * @param drawable Drawable资源
     * @return Bitmap
     */
    public Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * 将文件转化为Bitmap
     * @param tempFile 图片文件
     * @return Bitmap
     */
    public Bitmap fileToBitmap(File tempFile){
        if(tempFile != null){
            return BitmapFactory.decodeFile(tempFile.getAbsolutePath());
        }
        else {
            return null;
        }
    }

    /**
     * 将位图转化为文件
     * @param bitmap                bitmap图片资源
     * @param currentImageUri       uri文件资源路径
     * @param context               上下文
     * @return                      File文件
     */
    public File bitmapToFile(Bitmap bitmap, Uri currentImageUri, Context context){
        // 创建临时文件
        // 根据 Uri 获取文件路径
        ContentResolver contentResolver = context.getContentResolver();
        String filePath = FileUtil.getFilePathFromContentUri(currentImageUri,contentResolver);
        Log.d("Runtime","currentImageUri:"+currentImageUri);
        Log.d("Runtime","filePath:"+filePath);
        File file = new File(filePath);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {

            // 将Bitmap压缩为JPEG格式,质量为100
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);

            byte[] bitmapData = bos.toByteArray();

            // 将字节数组写入文件
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(bitmapData);
                fos.flush();
            }
        } catch (IOException e) {
            Log.e(TAG, "保存 Bitmap 到文件时发生异常: " + file.getAbsolutePath(), e);
        } finally {
            try {
                bos.close();
            } catch (IOException e) {
                Log.e(TAG, "关闭 ByteArrayOutputStream 时发生异常", e);
            }
        }

        return file;
    }

    @SuppressLint("Range")
    private String getPathFromUri(Uri uri, Context context) {
        String filePath = null;
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{"_data"}, null, null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    filePath = cursor.getString(cursor.getColumnIndex("_data"));
                }
                cursor.close();
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            filePath = uri.getPath();
        }
        return filePath;
    }

    private boolean isImageFile(File file) {
        String mimeType = URLConnection.guessContentTypeFromName(file.getName());
        return mimeType != null && mimeType.startsWith("image/");
    }

    // 图片压缩
    public Bitmap compressBitmap(Bitmap originalBitmap, int newWidth, int newHeight) {
        return Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true);
    }

    // 图片比例压缩
    public Bitmap processImage(Bitmap bitmap, int maxSize) {

        // 获取 Bitmap 的当前宽度和高度
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();


        // 检查是否超过最大尺寸
        if (width > maxSize || height > maxSize) {
            // 计算缩放比例
            float scale = Math.min((float) maxSize / width, (float) maxSize / height);

            // 计算新的宽度和高度
            int newWidth = Math.round(width * scale);
            int newHeight = Math.round(height * scale);

            // 压缩 Bitmap
            bitmap = compressBitmap(bitmap, newWidth, newHeight);
        }

        return bitmap;
    }

}
