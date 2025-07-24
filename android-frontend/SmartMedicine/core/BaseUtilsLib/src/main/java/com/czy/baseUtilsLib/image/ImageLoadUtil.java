package com.czy.baseUtilsLib.image;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.czy.baseUtilsLib.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * @author 13225
 */
public class ImageLoadUtil {

    protected static final String TAG = ImageLoadUtil.class.getSimpleName();

    // 不为空检查
    private static boolean needAbort(ImageView imageView) {
        if (imageView == null) {
            return true;
        }
        Context context = imageView.getContext();
        if (context == null) {
            return true;
        }
        if (context instanceof Activity) {
            return ((Activity) context).isFinishing();
        }
        return false;
    }

    /**
     * 加载网络图片
     * @param url           网络图片的url
     * @param imageView     需要加载到的imageview
     */
    public static void loadImageViewByUrl(String url, ImageView imageView){
        // 使用 Glide 加载图片
        if(needAbort(imageView)) {
            return;
        }
        if(url == null || url.isEmpty()) {
            return;
        }
        Glide.with(imageView.getContext())
                .load(url)
                .apply(new RequestOptions()
                        .placeholder(R.drawable.icon_default_acatar)
                        .error(R.drawable.icon_default_acatar)
                )
                .listener(new RequestListener<>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        Log.e(TAG, "Error loading image: " + Optional.ofNullable(e)
                                .map(GlideException::getMessage)
                                .orElse(null));
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                })
                .into(imageView);
    }


    public static void loadImageViewByUrl2(String url, ImageView imageView){
        if(needAbort(imageView)) {
            return;
        }

        Glide.with(imageView.getContext())
                .load(url)
                .apply(
                        new RequestOptions()
                                .placeholder(R.drawable.icon_default_acatar)
                                .error(R.drawable.icon_default_acatar)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .format(DecodeFormat.PREFER_ARGB_8888)
                                .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                ).into(imageView);
    }

    /**
     * 加载图片资源：存在本地文件资源/网络资源的区分
     * @param uri           图片的uri
     * @param imageView     需要加载到的imageview
     */
    public static void loadImageViewByResource(String uri, ImageView imageView){
        Glide.with(imageView.getContext())
                .load(uri)
                .error(R.drawable.icon_dialog_loading)
                .into(imageView);
//        // 网络
//        if (uri.startsWith("http") || uri.startsWith("https")){
//            Glide.with(imageView.getContext())
//                    .load(uri)
//                    .error(R.drawable.icon_dialog_loading)
//                    .into(imageView);
//        }
//        // 本地file
//        else {
//            File file = new File(uri);
//            if (file.exists()) {
//                Glide.with(imageView.getContext())
//                        .load(file)
//                        .error(R.drawable.icon_dialog_loading)
//                        .into(imageView);
//            }
//            else {
//                Glide.with(imageView.getContext())
//                        .load(R.drawable.icon_dialog_loading)
//                        .into(imageView);
//            }
//        }
    }

    /**
     * 加载图片资源：存在本地文件资源/网络资源的区分
     * @param imageUri          图片的uri
     * @param lifecycleOwner    生命周期
     * @param imageView         需要加载到的imageview
     */
    public static void observeImageResource(MutableLiveData<String> imageUri, LifecycleOwner lifecycleOwner, ImageView imageView){
        imageUri.observe(lifecycleOwner, uri -> {
            if (uri != null) {
                ImageLoadUtil.loadImageViewByResource(uri, imageView);
            }
        });
    }
}
