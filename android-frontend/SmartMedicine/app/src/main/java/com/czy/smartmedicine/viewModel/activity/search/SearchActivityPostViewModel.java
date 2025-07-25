package com.czy.smartmedicine.viewModel.activity.search;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.RecyclerView;

import com.czy.appcore.network.netty.api.send.SocketMessageSender;
import com.czy.customviewlib.view.home.OnRecommendCardClick;
import com.czy.customviewlib.view.home.PostAdapter;
import com.czy.dal.ao.home.PostAo;
import com.czy.dal.vo.fragmentActivity.search.SearchPostVo;
import com.czy.datalib.networkRepository.ApiRequestImpl;
import com.czy.smartmedicine.manager.PostClickManager;

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

}
