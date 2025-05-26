package com.czy.smartmedicine.activity;



import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.czy.baseUtilsLib.activity.BaseActivity;
import com.czy.dal.ao.home.PostIntentAo;
import com.czy.smartmedicine.databinding.ActivityPostBinding;

import java.util.Optional;

public class PostActivity extends BaseActivity<ActivityPostBinding> {

    public PostActivity() {
        super(PostActivity.class);
    }

    @Override
    protected void init() {
        super.init();
        initIntent();
    }

    @Override
    protected void setListener() {
        super.setListener();
        binding.topBar.setOnClickListener(v -> {
            finishActivityWithPostId(currentActivityPostId);
        });
    }

    private Long currentActivityPostId = null;

    private void initIntent(){
        Intent initIntent = getIntent();
        PostIntentAo postIntentAo = (PostIntentAo) initIntent.getSerializableExtra(PostIntentAo.POST_OPEN_INTENT);

        currentActivityPostId = Optional.ofNullable(postIntentAo)
                .map(p -> p.postId)
                .orElse(null);

        if (currentActivityPostId == null){
            Log.e(TAG, "帖子id为空");
            Toast.makeText(this, "帖子异常，请查看其他帖子", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // TODO 利用postId去网络请求帖子信息
    }

    // 在 PostActivity 中
    private void finishActivityWithPostId(Long postId) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(PostIntentAo.POST_ID, postId);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        // 在这里处理返回逻辑
        finishActivityWithPostId(currentActivityPostId);
    }
}