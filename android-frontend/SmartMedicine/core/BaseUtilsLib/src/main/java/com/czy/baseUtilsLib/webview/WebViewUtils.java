package com.czy.baseUtilsLib.webview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.util.Log;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSONObject;
import com.czy.baseUtilsLib.debug.DebugMyUtil;
import com.czy.baseUtilsLib.json.JsonUtils;

import java.io.File;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;

public class WebViewUtils {

    public static String TAG = WebViewUtils.class.getSimpleName();

    //--------------------------Setting--------------------------

    @SuppressLint("SetJavaScriptEnabled")
    public static void setBaseWebViewSetting(WebView webView){
        WebSettings settings = webView.getSettings();

        // 启用 JavaScript
        settings.setJavaScriptEnabled(true);
        //增加 设置脚本是否允许自动打开弹窗
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setBlockNetworkImage(false);
        settings.setSupportZoom(false);
        //WebView.setWebContentsDebuggingEnabled(true);
        // 设置文本大小   100% 的默认文本缩放
        settings.setTextZoom(100);
        settings.setDefaultFontSize(16);
        //支持自动加载图片
        settings.setLoadsImagesAutomatically(true);

        // 启用支持多窗口
        settings.setSupportMultipleWindows(true);
        settings.setDatabaseEnabled(true);
        // 启用 DOM 存储 API : 返回上个界面不刷新  允许本地缓存
        settings.setDomStorageEnabled(true);
        // 启用缓存 : 增加 设置缓存LOAD_DEFAULT   LOAD_CACHE_ONLY,LOAD_NO_CACHE
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        // 启用访问文件
        settings.setAllowFileAccess(true);
        // 设置布局算法 不支持放大缩小
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        //不支持放大缩小
        settings.setDisplayZoomControls(false);

        //增加 设置编码格式
        settings.setDefaultTextEncodingName("utf-8");

        webView.setLongClickable(true);
        webView.setScrollbarFadingEnabled(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

        // 启用 HTTPS 支持
        webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
    }

    //--------------------------param--------------------------

    public static void setClient(WebView webView, SendParamToH5Interface sendParamToH5Interface, Map<String, Object> sendParams, String functionName){
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            // 在Web加载结束的时候传参
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // 异步操作，需要发送前确定接口没有被销毁
                sendParamToH5Interface.send(sendParams, functionName, webView);
            }
        });
    }

    public interface SendParamToH5Interface{
        void send(Map<String, Object> sendParams, String functionName, WebView webView);
    }

    public static void sendParamToH5(Map<String, Object> sendParams, String functionName, WebView webView){
        if(sendParams != null && !sendParams.isEmpty()){

            String h5LoadCode = getJavaScriptParam(sendParams, functionName);

            Log.d(TAG,"调用H5方法：\n" + h5LoadCode);

            webView.evaluateJavascript(h5LoadCode, null);
        }
    }

    private static String getJavaScriptParam(Map<String, Object> sendParams, String functionName) {
        JSONObject paramJson = JsonUtils.mapToJson(sendParams);

        //String paramJsonStr = paramJson.toJSONString();
        String paramJsonStr = JsonUtils.toJsonString(paramJson);

        Log.d(TAG,"传递参数内容：\n" + paramJsonStr);

        String jsCode = functionName + "(" + paramJsonStr + ");";

        return "javascript:" + jsCode;
    }

    //--------------------------openWeb--------------------------

    public static class OpenOnWeb {
        public OpenOnWebCallback loadResourceCallback;
        public Activity activity;

        public OpenOnWeb(OpenOnWebCallback callback, Activity activity){
            this.loadResourceCallback = callback;
            this.activity = activity;
        }

        @JavascriptInterface
        public void openWeb(String url) {
            openBrowserWithUrl(url, activity);
        }
    }

    public interface OpenOnWebCallback {
        void openWebCallback(String url);
    }

    private static void openBrowserWithUrl(String url, Activity activity) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        activity.startActivity(intent);
    }

    public static void setOpenOnWeb(WebView webView, OpenOnWebCallback openOnWebCallback, Activity activity,String name){
        assert name != null && !name.isEmpty();
        webView.addJavascriptInterface(new OpenOnWeb(openOnWebCallback,activity),name);
    }

    //--------------------------resource--------------------------

    public static class WebResourceUtil{
        //private ValueCallback<Uri> mUploadMessage;
        private ValueCallback<Uri[]> mUploadCallbackAboveL;
        private Uri imageUri; //图片地址
        private ActivityResultLauncher<Intent> activityResultLauncher;

        public WebResourceUtil(AppCompatActivity activity){
            init(activity);
        }

        public void init(AppCompatActivity activity) {
            activityResultLauncher = activity.registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        // 成功的情况
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // Intent非空
                            if (result.getData() != null){
                                // uri非空
                                Uri resultUri = result.getData().getData();
                                Uri[] uris;
                                if (resultUri != null){
                                    uris = new Uri[]{resultUri};
                                    for (Uri uri : uris) {
                                        Log.d(TAG, "系统里取到的图片uri：" + uri.toString());
                                    }
                                    mUploadCallbackAboveL.onReceiveValue(uris);
                                }
                                else {
                                    mUploadCallbackAboveL.onReceiveValue(null);
                                }
                            }
                            // Intent空的情况(自己命名的照片)
                            else {
                                Log.d(TAG, "自己命名的图片：" + imageUri.toString());
                                // 处理使用指定路径的图片
                                mUploadCallbackAboveL.onReceiveValue(new Uri[]{imageUri});
                            }
                            updatePhotos(activity);
                        }
                        else {
                            mUploadCallbackAboveL.onReceiveValue(null);
                        }
                        mUploadCallbackAboveL = null;
                    }
            );
        }


        //发送广播进行更新相册
        private void updatePhotos(AppCompatActivity activity) {
            // 该广播即使多发（即选取照片成功时也发送）也没有关系，只是唤醒系统刷新媒体文件
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            intent.setData(imageUri);
            activity.sendBroadcast(intent);
        }

        private void updatePhotosV2(AppCompatActivity activity, Uri imageUri) {
            // 获取文件路径
            File file = new File(imageUri.getPath());

            // 使用 MediaScannerConnection 扫描文件，更新相册
            MediaScannerConnection.scanFile(activity, new String[]{file.getAbsolutePath()}, null,
                    (path, uri) -> {
                        // 扫描完成后的回调
                        if (uri != null) {
                            Log.i(TAG, "扫描完成: " + uri);
                        } else {
                            Log.e(TAG, "扫描失败: " + path);
                        }
                    });
        }

        /**
         * 调用相机/相册选择窗
         */
        public void takePhoto() {
            String filePath = Environment.getExternalStorageDirectory() + File.separator;
            String fileName = "IMG_" + DateFormat.format("yyyyMMdd_hhmmss", Calendar.getInstance(Locale.TAIWAN)) + ".jpg";
            imageUri = Uri.fromFile(new File(filePath + fileName));

            // 创建 Intent 进行图片选择
            Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            Intent chooserIntent = Intent.createChooser(photoPickerIntent, "选择上传方式");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{captureIntent});

            // 使用 ActivityResultLauncher 启动选择器
            activityResultLauncher.launch(chooserIntent);
        }

        // 设置调用Android资源
        public void setChromeClient(WebView webView){
            webView.setWebChromeClient(new WebChromeClient(){
                // 实现 onShowFileChooser 方法,处理 H5 页面的文件选择
                @Override
                public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                    Log.d(TAG,"H5 页面的文件选择");
                    mUploadCallbackAboveL = filePathCallback;
                    takePhoto();
                    return super.onShowFileChooser(webView, filePathCallback, fileChooserParams);
                    // return true;
                }

                // 实现 onPermissionRequest 方法,处理 H5 页面的权限请求
                @Override
                public void onPermissionRequest(PermissionRequest request) {
                    super.onPermissionRequest(request);
                    Log.d(TAG, "H5 页面的权限请求");
                    request.grant(request.getResources());
                }
            });
        }
    }

}
