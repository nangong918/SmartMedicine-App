package com.czy.smartmedicine.viewModel.fragment;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.activity.result.ActivityResultCaller;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.RecyclerView;

import com.czy.appcore.network.api.handle.SyncRequestCallback;
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
import com.czy.smartmedicine.utils.ResponseTool;
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

    public SocketMessageSender getSocketMessageSender(){
        return socketMessageSender;
    }

    //---------------------------Vo Ld---------------------------

    public HomeVo homeVo = new HomeVo();
    public PostAdapter postAdapter;

    public void init(HomeVo homeVo){
        this.homeVo = homeVo;
        initialNetworkRequest();
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
    public void getRecommendPosts(Context context, SyncRequestCallback callback){
        FeatureContext currentFeatureContext = getFeatureContext();
        RecommendPostRequest request = new RecommendPostRequest();
        request.featureContext = currentFeatureContext;
        request.featureContext.userId = MainApplication.getInstance().getUserLoginInfoAo().userId;
        apiRequestImpl.getRecommendPosts(
                request,
                response -> {
                    ResponseTool.handleSyncResponseEx(
                            response,
                            context,
                            callback,
                            this::handleGetPostList
                    );
                },
                throwable -> {
                    callback.onThrowable(throwable);
                    ViewModelUtil.globalThrowableToast(throwable);
                }
        );
    }

    /**
     * 处理获取帖子列表
     * @param response  接口返回的数据
     */
    @SuppressLint("NotifyDataSetChanged")
    private void handleGetPostList(BaseResponse<RecommendPostResponse> response, Context context, SyncRequestCallback callback) {
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

        callback.onAllRequestSuccess();
    }

    //---------------------------Logic---------------------------

    public PostClickManager postClickManager;

    public void initPostClickManager(ActivityResultCaller fragment){
        postClickManager = new PostClickManager(
                homeVo.postListVo.postAoList,
                this.socketMessageSender,
                fragment
        );
    }

    private final FeatureContext featureContext = new FeatureContext();

    // todo 实现的时候需要再采集更多的数据，如点击时间，点击时长，交给后端的规则集去处理
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
