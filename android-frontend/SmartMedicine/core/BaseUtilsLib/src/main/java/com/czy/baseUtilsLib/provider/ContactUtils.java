package com.czy.baseUtilsLib.provider;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class ContactUtils {

    public static final String TAG = ContactUtils.class.getSimpleName();

    public interface ContactResultCallback {
        void onContactSelected(String name, String number);
    }

    /**
     * 获得启动器
     * @param activity      Activity
     * @param callback      获得数据回调
     * @return              启动器
     */
    public static ActivityResultLauncher<Intent> initActivityResultLauncher(AppCompatActivity activity, SetContactDataCallback callback){
        return getContactActivityLauncher(activity, (name, number) -> {
            callback.setContactData((name == null ? "" : name),(number == null ? "" : parseNumberString(number)));
        });
    }

    /**
     * 获取联系人信息
     * @param launcher      启动器
     */
    public static void startActivityToGetContact(ActivityResultLauncher<Intent> launcher){
        launcher.launch(getContactPickerIntent());
    }

    /**
     * 解决有的用户手机号中间存在空格问题
     * @param input     手机号
     * @return          清洗后的手机号
     */
    private static String parseNumberString(String input) {
        // 移除空格和换行符
        String trimmedInput = input.replaceAll("[\\s\\n]", "");

        // 检查是否为数字
        if (trimmedInput.matches("\\d+")) {
            return trimmedInput; // 返回处理后的数字字符串
        } else {
            return ""; // 返回空字符串
        }
    }

    private static ActivityResultLauncher<Intent> getContactActivityLauncher(AppCompatActivity activity, ContactResultCallback callback) {
        return activity.registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            Uri uri = data.getData();
                            ContentResolver resolver = activity.getContentResolver();
                            if (uri == null){return;}
                            Cursor cursor = resolver.query(uri, new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME}, null, null, null);
                            if (cursor == null){return;}
                            if (cursor.moveToFirst()) {
                                String name = cursor.getString(1);
                                String number = cursor.getString(0);
                                callback.onContactSelected(name, number);
                            }
                            cursor.close();
                        }
                    }
                }
        );
    }

    private static Intent getContactPickerIntent() {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        if ("OPPO R11st".equals(Build.MODEL)) {
            intent.setType("vnd.android.cursor.dir/phone");
        } else {
            intent.setType("vnd.android.cursor.dir/phone_v2");
        }
        intent.setAction(Intent.ACTION_PICK);
        return intent;
    }

}
