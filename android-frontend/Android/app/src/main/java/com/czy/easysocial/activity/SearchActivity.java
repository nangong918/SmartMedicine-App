package com.czy.easysocial.activity;


import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.widget.SearchView;

import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.RecyclerView;

import com.czy.baseUtilsLib.activity.BaseActivity;
import com.czy.baseUtilsLib.ui.ToastUtils;
import com.czy.baseUtilsLib.viewModel.ViewModelUtil;
import com.czy.customviewlib.view.addContact.AddContactAdapter;
import com.czy.dal.constant.SearchEnum;
import com.czy.dal.constant.newUserGroup.ApplyStatusEnum;
import com.czy.dal.dto.netty.request.AddUserRequest;
import com.czy.dal.vo.entity.addContact.AddContactItemVo;
import com.czy.dal.vo.entity.addContact.AddContactListVo;
import com.czy.dal.vo.viewModeVo.search.SearchActivityUserVo;
import com.czy.easysocial.MainApplication;
import com.czy.easysocial.databinding.ActivitySearchBinding;
import com.czy.easysocial.viewModel.ApiViewModelFactory;
import com.czy.easysocial.viewModel.SearchActivityUserViewModel;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class SearchActivity extends BaseActivity<ActivitySearchBinding> {

    public SearchActivity() {
        super(SearchActivity.class);
    }

    //----------------------------init----------------------------

    @Override
    protected void init() {
        super.init();
        initView();
        initViewModel();
    }

    private SearchEnum searchEnum;

    //----------------------------view----------------------------

    private void initView(){
        Intent intent = getIntent();

        this.searchEnum = Optional.ofNullable(intent.getSerializableExtra(SearchEnum.INTENT_EXTRA_NAME))
                .map(o -> (SearchEnum) o)
                .orElse(SearchEnum.OTHER);

        analysisSearchType(searchEnum);

        initViewModelVo();

        initRecyclerView();

        observeData();
    }

    private void analysisSearchType(SearchEnum searchEnum){
        String title = "";

        switch (searchEnum){
            case USER -> {
                title = "搜索用户";
            }
            case GROUP -> {
                title = "搜索群组";
            }
            case ARTICLE -> {
                title = "搜索文章";
            }
            case VIDEO -> {
                title = "搜索视频";
            }
            case MUSIC -> {
                title = "搜索音乐";
            }
            case PHOTO -> {
                title = "搜索图片";
            }
            case DOCUMENT -> {
                title = "搜索文档";
            }
            case OTHER -> {
                title = "搜索";
            }
        }

        binding.topBar.setTitle(title);
    }

    RecyclerView.Adapter<RecyclerView.ViewHolder> adapter;
    private void initRecyclerView(){
//        testRecyclerView();
        adapter = new AddContactAdapter(
                searchActivityUserVo.addContactListVo.contactItemList.getValue(),
                position -> {
            Log.d(TAG, "position:" + position);
        });
        binding.rclvSearch.setAdapter(adapter);
    }

    private void observeData(){
        // 观察RecyclerView
        Optional.ofNullable(searchActivityUserVo)
                .map(vo -> vo.addContactListVo)
                .map(vo -> vo.contactItemList)
                .ifPresent(liveData -> {
                    liveData.observe(this, list -> {
                        Optional.ofNullable(adapter)
                                .map(adapter -> (AddContactAdapter)adapter)
                                .ifPresent(a -> a.setChatItems(list));
                    });
                });
    }

    //----------------------------viewModel----------------------------

    private ViewModel viewModel;

    private void initViewModel(){
        ApiViewModelFactory apiViewModelFactory = new ApiViewModelFactory(MainApplication.getApiRequestImplInstance(), MainApplication.getInstance().getMessageSender());
        switch (searchEnum){
            case USER -> {
                viewModel = ViewModelUtil.newViewModel(this, apiViewModelFactory, SearchActivityUserViewModel.class);
                ((SearchActivityUserViewModel)viewModel).init(searchActivityUserVo);
            }
            case GROUP -> {}
        }
    }
    private SearchActivityUserVo searchActivityUserVo;
    private void initViewModelVo(){
        switch (searchEnum){
            case USER -> {
                searchActivityUserVo = new SearchActivityUserVo();

                // 双向绑定
                // SearchView -> LiveData
                binding.searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        // 更新 LiveData 数据
                        Optional.of(searchActivityUserVo)
                                .map(vo -> vo.edtvInputData)
                                .ifPresent(edtvInputData -> edtvInputData.setValue(newText));
                        return true;
                    }
                });
                // LiveData -> SearchView
                searchActivityUserVo.edtvInputData.observe(this, newText -> {
                    if (newText != null && !newText.equals(binding.searchBar.getQuery().toString())) {
                        binding.searchBar.setQuery(newText, false); // 更新 SearchView 的文本
                    }
                });
            }
            case GROUP -> {}
        }
    }

    //----------------------------listener----------------------------

    @Override
    protected void setListener() {
        super.setListener();

        binding.topBar.setOnClickListener(v -> finish());

        binding.btnSearch.setOnClickListener(v -> searchInfo());
    }

    // vo -> user;article
    private void searchInfo(){
        String query = binding.searchBar.getQuery().toString();
        if (TextUtils.isEmpty(query)){
            ToastUtils.showToastActivity(this, "请输入搜索内容");
        }
        switch (searchEnum){
            case USER -> {
                ((SearchActivityUserViewModel)viewModel).searchUsers(query);
            }
            case GROUP -> {}
        }
    }
}