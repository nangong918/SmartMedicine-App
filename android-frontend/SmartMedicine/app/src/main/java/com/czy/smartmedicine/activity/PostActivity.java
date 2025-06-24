package com.czy.smartmedicine.activity;



import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.czy.baseUtilsLib.activity.BaseActivity;
import com.czy.baseUtilsLib.image.ImageLoadUtil;
import com.czy.baseUtilsLib.viewModel.ViewModelUtil;
import com.czy.customviewlib.view.post.CommentAdapter;
import com.czy.dal.ao.home.PostIntentAo;
import com.czy.dal.vo.entity.home.CommentVo;
import com.czy.dal.vo.viewModelVo.post.PostActivityVo;
import com.czy.smartmedicine.MainApplication;
import com.czy.smartmedicine.databinding.ActivityPostBinding;
import com.czy.smartmedicine.viewModel.base.ApiViewModelFactory;
import com.czy.smartmedicine.viewModel.activity.PostViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PostActivity extends BaseActivity<ActivityPostBinding> {

    public PostActivity() {
        super(PostActivity.class);
    }

    @Override
    protected void init() {
        super.init();
        initIntent();
        initViewModel();
        initRecyclerView();
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

        // 利用postId去网络请求帖子信息（先请求1页的评论内容）
        viewModel.getSinglePost(currentActivityPostId, 1L);
    }

    private PostViewModel viewModel;

    private void initViewModel(){
        ApiViewModelFactory apiViewModelFactory = new ApiViewModelFactory(MainApplication.getApiRequestImplInstance(), MainApplication.getInstance().getMessageSender());
        viewModel = ViewModelUtil.newViewModel(this, apiViewModelFactory, PostViewModel.class);

        initViewModelVo();

        observeLivedata();

        // 绑定viewModel
        binding.setViewModel(viewModel);
        // 设置监听者
        binding.setLifecycleOwner(this);
    }

    private void initViewModelVo(){
        PostActivityVo postActivityVo = new PostActivityVo();
        viewModel.init(postActivityVo);
    }

    private void observeLivedata() {
        observePostVo();
        observeCommentVo();
    }

    private void observePostVo(){
        viewModel.postActivityVo.postVoLd.authorAvatarUrlLd.observe(
                this, authorAvatarUrl -> {
                    if (TextUtils.isEmpty(authorAvatarUrl)){
                        return;
                    }
                    ImageLoadUtil.loadImageViewByUrl(authorAvatarUrl, binding.authorFacePicture);
                }
        );

        viewModel.postActivityVo.postVoLd.postImgUrlsLd.observe(
                this, postImgUrls -> {
                    if (postImgUrls == null){
                        return;
                    }
                    if (!postImgUrls.isEmpty()){
                        ImageLoadUtil.loadImageViewByUrl(postImgUrls.get(0), binding.articlePicture);
                    }
                }
        );

        viewModel.postActivityVo.postVoLd.postContentLd.observe(
                this, content -> {
                    if (TextUtils.isEmpty(content)){
                        return;
                    }
                    binding.content.setText(content);
                }
        );

        viewModel.postActivityVo.postVoLd.postTitleLd.observe(
                this, title -> {
                    if (TextUtils.isEmpty(title)){
                        return;
                    }
                    binding.Title.setText(title);
                }
        );

        viewModel.postActivityVo.postVoLd.authorNameLd.observe(
                this, authorName -> {
                    if (TextUtils.isEmpty(authorName)){
                        return;
                    }
                    binding.authorName.setText(authorName);
                }
        );

        viewModel.postActivityVo.postVoLd.likeNumLd.observe(
                this, likeNum -> {
                    if (TextUtils.isEmpty(likeNum)){
                        return;
                    }
                    binding.tvLikeNum.setText(likeNum);
                }
        );

        viewModel.postActivityVo.postVoLd.collectNumLd.observe(
                this, collectNum -> {
                    if (TextUtils.isEmpty(collectNum)){
                        return;
                    }
                    binding.tvCollectionNum.setText(collectNum);
                }
        );

        viewModel.postActivityVo.postVoLd.commentNumLd.observe(
                this, commentNum -> {
                    if (TextUtils.isEmpty(commentNum)){
                        return;
                    }
                    binding.tvCommentNum.setText(commentNum);
                }
        );

        viewModel.postActivityVo.postVoLd.isLikeLd.observe(
                this, isLike -> {
                    if (isLike){
                        binding.imgFavorite.setImageResource(com.czy.customviewlib.R.drawable.favorite_full);
                    }else{
                        binding.imgFavorite.setImageResource(com.czy.customviewlib.R.drawable.favorite_border);
                    }
                }
        );

        viewModel.postActivityVo.postVoLd.isCollectLd.observe(
                this, isCollect -> {
                    if (isCollect){
                        binding.imgvStar.setImageResource(com.czy.customviewlib.R.drawable.star_full);
                    }
                    else{
                        binding.imgvStar.setImageResource(com.czy.customviewlib.R.drawable.star_border);
                    }
                }
        );
    }

    @SuppressLint("NotifyDataSetChanged")
    private void observeCommentVo() {
        viewModel.postActivityVo.commentNumLd.observe(
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
        List<CommentVo> commentVos = Optional.ofNullable(viewModel.postActivityVo)
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