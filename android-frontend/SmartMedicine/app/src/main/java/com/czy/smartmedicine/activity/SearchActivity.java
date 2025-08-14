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
import com.czy.smartmedicine.viewModel.activity.search.SearchPostVm;
import com.czy.smartmedicine.viewModel.activity.search.SearchUserVm;
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
    public ActivitySearchBinding getBinding() {
        return ActivitySearchBinding.inflate(getLayoutInflater());
    }

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
        ((SearchUserVm) vm).initRecyclerAdapter(
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
    private ViewModel vm;

    private void initViewModel(){
        ApiViewModelFactory apiViewModelFactory = new ApiViewModelFactory(MainApplication.getApiRequestImplInstance(), MainApplication.getInstance().getMessageSender());
        switch (intentAo.searchType){
            case USER -> {
                vm = ViewModelUtil.newViewModel(this, apiViewModelFactory, SearchUserVm.class);

                SearchUserVm searchActivityUserVo = (SearchUserVm) vm;
                searchActivityUserVo.init(new SearchUserVo());
            }
            case GROUP -> {}
            case POST -> {
                vm = ViewModelUtil.newViewModel(this, apiViewModelFactory, SearchPostVm.class);

                SearchPostVm searchPostVm = (SearchPostVm) vm;
                searchPostVm.init(new SearchPostVo(), this);
                searchPostVm.initRecyclerAdapter(binding.rclvSearch, this);
                searchPostVm.initDialogAnswer(this, v -> {
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
                SearchUserVm searchUserVm = (SearchUserVm) vm;

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
                        SearchUserVm searchUserVm = (SearchUserVm) vm;
                        Optional.ofNullable(searchUserVm.searchUserVo)
                                        .map(vo -> vo.edtvInputData)
                                        .ifPresent(edtvInputData -> edtvInputData.setValue(newText));
                        return true;
                    }
                });
                // LiveData -> SearchView
                searchUserVm.searchUserVo.edtvInputData.observe(this, newText -> {
                    if (newText != null && !newText.equals(binding.searchBar.getQuery().toString())) {
                        binding.searchBar.setQuery(newText, false); // 更新 SearchView 的文本
                    }
                });
            }
            case GROUP -> {}
            case POST -> {
                SearchPostVm searchPostVm = (SearchPostVm) vm;

                // 双向绑定
                // SearchView -> LiveData
                binding.searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        SearchPostVm searchPostVm = (SearchPostVm) vm;
                        Optional.ofNullable(searchPostVm.searchPostVo)
                                .map(vo -> vo.edtvInputData)
                                .ifPresent(edtvInputData -> edtvInputData.setValue(newText));
                        return true;
                    }
                });
                // LiveData -> SearchView
                searchPostVm.searchPostVo.edtvInputData.observe(this, newText -> {
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
                SearchUserVm searchActivityUserVo = (SearchUserVm) vm;
                searchActivityUserVo.searchUsers(query);
            }
            case GROUP -> {}
            case POST -> {
                SearchPostVm searchPostVm = (SearchPostVm) vm;
                NetworkLoadUtils.showDialog(this);
                searchPostVm.searchPosts(this, query, new SyncRequestCallback() {
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