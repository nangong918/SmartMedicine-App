package com.czy.smartmedicine.activity.search;

import android.content.Intent;
import android.text.TextUtils;
import android.widget.SearchView;

import com.czy.appcore.network.api.handle.SyncRequestCallback;
import com.czy.baseutil.activity.BaseActivity;
import com.czy.baseutil.network.networkLoad.NetworkLoadUtils;
import com.czy.baseutil.ui.ToastUtils;
import com.czy.baseutil.viewModel.ViewModelUtil;
import com.czy.domain.ao.home.PostIntentAo;
import com.czy.domain.fragmentActivityAo.search.SearchPostAAo;
import com.czy.smartmedicine.MainApplication;
import com.czy.smartmedicine.activity.PostActivity;
import com.czy.smartmedicine.databinding.ActivitySearchBaseBinding;
import com.czy.smartmedicine.viewModel.activity.search.SearchPostVm;
import com.czy.smartmedicine.viewModel.base.ApiViewModelFactory;

import java.util.Optional;

/**
 * post 搜索
 * 公用 ActivitySearchBaseBinding
 */
public class SearchPostActivity extends BaseActivity<ActivitySearchBaseBinding> {


    public SearchPostActivity() {
        super(SearchPostActivity.class);
    }

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

    @Override
    protected void setListener() {
        super.setListener();

        binding.topBar.setOnClickListener(v -> finish());

        binding.btnSearch.setOnClickListener(v -> searchInfo());
    }

    //----------------------------view----------------------------

    private void initView(){
        binding.topBar.setTitle(
                getString(com.czy.appview.R.string.search_post)
        );

        initViewModelVo();

        observeData();
    }

    //----------------------------viewModel----------------------------

    private SearchPostVm vm;

    private void initViewModel(){
        ApiViewModelFactory apiViewModelFactory = new ApiViewModelFactory(MainApplication.getApiRequestImplInstance(), MainApplication.getInstance().getMessageSender());
        vm = ViewModelUtil.newViewModel(this, apiViewModelFactory, SearchPostVm.class);

        vm.init(new SearchPostAAo(), this);
        vm.initRecyclerAdapter(binding.rclvSearch, this::openSearchPostDetailActivity);
        vm.initDialogAnswer(this, v -> {
            // todo 跳转到跟ai聊天的详情页
        });
    }

    private void initViewModelVo(){
        SearchPostVm searchPostVm = vm;

        // 双向绑定
        // SearchView -> LiveData
        binding.searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                SearchPostVm searchPostVm = vm;
                Optional.ofNullable(searchPostVm.searchPostAAo)
                        .map(vo -> vo.edtvInputData)
                        .ifPresent(edtvInputData -> edtvInputData.setValue(newText));
                return true;
            }
        });
        // LiveData -> SearchView
        searchPostVm.searchPostAAo.edtvInputData.observe(this, newText -> {
            if (newText != null && !newText.equals(binding.searchBar.getQuery().toString())) {
                binding.searchBar.setQuery(newText, false); // 更新 SearchView 的文本
            }
        });
    }

    private void searchInfo(){
        String query = binding.searchBar.getQuery().toString();
        if (TextUtils.isEmpty(query)){
            ToastUtils.showToastActivity(this, "请输入搜索内容");
        }
        NetworkLoadUtils.showDialog(this);
        vm.searchPosts(this, query, new SyncRequestCallback() {
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

    private void observeData(){
    }
    
    private void openSearchPostDetailActivity(int position, Long postId){
        if (postId == null){
            ToastUtils.showToast(this, getString(com.czy.appview.R.string.post_id_is_null));
            return;
        }
        Intent intent = new Intent(this, PostActivity.class);

        PostIntentAo postIntentAo = new PostIntentAo();
        postIntentAo.postId = postId;

        intent.putExtra(PostIntentAo.POST_OPEN_INTENT, postIntentAo);

        startActivity(intent);
    }
}