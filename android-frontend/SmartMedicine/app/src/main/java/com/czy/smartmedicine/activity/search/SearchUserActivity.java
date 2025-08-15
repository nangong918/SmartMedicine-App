package com.czy.smartmedicine.activity.search;


import android.text.TextUtils;
import android.util.Log;
import android.widget.SearchView;

import com.czy.baseUtilsLib.activity.BaseActivity;
import com.czy.baseUtilsLib.ui.ToastUtils;
import com.czy.baseUtilsLib.viewModel.ViewModelUtil;
import com.czy.dal.fragmentActivityAo.search.SearchUserVo;
import com.czy.smartmedicine.MainApplication;
import com.czy.smartmedicine.databinding.ActivitySearchBaseBinding;
import com.czy.smartmedicine.viewModel.activity.search.SearchUserVm;
import com.czy.smartmedicine.viewModel.base.ApiViewModelFactory;

import java.util.Optional;

/**
 * 搜索界面：搜索好友 / 搜索帖子
 */
public class SearchUserActivity extends BaseActivity<ActivitySearchBaseBinding> {

    public SearchUserActivity() {
        super(SearchUserActivity.class);
    }

    //----------------------------init----------------------------

    @Override
    public ActivitySearchBaseBinding getBinding() {
        return ActivitySearchBaseBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void init() {
        super.init();

        initViewModel();

        initView();
    }



    //----------------------------view----------------------------

    private void initView(){
        binding.topBar.setTitle(
                getString(com.czy.customviewlib.R.string.search_user)
        );

        initViewModelVo();

        initRecyclerView();

        observeData();
    }

    private void initRecyclerView(){
        vm.initRecyclerAdapter(
                binding.rclvSearch,
                position -> {
                    Log.d(TAG, "position:" + position);
                }
        );
    }

    private void observeData(){
    }

    //----------------------------viewModel----------------------------
;
    // 由于此页面是复用页面，所以可能需要多个viewModel适配，所以类型使用的是通用viewModel
    private SearchUserVm vm;

    private void initViewModel(){
        ApiViewModelFactory apiViewModelFactory = new ApiViewModelFactory(MainApplication.getApiRequestImplInstance(), MainApplication.getInstance().getMessageSender());
        vm = ViewModelUtil.newViewModel(this, apiViewModelFactory, SearchUserVm.class);

        SearchUserVm searchActivityUserVo = vm;
        searchActivityUserVo.init(new SearchUserVo());
    }

    private void initViewModelVo(){

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
                Optional.ofNullable(vm.searchUserVo)
                        .map(vo -> vo.edtvInputData)
                        .ifPresent(edtvInputData -> edtvInputData.setValue(newText));
                return true;
            }
        });
        // LiveData -> SearchView
        vm.searchUserVo.edtvInputData.observe(this, newText -> {
            if (newText != null && !newText.equals(binding.searchBar.getQuery().toString())) {
                binding.searchBar.setQuery(newText, false); // 更新 SearchView 的文本
            }
        });
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
        vm.searchUsers(query);
    }
}