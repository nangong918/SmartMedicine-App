package com.czy.smartmedicine.viewModel.fragment.home

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.activity.result.ActivityResultCaller
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.czy.appcore.network.api.handle.SyncRequestCallback
import com.czy.appcore.network.netty.api.send.SocketMessageSender
import com.czy.appview.view.home.OnRecommendCardClick
import com.czy.appview.view.home.PostHomeAdapter
import com.czy.baseutil.network.BaseResponse
import com.czy.baseutil.network.networkLoad.NetworkLoadUtils
import com.czy.dao.networkRepository.ApiRequestImpl
import com.czy.domain.ao.chat.UserLoginInfoAo
import com.czy.domain.constant.NettyConstants
import com.czy.domain.dto.http.request.RecommendPostRequest
import com.czy.domain.dto.http.response.RecommendPostResponse
import com.czy.smartmedicine.MainApplication
import com.czy.smartmedicine.fragment.home.HomeFragment
import com.czy.smartmedicine.manager.HttpRequestManager
import com.czy.smartmedicine.manager.PostClickManager
import com.czy.smartmedicine.test.TestConfig
import com.czy.smartmedicine.utils.ResponseTool
import com.czy.smartmedicine.utils.ViewModelUtil
import java.util.Optional

open class RecommendVm(
    private val apiRequestImpl: ApiRequestImpl,
    private val socketMessageSender: SocketMessageSender
) : ViewModel(){

    companion object {
        val TAG: String = RecommendVm::class.java.name
    }

    //==========RecyclerView

    open lateinit var postHomeAdapter: PostHomeAdapter

    fun initRecyclerView(recyclerView: RecyclerView, activity: FragmentActivity?) {
        val onRecommendCardClick: OnRecommendCardClick =
            postClickManager.getOnRecommendCardClick(activity)

        // adapter的地址指针指向数据仓库的recommendList
        postHomeAdapter = PostHomeAdapter(
            MainApplication.getInstance().postDataManager.recommendPosts,
            onRecommendCardClick
        )

        recyclerView.adapter = postHomeAdapter
    }

    //---------------------------NetWork---------------------------
;
    // 初始化网络请求 todo 管理缓存，图片也要缓存在内存，避免重复网络请求
    fun initialNetworkRequest(context: Context, callback: SyncRequestCallback) {
        // app首次打开HomeFragment时，请求推荐帖子
        if (HttpRequestManager.getIsFirstOpen(HomeFragment::class.java.name)) {
            NetworkLoadUtils.showDialog(context)
            getRecommendPostsP(context, callback)
        }
        // 不是首次打开管都不用管
    }

    private fun getRecommendPostsP(context: Context, callback: SyncRequestCallback) {
        if (!TestConfig.IS_TEST) {
            getRecommendPosts(context, callback)
        } else {
            testGetRandomPosts(context, callback)
        }
    }


    // 获取推荐帖子
    private fun getRecommendPosts(context: Context, callback: SyncRequestCallback) {
        val request = RecommendPostRequest().apply {
            userId = Optional.ofNullable(MainApplication.getInstance().userLoginInfoAo)
                .map { ao: UserLoginInfoAo -> ao.userId }
                .orElse(NettyConstants.ERROR_ID)
        }
        apiRequestImpl.getRecommendPosts(
            request,
            { response ->
                ResponseTool.handleSyncResponseEx(
                    response,
                    context,
                    callback,
                    this::handleGetPostList
                )
            },
            { throwable ->
                callback.onThrowable(throwable)
                ViewModelUtil.globalThrowableToast(throwable)
            }
        )
    }

    // 前后端联调测试接口：获取随机推荐帖子
    private fun testGetRandomPosts(context: Context, callback: SyncRequestCallback) {
        val request = RecommendPostRequest().apply {
            userId = Optional.ofNullable(MainApplication.getInstance().userLoginInfoAo)
                .map { ao: UserLoginInfoAo -> ao.userId }
                .orElse(NettyConstants.ERROR_ID)
        }
        apiRequestImpl.recommendTestGetRandomPost(
            request,
            { response ->
                ResponseTool.handleSyncResponseEx(
                    response,
                    context,
                    callback,
                    this::handleGetPostList
                )
            },
            { throwable ->
                callback.onThrowable(throwable)
                ViewModelUtil.globalThrowableToast(throwable)
            }
        )
    }

    /**
     * 处理获取帖子列表
     * @param response  接口返回的数据
     */
    @SuppressLint("NotifyDataSetChanged")
    private fun handleGetPostList(
        response: BaseResponse<RecommendPostResponse>,
        context: Context,
        callback: SyncRequestCallback
    ) {
        val postInfoAos = Optional.ofNullable(response)
            .map { obj: BaseResponse<RecommendPostResponse> -> obj.data }
            .map { obj: RecommendPostResponse -> obj.getPostVos() }
            .orElse(ArrayList())

        if (postInfoAos.isEmpty()) {
            return
        }

        // postInfoUrlAo -> PostAo
        val newPostAoList = postClickManager.getPostAoListByResponse(postInfoAos)

        // homeList原先存在的列表
        val recommendPost = MainApplication.getInstance().postDataManager.recommendPosts

        if (recommendPost == null) {
            Log.w(TAG, "PostDataManager中的数据为空")
            return
        }

        val beforeSize = recommendPost.size
        recommendPost.addAll(newPostAoList)

        Log.i(TAG, "推荐数据检查：[原数据：$beforeSize] " +
                "[处理前 新数据：${postInfoAos.size}] " +
                "[处理后 新数据：${newPostAoList.size}] " +
                "[总数据：${recommendPost.size}]"
        )

        // adapter更新；注意此处RecyclerViewAdapter的更新逻辑跟其他地方的Adapter更新逻辑不一样，是直接由指针指向地址去更新
        for (i in beforeSize until recommendPost.size) {
            postHomeAdapter.notifyItemInserted(i)
        }

        callback.onAllRequestSuccess()
    }

    //---------------------------Logic---------------------------

    private lateinit var postClickManager: PostClickManager

    fun initPostClickManager(fragment: ActivityResultCaller) {
        postClickManager = PostClickManager(
            // postClickManager的指针指向 postDataManager的recommendList
            MainApplication.getInstance().postDataManager.recommendPosts,
            fragment
        )
    }
}