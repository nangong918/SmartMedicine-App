package com.czy.smartmedicine.activity;


import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.widget.SearchView;

import androidx.lifecycle.ViewModel;

import com.czy.appcore.network.api.handle.SyncRequestCallback;
import com.czy.baseUtilsLib.activity.BaseActivity;
import com.czy.baseUtilsLib.network.networkLoad.NetworkLoadUtils;
import com.czy.baseUtilsLib.ui.ToastUtils;
import com.czy.baseUtilsLib.viewModel.ViewModelUtil;
import com.czy.dal.ao.intent.SearchActivityIntentAo;
import com.czy.dal.constant.SearchEnum;
import com.czy.dal.vo.fragmentActivity.search.SearchPostVo;
import com.czy.dal.vo.fragmentActivity.search.SearchUserVo;
import com.czy.smartmedicine.MainApplication;
import com.czy.smartmedicine.databinding.ActivitySearchBinding;
import com.czy.smartmedicine.viewModel.activity.search.SearchActivityPostViewModel;
import com.czy.smartmedicine.viewModel.activity.search.SearchActivityUserViewModel;
import com.czy.smartmedicine.viewModel.base.ApiViewModelFactory;

import java.util.Optional;

/**
 * 搜索界面：搜索好友 / 搜索帖子
 */
public class SearchActivity extends BaseActivity<ActivitySearchBinding> {

    public SearchActivity() {
        super(SearchActivity.class);
    }

    //----------------------------init----------------------------

    @Override
    protected void init() {
        super.init();

        initIntent();

        initViewModel();

        initView();
    }

    private void initIntent(){
        Intent intent = getIntent();

        this.intentAo = (SearchActivityIntentAo) intent.getSerializableExtra(SearchActivityIntentAo.INTENT_KEY);
        if (intentAo == null){
            Log.e(TAG, "初始化searchActivity失败, intentAo是null");
            ToastUtils.showToast(this, "初始化searchActivity失败");
            finish();
        }
        if (intentAo.searchType == null){
            Log.e(TAG, "初始化searchActivity失败, searchType是null");
            ToastUtils.showToast(this, "初始化searchActivity失败");
            finish();
        }
    }

    private SearchActivityIntentAo intentAo = null;


    //----------------------------view----------------------------

    private void initView(){

        analysisSearchType(intentAo.searchType);

        initViewModelVo();

        initRecyclerView();

        observeData();
    }

    private void analysisSearchType(SearchEnum searchEnum){
        String title = "";

        switch (searchEnum){
            case USER -> {
                title = getString(com.czy.customviewlib.R.string.search_user);
            }
            case GROUP -> {
                title = getString(com.czy.customviewlib.R.string.search_group);
            }
            case POST -> {
                title = getString(com.czy.customviewlib.R.string.search_post);
            }
            case PRODUCTS -> {
                title = getString(com.czy.customviewlib.R.string.search_products);
            }
            case OTHER -> {
                title = getString(com.czy.customviewlib.R.string.search);
            }
        }

        binding.topBar.setTitle(title);
    }

    private void initRecyclerView(){
//        testRecyclerView();
//        adapter = new AddContactAdapter(
//                searchUserVo.addContactListVo.contactItemList,
//                position -> {
//            Log.d(TAG, "position:" + position);
//        });
//        binding.rclvSearch.setAdapter(adapter);
        ((SearchActivityUserViewModel)viewModel).initRecyclerAdapter(
                binding.rclvSearch,
                position -> {
                    Log.d(TAG, "position:" + position);
                }
        );
    }

    private void observeData(){
        // 观察RecyclerView
        // 取消观察list
/*        Optional.ofNullable(searchUserVo)
                .map(vo -> vo.addContactListVo)
                .map(vo -> vo.contactItemList)
                .ifPresent(liveData -> {
                    liveData.observe(this, list -> {
                        Optional.ofNullable(adapter)
                                .map(adapter -> (AddContactAdapter)adapter)
                                .ifPresent(a -> a.setChatItems(list));
                    });
                });*/
    }

