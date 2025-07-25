package com.czy.smartmedicine.viewModel.fragment;

import android.annotation.SuppressLint;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.RecyclerView;

import com.czy.appcore.network.netty.api.send.SocketMessageSender;
import com.czy.baseUtilsLib.network.BaseResponse;
import com.czy.customviewlib.view.home.OnRecommendCardClick;
import com.czy.customviewlib.view.home.PostAdapter;
import com.czy.dal.ao.home.FeatureContext;
import com.czy.dal.ao.home.PostAo;
import com.czy.dal.ao.home.PostInfoUrlAo;
import com.czy.dal.dto.http.request.RecommendPostRequest;
import com.czy.dal.dto.http.response.RecommendPostResponse;
import com.czy.dal.vo.fragmentActivity.HomeVo;
import com.czy.datalib.networkRepository.ApiRequestImpl;
import com.czy.smartmedicine.MainApplication;
import com.czy.smartmedicine.manager.PostClickManager;
import com.czy.smartmedicine.utils.ViewModelUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class HomeViewModel extends ViewModel {

    private static final String TAG = HomeViewModel.class.getName();

    private final ApiRequestImpl apiRequestImpl;
    private final SocketMessageSender socketMessageSender;

    public HomeViewModel(ApiRequestImpl apiRequestImpl, SocketMessageSender socketMessageSender) {
        this.apiRequestImpl = apiRequestImpl;
        this.socketMessageSender = socketMessageSender;
    }

    //---------------------------Vo Ld---------------------------

    public HomeVo homeVo = new HomeVo();
    public PostAdapter postAdapter;

    public void init(HomeVo homeVo, FragmentActivity activity){
        this.homeVo = homeVo;
        initialNetworkRequest();
        initPostClickManager(activity);
    }

    //==========RecyclerView

    public void initRecyclerView(RecyclerView recyclerView, FragmentActivity activity){
        List<PostAo> postAoList = Optional.ofNullable(homeVo)
                .map(vo -> vo.postListVo)
                .map(pvo -> pvo.postAoList)
                .orElse(new ArrayList<>());

        OnRecommendCardClick onRecommendCardClick = postClickManager.getOnRecommendCardClick(activity);

        postAdapter = new PostAdapter(
                postAoList,
                onRecommendCardClick
        );

        recyclerView.setAdapter(postAdapter);
    }

    //---------------------------NetWork---------------------------

    // 初始化网络请求
    private void initialNetworkRequest() {
    }

    // 获取推荐帖子 todo 适配一下（debug模式下暂时不进行用户已推荐过滤）
    public void getRecommendPosts(){
        FeatureContext currentFeatureContext = getFeatureContext();
        RecommendPostRequest request = new RecommendPostRequest();
        request.featureContext = currentFeatureContext;
        request.userAccount = MainApplication.getInstance().getUserLoginInfoAo().account;
        apiRequestImpl.getRecommendPosts(
                request,
                this::handleGetPostList,
                ViewModelUtil::globalThrowableToast
        );
    }

    /**
     * 处理获取帖子列表
     * @param response  接口返回的数据
     */
    @SuppressLint("NotifyDataSetChanged")
    private void handleGetPostList(BaseResponse<RecommendPostResponse> response) {
        List<PostInfoUrlAo> postInfoAos = Optional.ofNullable(response)
                .map(BaseResponse::getData)
                .map(RecommendPostResponse::getPostInfoUrlAos)
                .orElse(new ArrayList<>());

        if (postInfoAos.isEmpty()){
            return;
        }

        // postInfoUrlAo -> PostAo
        List<PostAo> postAoList = this.postClickManager.getPostAoListByResponse(postInfoAos);

        // homeList原先存在的列表
        List<PostAo> homeList = Optional.ofNullable(homeVo.postListVo.postAoList)
                        .orElse(new ArrayList<>());

        int beforeSize = homeList.size();
        homeList.addAll(postAoList);

        // 设置值，观察者模式会通知view更新
//        homeVo.postListVo.postAoListLd.setValue(homeList);

        // list都采用手动更新，而不是livedata观察
        // 更新beforeSize ~ size
//        this.postAdapter.notifyItemRangeChanged(beforeSize, homeList.size());
        for (int i = beforeSize; i < homeList.size(); i++) {
            postAdapter.notifyItemInserted(i);
        }
    }

    //---------------------------Logic---------------------------

    public PostClickManager postClickManager;

    private void initPostClickManager(FragmentActivity activity){
        postClickManager = new PostClickManager(
                homeVo.postListVo.postAoList,
                this.socketMessageSender,
                activity
        );
    }

    private final FeatureContext featureContext = new FeatureContext();

    public void setFeatureContext(List<Long> postIds){
        // 添加全部上下文
        featureContext.postIds.addAll(postIds);
        featureContext.timestamp = System.currentTimeMillis();
    }

    public FeatureContext getFeatureContext(){
        FeatureContext copyFeatureContext = this.featureContext.copy();
        clearFeatureContext();
        return copyFeatureContext;
    }

    private void clearFeatureContext(){
        this.featureContext.clear();
    }
}
