package com.czy.baseUtilsLib.QR;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

public class QrUtils {

    protected final static String TAG = QrUtils.class.getSimpleName();

    public static Bitmap generateQrByUrl(String url, int width, int height){
        if(url != null && !url.isEmpty()){
            // 1. 获取要生成二维码的 URL
            // 2. 创建 MultiFormatWriter 对象
            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
            try {
                // 3. 生成二维码 BitMatrix
                BitMatrix bitMatrix = multiFormatWriter.encode(url, BarcodeFormat.QR_CODE, width, height);

                // 4. 将 BitMatrix 转换为 Bitmap
                Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                    }
                }
                return bitmap;
            } catch (WriterException e) {
                Log.e(TAG, "生成二维码时发生异常: " + e.getMessage(), e);
            }
        }
        return null;
    }

}
