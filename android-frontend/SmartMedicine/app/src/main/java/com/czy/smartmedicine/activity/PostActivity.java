package com.czy.smartmedicine.activity;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.czy.appcore.network.api.handle.SyncRequestCallback;
import com.czy.baseUtilsLib.activity.BaseActivity;
import com.czy.baseUtilsLib.image.ImageLoadUtil;
import com.czy.baseUtilsLib.viewModel.ViewModelUtil;
import com.czy.appview.view.post.CommentAdapter;
import com.czy.domain.ao.home.PostIntentAo;
import com.czy.domain.vo.entity.home.CommentVo;
import com.czy.domain.fragmentActivityAo.post.PostActivityVo;
import com.czy.smartmedicine.MainApplication;
import com.czy.smartmedicine.databinding.ActivityPostBinding;
import com.czy.smartmedicine.viewModel.activity.PostVm;
import com.czy.smartmedicine.viewModel.base.ApiViewModelFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 帖子详情界面
 * todo 点赞，评论，收藏，转发
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

        initIntent();
        initViewModel();
        initRecyclerView();

        // 利用postId去网络请求帖子信息（先请求1页的评论内容）
        vm.getSinglePost(1, this, new SyncRequestCallback() {
            @Override
            public void onThrowable(Throwable throwable) {

            }

            @Override
            public void onAllRequestSuccess() {

            }
        });
    }

    @Override
    protected void setListener() {
        super.setListener();
        binding.btnBack.setOnClickListener(v -> {
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
    }

    private PostVm vm;

    private void initViewModel(){
        ApiViewModelFactory apiViewModelFactory = new ApiViewModelFactory(MainApplication.getApiRequestImplInstance(), MainApplication.getInstance().getMessageSender());
        vm = ViewModelUtil.newViewModel(this, apiViewModelFactory, PostVm.class);

        initViewModelVo();

        observeLivedata();

        // 绑定viewModel
        binding.setViewModel(vm);
        // 设置监听者
        binding.setLifecycleOwner(this);
    }

    private void initViewModelVo(){
        PostActivityVo vo = new PostActivityVo();
        vo.postVoLd.postIdLd.setValue(currentActivityPostId);
        vm.init(vo);
    }

    private void observeLivedata() {
        observePostVo();
        observeCommentVo();
    }

    private void observePostVo(){
        vm.postActivityVo.postVoLd.authorAvatarUrlLd.observe(
                this, authorAvatarUrl -> {
                    if (TextUtils.isEmpty(authorAvatarUrl)){
                        return;
                    }
                    ImageLoadUtil.loadImageViewByUrl(authorAvatarUrl, binding.authorFacePicture);
                }
        );

        vm.postActivityVo.postVoLd.postImgUrlsLd.observe(
                this, postImgUrls -> {
                    if (postImgUrls == null){
                        return;
                    }
                    if (!postImgUrls.isEmpty()){
                        ImageLoadUtil.loadImageViewByUrl(postImgUrls.get(0), binding.articlePicture);
                    }
                }
        );

        vm.postActivityVo.postVoLd.postContentLd.observe(
                this, content -> {
                    if (TextUtils.isEmpty(content)){
                        return;
                    }
                    binding.content.setText(content);
                }
        );

        vm.postActivityVo.postVoLd.postTitleLd.observe(
                this, title -> {
                    if (TextUtils.isEmpty(title)){
                        return;
                    }
                    binding.Title.setText(title);
                }
        );

        vm.postActivityVo.postVoLd.authorNameLd.observe(
                this, authorName -> {
                    if (TextUtils.isEmpty(authorName)){
                        return;
                    }
                    binding.authorName.setText(authorName);
                }
        );

        vm.postActivityVo.postVoLd.likeNumLd.observe(
                this, likeNum -> {
                    if (TextUtils.isEmpty(likeNum)){
                        return;
                    }
                    binding.tvLikeNum.setText(likeNum);
                }
        );

        vm.postActivityVo.postVoLd.collectNumLd.observe(
                this, collectNum -> {
                    if (TextUtils.isEmpty(collectNum)){
                        return;
                    }
                    binding.tvCollectionNum.setText(collectNum);
                }
        );

        vm.postActivityVo.postVoLd.commentNumLd.observe(
                this, commentNum -> {
                    if (TextUtils.isEmpty(commentNum)){
                        return;
                    }
                    binding.tvCommentNum.setText(commentNum);
                }
        );

        vm.postActivityVo.postVoLd.isLikeLd.observe(
                this, isLike -> {
                    if (isLike){
                        binding.imgFavorite.setImageResource(com.czy.appview.R.drawable.favorite_full);
                    }else{
                        binding.imgFavorite.setImageResource(com.czy.appview.R.drawable.favorite_border);
                    }
                }
        );

        vm.postActivityVo.postVoLd.isCollectLd.observe(
                this, isCollect -> {
                    if (isCollect){
                        binding.imgvStar.setImageResource(com.czy.appview.R.drawable.star_full);
                    }
                    else{
                        binding.imgvStar.setImageResource(com.czy.appview.R.drawable.star_border);
                    }
                }
        );
    }

    @SuppressLint("NotifyDataSetChanged")
    private void observeCommentVo() {
        vm.postActivityVo.commentNumLd.observe(
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
        List<CommentVo> commentVos = Optional.ofNullable(vm.postActivityVo)
                .map(ao -> ao.commentVos)
                .orElse(new ArrayList<>());
        adapter = new CommentAdapter(commentVos);
        binding.rclvComment.setAdapter(adapter);
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