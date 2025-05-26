package com.czy.smartmedicine.activity;



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


    private void initIntent(){
        Intent initIntent = getIntent();
        PostIntentAo postIntentAo = (PostIntentAo) initIntent.getSerializableExtra(PostIntentAo.POST_OPEN_INTENT);

        Long postId = Optional.ofNullable(postIntentAo)
                .map(p -> p.postId)
                .orElse(null);

        if (postId == null){
            Log.e(TAG, "帖子id为空");
            Toast.makeText(this, "帖子异常，请查看其他帖子", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // TODO 利用postId去网络请求帖子信息
    }
}