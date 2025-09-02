package com.czy.smartmedicine.activity;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.czy.appview.view.post.CommentAdapter;
import com.czy.baseutil.activity.BaseActivity;
import com.czy.baseutil.image.ImageLoadUtil;
import com.czy.baseutil.viewModel.ViewModelUtil;
import com.czy.domain.ao.entity.CommentAo;
import com.czy.domain.ao.home.PostIntentAo;
import com.czy.domain.fragmentActivityAo.post.PostAAo;
import com.czy.smartmedicine.MainApplication;
import com.czy.smartmedicine.databinding.ActivityPostBinding;
import com.czy.smartmedicine.viewModel.activity.PostVm;
import com.czy.smartmedicine.viewModel.base.ApiViewModelFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 帖子详情界面
 */
public class PostActivity extends BaseActivity<ActivityPostBinding> {

    public PostActivity() {
        super(PostActivity.class);
    }

    @Override
    public ActivityPostBinding getBinding() {
        return ActivityPostBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void init() {
        super.init();

        Log.i("check_netty", "PostActivity::MessageSender: " + MainApplication.getInstance().getMessageSender());

        initViewModel();
        initRecyclerView();
    }

    @Override
    protected void setListener() {
        super.setListener();
        binding.btnBack.setOnClickListener(v -> {
            finishActivityWithPostId(vm.postAAo.postId);
        });
    }


    private void initIntent(){
        Intent initIntent = getIntent();
        PostIntentAo postIntentAo = (PostIntentAo) initIntent.getSerializableExtra(PostIntentAo.POST_OPEN_INTENT);

        vm.postAAo.postId = Optional.ofNullable(postIntentAo)
                .map(p -> p.postId)
                .orElse(null);

        if (vm.postAAo.postId == null){
            Log.e(TAG, "帖子id为空");
            Toast.makeText(this, "帖子异常，请查看其他帖子", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
    }

    private PostVm vm;

    private void initViewModel(){
        ApiViewModelFactory apiViewModelFactory = new ApiViewModelFactory(MainApplication.getApiRequestImplInstance(), MainApplication.getInstance().getMessageSender());
        vm = ViewModelUtil.newViewModel(this, apiViewModelFactory, PostVm.class);

        initViewModelVo();

        initIntent();

        observeLivedata();

        // 绑定viewModel
        binding.setViewModel(vm);
        // 设置监听者
        binding.setLifecycleOwner(this);
    }

    private void initViewModelVo(){
        PostAAo aao = new PostAAo();
        vm.init(aao, this);
    }

    private void observeLivedata() {
        observePostVo();
        observeCommentVo();
    }

    private void observePostVo(){

        // author
        vm.postAAo.postAVo.authorAvatarUrlLd.observe(this, authorAvatarUrl -> {
                    if (TextUtils.isEmpty(authorAvatarUrl)){
                        return;
                    }
                    ImageLoadUtil.loadImageViewByUrl(
                            authorAvatarUrl, binding.imgvAuthorAvatar
                    );
                }
        );
        vm.postAAo.postAVo.authorNameLd.observe(this, authorName -> {
            binding.tvAuthorName.setText(authorName);
        });

        // post
        vm.postAAo.postAVo.postTitleLd.observe(this, postTitle -> {
            binding.tvTime.setText(postTitle);
        });
        vm.postAAo.postAVo.postContentLd.observe(this, postContent -> {
            binding.tvContent.setText(postContent);
        });
        vm.postAAo.postAVo.postPublishTimeLd.observe(this, publishTime -> {
            binding.tvTime.setText(publishTime);
        });
        vm.postAAo.postAVo.postViewNumLd.observe(this, viewNum -> {
            binding.tvViewNum.setText(viewNum);
        });

        // action
        vm.postAAo.postAVo.likePostLd.observe(this, likePost -> {
            binding.btnLike.setImageResource(likePost ?
                    com.czy.appview.R.drawable.favorite2_48px :
                    com.czy.appview.R.drawable.favorite_24px
            );
        });
        vm.postAAo.postAVo.collectPostLd.observe(this, collectPost -> {
            binding.btnCollect.setImageResource(collectPost ?
                    com.czy.appview.R.drawable.kid_star2_48px :
                    com.czy.appview.R.drawable.kid_star_48px
            );
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void observeCommentVo() {
        vm.postAAo.postAVo.commentNumLd.observe(
                this,
                commentNum -> {
                    // 通知适配器数据已更改
                    adapter.notifyDataSetChanged();
                }
        );
    }

    // recyclerView
    private CommentAdapter adapter;
    private void initRecyclerView(){
        List<CommentAo> commentAos = Optional.ofNullable(vm.postAAo)
                .map(aao -> aao.postAVo)
                .map(avo -> avo.commentAos)
                .orElse(new ArrayList<>());
        adapter = new CommentAdapter(commentAos);
        binding.rclvCommend.setAdapter(adapter);
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
        finishActivityWithPostId(vm.postAAo.postId);
    }
}