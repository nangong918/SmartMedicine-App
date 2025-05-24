package com.czy.smartmedicine.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.czy.baseUtilsLib.activity.BaseFragment;
import com.czy.baseUtilsLib.viewModel.ViewModelUtil;
import com.czy.customviewlib.view.home.OnClickArticleCardCallBack;
import com.czy.customviewlib.view.home.PostAdapter;
import com.czy.dal.OnPositionItemClick;
import com.czy.dal.ao.home.PostAo;
import com.czy.dal.vo.entity.home.PostListVo;
import com.czy.smartmedicine.MainApplication;
import com.czy.smartmedicine.databinding.FragmentHomeBinding;
import com.czy.smartmedicine.viewModel.ApiViewModelFactory;
import com.czy.smartmedicine.viewModel.HomeViewModel;

import java.util.ArrayList;
import java.util.List;
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

        OnPositionItemClick onPositionItemClick = position -> {

        };

        OnClickArticleCardCallBack onClickArticleCardCallBack = new OnClickArticleCardCallBack() {
            @Override
            public void onCardClickListener(int position, int cardType, int cardId, boolean[] feedbackButton) {

            }

            @Override
            public void onButtonClickListener(int position, int cardType, int cardId, int buttonType, boolean[] feedbackButton) {

            }
        };

        PostAdapter adapter = new PostAdapter(postListVo.postAoListLd.getValue(), onPositionItemClick, onClickArticleCardCallBack);

        binding.rclvRecommend.setAdapter(adapter);
    }

}