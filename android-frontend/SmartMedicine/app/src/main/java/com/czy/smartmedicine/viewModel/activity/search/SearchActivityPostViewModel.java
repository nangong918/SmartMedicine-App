package com.czy.smartmedicine.viewModel.activity.search;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.RecyclerView;

import com.czy.appcore.BaseConfig;
import com.czy.appcore.network.api.handle.SyncRequestCallback;
import com.czy.appcore.network.netty.api.send.SocketMessageSender;
import com.czy.baseUtilsLib.network.BaseResponse;
import com.czy.baseUtilsLib.ui.ToastUtils;
import com.czy.customviewlib.view.DialogAnswer;
import com.czy.customviewlib.view.home.OnRecommendCardClick;
import com.czy.customviewlib.view.home.PostAdapter;
import com.czy.dal.ao.chat.UserLoginInfoAo;
import com.czy.dal.ao.home.PostAo;
import com.czy.dal.ao.search.AppFunctionAo;
import com.czy.dal.ao.search.PersonalEvaluateAo;
import com.czy.dal.ao.search.PostRecommendAo;
import com.czy.dal.ao.search.PostSearchResultAo;
import com.czy.dal.ao.search.QuestionAo;
import com.czy.dal.constant.Constants;
import com.czy.dal.constant.search.FuzzySearchResponseEnum;
import com.czy.dal.dto.http.request.FuzzySearchRequest;
import com.czy.dal.dto.http.response.FuzzySearchResponse;
import com.czy.dal.vo.fragmentActivity.search.SearchPostVo;
import com.czy.datalib.networkRepository.ApiRequestImpl;
import com.czy.smartmedicine.MainApplication;
import com.czy.smartmedicine.manager.PostClickManager;
import com.czy.smartmedicine.utils.ResponseTool;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SearchActivityPostViewModel extends ViewModel {

    private static final String TAG = SearchActivityPostViewModel.class.getName();

    private final ApiRequestImpl apiRequestImpl;
    private final SocketMessageSender socketMessageSender;

    public SearchActivityPostViewModel(ApiRequestImpl apiRequestImpl, SocketMessageSender socketMessageSender){
        this.apiRequestImpl = apiRequestImpl;
        this.socketMessageSender = socketMessageSender;
    }

    public PostClickManager postClickManager;

    public void init(SearchPostVo searchPostVo, FragmentActivity activity){
        this.searchPostVo = searchPostVo;
        initPostClickManager(activity);
    }

    private void initPostClickManager(FragmentActivity activity){
        postClickManager = new PostClickManager(
                this.searchPostVo.postAoList,
                this.socketMessageSender,
                activity
        );
    }

    //---------------------------Vo Ld---------------------------

    public SearchPostVo searchPostVo = new SearchPostVo();

    public PostAdapter postAdapter;

    public DialogAnswer dialogAnswer;

    public void initRecyclerAdapter(RecyclerView recyclerView, FragmentActivity activity){
        List<PostAo> postAoList = Optional.ofNullable(searchPostVo)
                .map(vo -> vo.postAoList)
                .orElse(new ArrayList<>());

        OnRecommendCardClick onRecommendCardClick = postClickManager.getOnRecommendCardClick(activity);

        postAdapter = new PostAdapter(
                postAoList,
                onRecommendCardClick
        );

        recyclerView.setAdapter(postAdapter);
    }

    public void initDialogAnswer(FragmentActivity activity, View.OnClickListener onViewDetailsClickListener){
        dialogAnswer = new DialogAnswer(activity);

        dialogAnswer.setViewDetailsClickListener(onViewDetailsClickListener);
    }

    public void searchPosts(Context context, @NonNull String sentence, SyncRequestCallback callback) {
        UserLoginInfoAo userLoginInfoAo = MainApplication.getInstance().getUserLoginInfoAo();
        Long userId = Optional.ofNullable(userLoginInfoAo)
                .map(UserLoginInfoAo::getUserId)
                .orElse(Constants.ERROR_ID);
        if (userId.equals(Constants.ERROR_ID)){
            ToastUtils.showToast(context, "请先登录");
            return;
        }
        if (sentence.length() < BaseConfig.SEARCH_FIELD_MIN_LENGTH){
            String message = "请输入至少" + BaseConfig.SEARCH_FIELD_MIN_LENGTH + "个字符";
            ToastUtils.showToast(context, message);
            return;
        }
        if (sentence.length() > BaseConfig.SEARCH_FIELD_MAX_LENGTH){
            String message = "请输入不超过" + BaseConfig.SEARCH_FIELD_MAX_LENGTH + "个字符";
            ToastUtils.showToast(context, message);
            return;
        }
        FuzzySearchRequest request = new FuzzySearchRequest();
        request.sentence = sentence;

        apiRequestImpl.fuzzySearch(request,
                response -> {
                    ResponseTool.handleSyncResponseEx(
                            response,
                            context,
                            callback,
                            sentence,
                            this::handleSearchPosts
                    );
                },
                callback::onThrowable
                );
    }

    @SuppressLint("NotifyDataSetChanged")
    private void handleSearchPosts(BaseResponse<FuzzySearchResponse> response, Context context, SyncRequestCallback callback, Object param) {
        Integer fuzzySearchType = Optional.ofNullable(response)
                .map(BaseResponse::getData)
                .map(data -> data.type)
                .orElse(FuzzySearchResponseEnum.NO_RESULT.getType());

        Object data = Optional.ofNullable(response)
                .map(BaseResponse::getData)
                .map(d -> d.data)
                .orElse(null);

        String question = Optional.ofNullable(param)
                .map(p -> (String) p)
                .orElse("");

        if (data == null){
            callback.onAllRequestSuccess();
            ToastUtils.showToast(context, "没有搜索结果");
            return;
        }
        FuzzySearchResponseEnum enumType = FuzzySearchResponseEnum.getByType(fuzzySearchType);

        switch (enumType){
            case ERROR_RESULT -> {
                // String 返回错误信息
                String errorMessage = (String) data;
                ToastUtils.showToast(context, errorMessage);
            }
            case NO_RESULT -> {
                ToastUtils.showToast(context, "没有搜索结果");
            }
            case NOT_NATURAL_LANGUAGE_RESULT, TALK_RESULT -> {
                String answer = (String) data;
                dialogAnswer.setContent(question, answer);
                dialogAnswer.show();
            }
            case SEARCH_POST_RESULT -> {
                PostSearchResultAo ao = (PostSearchResultAo) data;
                searchPostVo.postAoList.clear();
                List<PostAo> likePostAoList = postClickManager.getPostAoByPostVo(ao.likePostPreviewVoList);
                List<PostAo> tokenizedPostAoList = postClickManager.getPostAoByPostVo(ao.tokenizedPostPreviewVoList);
                List<PostAo> similarPostAoList = postClickManager.getPostAoByPostVo(ao.similarPostPreviewVoList);
                List<PostAo> recommendPostAoList = postClickManager.getPostAoByPostVo(ao.recommendPostPreviewVoList);

                searchPostVo.postAoList.addAll(likePostAoList);
                searchPostVo.postAoList.addAll(tokenizedPostAoList);
                searchPostVo.postAoList.addAll(similarPostAoList);
                searchPostVo.postAoList.addAll(recommendPostAoList);

                // ui通知items变化
                postAdapter.notifyDataSetChanged();
            }
            case QUESTION_RESULT -> {
                QuestionAo ao = (QuestionAo) data;
            }
            case RECOMMEND_QUESTION_RESULT -> {
                PostRecommendAo ao = (PostRecommendAo) data;
            }
            case APP_FUNCTION_RESULT -> {
                AppFunctionAo ao = (AppFunctionAo) data;
            }
            case PERSONAL_QUESTION_RESULT -> {
                PersonalEvaluateAo ao = (PersonalEvaluateAo) data;
            }
        }
        callback.onAllRequestSuccess();
    }
}