    //----------------------------viewModel----------------------------
;
    // 由于此页面是复用页面，所以可能需要多个viewModel适配，所以类型使用的是通用viewModel
    private ViewModel viewModel;

    private void initViewModel(){
        ApiViewModelFactory apiViewModelFactory = new ApiViewModelFactory(MainApplication.getApiRequestImplInstance(), MainApplication.getInstance().getMessageSender());
        switch (intentAo.searchType){
            case USER -> {
                viewModel = ViewModelUtil.newViewModel(this, apiViewModelFactory, SearchActivityUserViewModel.class);

                SearchActivityUserViewModel searchActivityUserVo = (SearchActivityUserViewModel)viewModel;
                searchActivityUserVo.init(new SearchUserVo());
            }
            case GROUP -> {}
            case POST -> {
                viewModel = ViewModelUtil.newViewModel(this, apiViewModelFactory, SearchActivityPostViewModel.class);

                SearchActivityPostViewModel searchActivityPostViewModel = (SearchActivityPostViewModel)viewModel;
                searchActivityPostViewModel.init(new SearchPostVo(), this);
                searchActivityPostViewModel.initRecyclerAdapter(binding.rclvSearch, this);
                searchActivityPostViewModel.initDialogAnswer(this, v -> {
                    // todo 跳转到跟ai聊天的详情页
                });
            }
            case PRODUCTS -> {
            }
        }
    }

    private void initViewModelVo(){
        switch (intentAo.searchType){
            case USER -> {
                SearchActivityUserViewModel searchActivityUserViewModel = (SearchActivityUserViewModel)viewModel;

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
                        SearchActivityUserViewModel searchActivityUserViewModel = (SearchActivityUserViewModel)viewModel;
                        Optional.ofNullable(searchActivityUserViewModel.searchUserVo)
                                        .map(vo -> vo.edtvInputData)
                                        .ifPresent(edtvInputData -> edtvInputData.setValue(newText));
                        return true;
                    }
                });
                // LiveData -> SearchView
                searchActivityUserViewModel.searchUserVo.edtvInputData.observe(this, newText -> {
                    if (newText != null && !newText.equals(binding.searchBar.getQuery().toString())) {
                        binding.searchBar.setQuery(newText, false); // 更新 SearchView 的文本
                    }
                });
            }
            case GROUP -> {}
            case POST -> {
                SearchActivityPostViewModel searchActivityPostViewModel = (SearchActivityPostViewModel)viewModel;

                // 双向绑定
                // SearchView -> LiveData
                binding.searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        SearchActivityPostViewModel searchActivityPostViewModel = (SearchActivityPostViewModel)viewModel;
                        Optional.ofNullable(searchActivityPostViewModel.searchPostVo)
                                .map(vo -> vo.edtvInputData)
                                .ifPresent(edtvInputData -> edtvInputData.setValue(newText));
                        return true;
                    }
                });
                // LiveData -> SearchView
                searchActivityPostViewModel.searchPostVo.edtvInputData.observe(this, newText -> {
                    if (newText != null && !newText.equals(binding.searchBar.getQuery().toString())) {
                        binding.searchBar.setQuery(newText, false); // 更新 SearchView 的文本
                    }
                });
            }
            case PRODUCTS -> {

            }
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
        switch (intentAo.searchType){
            case USER -> {
                SearchActivityUserViewModel searchActivityUserVo = (SearchActivityUserViewModel)viewModel;
                searchActivityUserVo.searchUsers(query);
            }
            case GROUP -> {}
            case POST -> {
                SearchActivityPostViewModel searchActivityPostViewModel = (SearchActivityPostViewModel)viewModel;
                NetworkLoadUtils.showDialog(this);
                searchActivityPostViewModel.searchPosts(this, query, new SyncRequestCallback() {
                    @Override
                    public void onThrowable(Throwable throwable) {
                        NetworkLoadUtils.dismissDialog();
                    }

                    @Override
                    public void onAllRequestSuccess() {
                        NetworkLoadUtils.dismissDialog();
                    }
                });
            }
            case PRODUCTS -> {

            }
        }
    }
}