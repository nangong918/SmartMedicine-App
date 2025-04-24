package com.example.chattest.Utils;


import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ImageUtils {

    public Bitmap uriToBitmap(Context context, Uri uri) throws IOException {
        //ContentResolver 用到了Content Provide的方法
        ContentResolver resolver = context.getContentResolver();
        InputStream inputStream = resolver.openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
        inputStream.close();
        return bitmap;
    }

    public byte[] bitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        try {
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return byteArray;
    }

    final float imageSizeLimit = 0.1f;//超过100kb就裁剪

    // 将该方法添加到你的类中
    public byte[] resizeImageIfNeeded(Bitmap originalBitmap) {
        byte[] imageBytes = bitmapToByteArray(originalBitmap);
        int imageSize = imageBytes.length;
        boolean isImageSizeExceeded = (imageSize > imageSizeLimit * 1024 * 1024);

        if (isImageSizeExceeded) {
            double scale = Math.sqrt((imageSizeLimit * 1024 * 1024) / (double) imageSize);

            int width = (int) (originalBitmap.getWidth() * scale);
            int height = (int) (originalBitmap.getHeight() * scale);
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, width, height, true);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            scaledBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);

            imageBytes = outputStream.toByteArray();
        }
        return imageBytes;
    }

    public void SetImageByByte(ImageView image, byte[] byteImage,int Res_Default) {
        if(byteImage != null && byteImage.length > 0){
            Bitmap bitmap = BitmapFactory.decodeByteArray(byteImage, 0, byteImage.length);
            image.setImageBitmap(bitmap);
        }
        else {
            image.setImageResource(Res_Default);
        }
    }

    public Bitmap ChangeByteToBitmap(@NonNull byte[] byteImage){
        return BitmapFactory.decodeByteArray(byteImage, 0, byteImage.length);
    }

    public byte[] drawableToByteArray(int res,Context context) {
        @SuppressLint("UseCompatLoadingForDrawables") Drawable drawable = context.getDrawable(res);
        Bitmap bitmap = drawableToBitmap(drawable);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        return outputStream.toByteArray();
    }

    private Bitmap drawableToBitmap(Drawable drawable) {
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
}

