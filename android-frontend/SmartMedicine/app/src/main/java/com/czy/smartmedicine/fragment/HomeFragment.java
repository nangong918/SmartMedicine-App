package com.czy.smartmedicine.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;

import com.czy.baseUtilsLib.activity.BaseFragment;
import com.czy.baseUtilsLib.viewModel.ViewModelUtil;
import com.czy.customviewlib.view.home.OnRecommendCardClick;
import com.czy.customviewlib.view.home.PostAdapter;

import com.czy.dal.ao.home.PostIntentAo;
import com.czy.dal.vo.entity.home.PostListVo;
import com.czy.dal.vo.entity.home.PostVo;
import com.czy.smartmedicine.MainApplication;
import com.czy.smartmedicine.activity.PostActivity;
import com.czy.smartmedicine.databinding.FragmentHomeBinding;
import com.czy.smartmedicine.viewModel.ApiViewModelFactory;
import com.czy.smartmedicine.viewModel.HomeViewModel;

import java.util.Optional;


/**
 * @author 13225
 */
public class HomeFragment extends BaseFragment<FragmentHomeBinding> {


    public HomeFragment() {
        super(HomeFragment.class);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected void init() {
        super.init();

        initActivityResultLauncher();

        initViewModel();

        initRecyclerView();
    }

    @Override
    protected void setListener() {
        super.setListener();
    }

    //---------------------------viewModel---------------------------

    private HomeViewModel viewModel;

    private void initViewModel(){
        ApiViewModelFactory apiViewModelFactory = new ApiViewModelFactory(MainApplication.getApiRequestImplInstance(), MainApplication.getInstance().getMessageSender());
        viewModel = ViewModelUtil.newViewModel(this, apiViewModelFactory, HomeViewModel.class);
    }

    //-----------------------RecyclerView-----------------------

    private void initRecyclerView(){
        PostListVo postListVo = Optional.ofNullable(viewModel.homeVo)
                .map(ao -> ao.postListVo)
                .orElse(new PostListVo());

        OnRecommendCardClick onRecommendCardClick = getOnRecommendCardClick();

        PostAdapter adapter = new PostAdapter(postListVo.postAoListLd.getValue(), onRecommendCardClick);

        binding.rclvRecommend.setAdapter(adapter);
    }

    private OnRecommendCardClick getOnRecommendCardClick() {
        return new OnRecommendCardClick() {
            @Override
            public void onCardClick(int position, int cardType, int cardId) {
                PostVo postVo = viewModel.getPostInfoByList(position, cardId);
                Long postId = Optional.ofNullable(postVo)
                        .map(p -> p.postId)
                        .orElse(null);
                if (postId == null){
                    Log.e(TAG, "帖子id为空");
                    Toast.makeText(requireActivity(), "帖子异常，请查看其他帖子", Toast.LENGTH_SHORT).show();
                    return;
                }

                startPostActivityIntent(postId);
            }

            @Override
            public void onButtonClick(int position, int cardType, int cardId, int buttonType) {
                viewModel.onButtonClick(position, cardType, cardId, buttonType);
            }
        };
    }

    //-----------------------intent-----------------------

    private ActivityResultLauncher<Intent> openPostActivityLauncher;
    private long startReadPostTime;
    private void initActivityResultLauncher(){
        // 用于记录看了多久的启动方法：openPostActivityLauncher
        openPostActivityLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // 计算观看时长
                        long endTime = System.currentTimeMillis();
                        long duration = endTime - startReadPostTime;
                        if (result.getData() == null){
                            Log.w(TAG, "浏览post返回结果失败，result.getData() == null");
                            return;
                        }
                        Long postId = result.getData().getLongExtra(PostIntentAo.POST_ID, Long.MIN_VALUE);
                        viewModel.recordViewingDuration(duration, postId);
                    }
                }
        );
    }

    private void startPostActivityIntent(Long postId){
        if (postId == null){
            return;
        }
        PostIntentAo postIntentAo = new PostIntentAo();
        postIntentAo.postId = postId;
        Intent intent = new Intent(requireActivity(), PostActivity.class);
        intent.putExtra(PostIntentAo.POST_OPEN_INTENT, postIntentAo);
        openPostActivityLauncher.launch(intent);

        startReadPostTime = System.currentTimeMillis();
        viewModel.recordPostView(postId);

    }

}