package com.czy.smartmedicine.viewModel.activity.search;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
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
import com.czy.appview.view.DialogAnswer;
import com.czy.appview.view.search.post.OnPostClick;
import com.czy.appview.view.search.post.PostSearchAdapter;
import com.czy.domain.ao.chat.UserLoginInfoAo;
import com.czy.domain.ao.home.PostInfoUrlAo;
import com.czy.domain.ao.search.AppFunctionAo;
import com.czy.domain.ao.search.PersonalEvaluateAo;
import com.czy.domain.ao.search.PostRecommendAo;
import com.czy.domain.ao.search.PostSearchResultAo;
import com.czy.domain.ao.search.QuestionAo;
import com.czy.domain.constant.NettyConstants;
import com.czy.domain.constant.search.FuzzySearchResponseEnum;
import com.czy.domain.constant.search.PersonalResultIntent;
import com.czy.domain.constant.search.PostSearchResultListEnum;
import com.czy.domain.dto.http.request.FuzzySearchRequest;
import com.czy.domain.dto.http.response.FuzzySearchResponse;
import com.czy.domain.fragmentActivityAo.search.SearchPostAAo;
import com.czy.domain.vo.entity.home.PostExVo;
import com.czy.domain.vo.entity.home.PostVo;
import com.czy.datalib.networkRepository.ApiRequestImpl;
import com.czy.smartmedicine.MainApplication;
import com.czy.smartmedicine.utils.ResponseTool;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SearchPostVm extends ViewModel {

    private static final String TAG = SearchPostVm.class.getName();

    private final ApiRequestImpl apiRequestImpl;
    private final SocketMessageSender socketMessageSender;

    public SearchPostVm(ApiRequestImpl apiRequestImpl, SocketMessageSender socketMessageSender){
        this.apiRequestImpl = apiRequestImpl;
        this.socketMessageSender = socketMessageSender;
    }

    public void init(SearchPostAAo searchPostAAo, FragmentActivity activity){
        this.searchPostAAo = searchPostAAo;
    }

    //---------------------------Vo Ld---------------------------

    public SearchPostAAo searchPostAAo = new SearchPostAAo();

    public PostSearchAdapter adapter;

    public DialogAnswer dialogAnswer;

    public void initRecyclerAdapter(RecyclerView recyclerView, OnPostClick onPostClick){

        adapter = new PostSearchAdapter(
                searchPostAAo.postExVoList,
                onPostClick
        );

        recyclerView.setAdapter(adapter);
    }

    public void initDialogAnswer(FragmentActivity activity, View.OnClickListener onViewDetailsClickListener){
        dialogAnswer = new DialogAnswer(activity);

        dialogAnswer.setViewDetailsClickListener(onViewDetailsClickListener);
    }

    public void searchPosts(Context context, @NonNull String sentence, SyncRequestCallback callback) {
        UserLoginInfoAo userLoginInfoAo = MainApplication.getInstance().getUserLoginInfoAo();
        Long userId = Optional.ofNullable(userLoginInfoAo)
                .map(UserLoginInfoAo::getUserId)
                .orElse(NettyConstants.ERROR_ID);
        if (userId.equals(NettyConstants.ERROR_ID)){
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
        request.userId = userId;

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
    private void handleSearchPosts(BaseResponse<FuzzySearchResponse> response, Context context,
                                   SyncRequestCallback callback, Object param) {
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

        // 清理数据
        searchPostAAo.postExVoList.clear();

        if (data == null){
            callback.onAllRequestSuccess();
            ToastUtils.showToast(context, "没有搜索结果");

            // 更新空数据
            adapter.notifyDataSetChanged();
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
                String answer;

                try {
                    answer = data.toString();
                } catch (Exception e){
                    answer = "";
                    Log.e(TAG, "模糊搜索::error[类型gson转换错误] enumType：" + enumType, e);
                }

                dialogAnswer.setContent(question, answer);
                dialogAnswer.show();
            }
            case SEARCH_POST_RESULT -> {
                try {
                    // 将 Object 类型的 data 转换为 JSON 字符串 (不能使用data.toString(), 因为Java的toString是=而不是:)
                    String jsonString = MainApplication.getGson().toJson(data);
                    // 解析 JSON 字符串为 PostSearchResultAo 对象
                    PostSearchResultAo ao = MainApplication.getGson().fromJson(jsonString, PostSearchResultAo.class);


                    // 搜索
                    searchPostAAo.postExVoList.addAll(ao.getPostExVoList());
                } catch (Exception e) {
                    Log.e(TAG, "模糊搜索::error[类型fastJson转换错误] enumType：" + enumType, e);
                }
            }
            case QUESTION_RESULT -> {
                try {
                    String jsonString = MainApplication.getGson().toJson(data);
                    QuestionAo ao = MainApplication.getGson().fromJson(
                            jsonString, QuestionAo.class
                    );
                    PostSearchResultAo postSearchResultAo = ao.postSearchResultAo;

                    // 问题
                    if (postSearchResultAo != null){
                        List<PostExVo> postExVoList = postSearchResultAo.getPostExVoList();
                        searchPostAAo.postExVoList.addAll(postExVoList);
                    }

                    String answer = Optional.ofNullable(ao.diseaseQuestionAo)
                            .map(dao -> dao.answer)
                            .orElse("");
                    if (!TextUtils.isEmpty(answer)){
                        dialogAnswer.setContent(question, answer);
                        dialogAnswer.show();
                    }
                } catch (Exception e){
                    Log.e(TAG, "模糊搜索::error[类型gson转换错误] enumType：" + enumType, e);
                }
            }
            case RECOMMEND_QUESTION_RESULT -> {
                try {
                    String jsonString = MainApplication.getGson().toJson(data);
                    PostRecommendAo ao = MainApplication.getGson().fromJson(
                            jsonString, PostRecommendAo.class
                    );
                    List<PostInfoUrlAo> postInfoUrlAos = ao.postInfoUrlAos;
                    List<PostExVo> recommendPostVoList = new ArrayList<>();
                    // 转换
                    for (PostInfoUrlAo postInfoUrlAo : postInfoUrlAos){
                        PostVo vo = PostVo.getRecommendPostVoFromPostInfoUrlAo(postInfoUrlAo);
                        PostExVo exVo = new PostExVo();
                        exVo.setByPostVo(vo);
                        exVo.type = PostSearchResultListEnum.RECOMMEND_MATCH_RESULT.getValue();
                        recommendPostVoList.add(exVo);
                    }

                    // 推荐
                    if (!recommendPostVoList.isEmpty()){
                        searchPostAAo.postExVoList.addAll(recommendPostVoList);
                    }
                } catch (Exception e){
                    Log.e(TAG, "模糊搜索::error[类型gson转换错误] enumType：" + enumType, e);
                }
            }
            case APP_FUNCTION_RESULT -> {
                try {
                    String jsonString = MainApplication.getGson().toJson(data);
                    AppFunctionAo ao = MainApplication.getGson().fromJson(
                            jsonString, AppFunctionAo.class
                    );
                    // 暂未开发
                    ToastUtils.showToast(context, ao.message);
                } catch (Exception e){
                    Log.e(TAG, "模糊搜索::error[类型gson转换错误] enumType：" + enumType, e);
                }
            }
            case PERSONAL_QUESTION_RESULT -> {
                try {
                    String jsonString = MainApplication.getGson().toJson(data);
                    PersonalEvaluateAo ao = MainApplication.getGson().fromJson(
                            jsonString, PersonalEvaluateAo.class
                    );
                    PersonalResultIntent intentType = PersonalResultIntent.getByType(ao.intent);
                    Double heartDisease = Optional.ofNullable(ao.heartDisease)
                            .orElse(0.0) * 100.0;
                    Double diabetes = Optional.ofNullable(ao.diabetes)
                            .orElse(0.0) * 100.0;
                    @SuppressLint("DefaultLocale") String message = String.format("%s \n %s: %.2f%%\n %s: %.2f%%",
                            intentType.getName(),
                            context.getString(com.czy.appview.R.string.possible_heart_disease),
                            heartDisease,
                            context.getString(com.czy.appview.R.string.possible_diabetes),
                            diabetes
                    );
                    ToastUtils.showToast(context, message);
                } catch (Exception e){
                    Log.e(TAG, "模糊搜索::error[类型gson转换错误] enumType：" + enumType, e);
                }
            }
        }

        // ui通知items变化
        adapter.notifyDataSetChanged();

        callback.onAllRequestSuccess();
    }
}
