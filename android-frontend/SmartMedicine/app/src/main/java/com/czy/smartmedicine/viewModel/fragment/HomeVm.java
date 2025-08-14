package com.czy.smartmedicine.viewModel.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import androidx.activity.result.ActivityResultCaller;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.RecyclerView;

import com.czy.appcore.network.api.handle.SyncRequestCallback;
import com.czy.appcore.network.netty.api.send.SocketMessageSender;
import com.czy.baseUtilsLib.network.BaseResponse;
import com.czy.customviewlib.view.home.OnRecommendCardClick;
import com.czy.customviewlib.view.home.PostAdapter;
import com.czy.dal.ao.home.PostAo;
import com.czy.dal.ao.home.PostInfoUrlAo;
import com.czy.dal.constant.NettyConstants;
import com.czy.dal.dto.http.request.RecommendPostRequest;
import com.czy.dal.dto.http.response.RecommendPostResponse;
import com.czy.dal.vo.fragmentActivity.HomeVo;
import com.czy.datalib.networkRepository.ApiRequestImpl;
import com.czy.smartmedicine.MainApplication;
import com.czy.smartmedicine.fragment.HomeFragment;
import com.czy.smartmedicine.manager.HttpRequestManager;
import com.czy.smartmedicine.manager.PostClickManager;
import com.czy.smartmedicine.test.TestConfig;
import com.czy.smartmedicine.utils.ResponseTool;
import com.czy.smartmedicine.utils.ViewModelUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class HomeVm extends ViewModel {

    private static final String TAG = HomeVm.class.getName();

    private final ApiRequestImpl apiRequestImpl;
    private final SocketMessageSender socketMessageSender;

    public HomeVm(ApiRequestImpl apiRequestImpl, SocketMessageSender socketMessageSender) {
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
    }

    //==========RecyclerView

    public void initRecyclerView(RecyclerView recyclerView, FragmentActivity activity){
        OnRecommendCardClick onRecommendCardClick = postClickManager.getOnRecommendCardClick(activity);

        // adapter的地址指针指向数据仓库
        postAdapter = new PostAdapter(
                MainApplication.getInstance().getPostDataManager().recommendPosts,
                onRecommendCardClick
        );

        recyclerView.setAdapter(postAdapter);
    }

    //---------------------------NetWork---------------------------

    // 初始化网络请求 todo 管理缓存，图片也要缓存在内存，避免重复网络请求
    public void initialNetworkRequest(Context context, SyncRequestCallback callback) {
        // app首次打开HomeFragment时，请求推荐帖子
        if (HttpRequestManager.getIsFirstOpen(HomeFragment.class.getName())){
            getRecommendPostsP(context, callback);
        }
        // 不是首次打开管都不用管
    }

    public void getRecommendPostsP(Context context, SyncRequestCallback callback){
        if (!TestConfig.IS_TEST){
            getRecommendPosts(context, callback);
        }
        else {
            testGetRandomPosts(context, callback);
        }
    }

    // 获取推荐帖子
    private void getRecommendPosts(Context context, SyncRequestCallback callback){
        RecommendPostRequest request = new RecommendPostRequest();
        request.userId = Optional.ofNullable(MainApplication.getInstance().getUserLoginInfoAo())
                .map(ao -> ao.userId)
                .orElse(NettyConstants.ERROR_ID);
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

    // 前后端联调测试接口：获取随机推荐帖子
    private void testGetRandomPosts(Context context, SyncRequestCallback callback){
        RecommendPostRequest request = new RecommendPostRequest();
        request.userId = Optional.ofNullable(MainApplication.getInstance().getUserLoginInfoAo())
                .map(ao -> ao.userId)
                .orElse(NettyConstants.ERROR_ID);
        apiRequestImpl.recommendTestGetRandomPost(
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
        List<PostAo> newPostAoList = this.postClickManager.getPostAoListByResponse(postInfoAos);

        // homeList原先存在的列表
        List<PostAo> recommendPost = MainApplication.getInstance().getPostDataManager().recommendPosts;

        if (recommendPost == null){
            Log.w(TAG, "PostDataManager中的数据为空");
            return;
        }

        int beforeSize = recommendPost.size();
        recommendPost.addAll(newPostAoList);

        Log.i(TAG, "推荐数据检查：[原数据：" + beforeSize + "] " +
                " [处理前 新数据：" + postInfoAos.size() + "] " +
                " [处理后 新数据：" + newPostAoList.size() + "] " +
                " [总数据：" + recommendPost.size() + "]");

        // adapter更新；注意此处RecyclerViewAdapter的更新逻辑跟其他地方的Adapter更新逻辑不一样，是直接由指针指向地址去更新
        for (int i = beforeSize; i < recommendPost.size(); i++) {
            postAdapter.notifyItemInserted(i);
        }

        callback.onAllRequestSuccess();
    }

    //---------------------------Logic---------------------------

    public PostClickManager postClickManager;

    public void initPostClickManager(ActivityResultCaller fragment){
        postClickManager = new PostClickManager(
                MainApplication.getInstance().getPostDataManager().recommendPosts,
                fragment
        );
    }
}
