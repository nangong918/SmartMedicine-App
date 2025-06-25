package com.czy.smartmedicine.activity;


import android.content.Intent;
import android.util.Log;

import com.czy.baseUtilsLib.activity.BaseActivity;
import com.czy.baseUtilsLib.viewModel.ViewModelUtil;
import com.czy.customviewlib.view.addContact.AddContactAdapter;
import com.czy.dal.ao.NewUserGroupActivityStartAo;
import com.czy.dal.constant.newUserGroup.UserGroupEnum;
import com.czy.dal.vo.entity.addContact.AddContactListVo;
import com.czy.dal.vo.fragmentActivity.NewUserGroupVo;
import com.czy.smartmedicine.MainApplication;
import com.czy.smartmedicine.databinding.ActivityNewUserGroupBinding;
import com.czy.smartmedicine.viewModel.base.ApiViewModelFactory;
import com.czy.smartmedicine.viewModel.activity.NewUserGroupViewModel;

import java.util.Optional;

// Navigation解决首页切换问题
public class NewUserGroupActivity extends BaseActivity<ActivityNewUserGroupBinding> {

    public NewUserGroupActivity() {
        super(NewUserGroupActivity.class);
    }

    @Override
    protected void init() {
        super.init();

        initIntentData();
        initViewModel();
        initView();
    }

    private String title = "";

    @Override
    protected void setListener() {
        super.setListener();

        binding.topBar.setOnClickListener(v -> finish());
    }

    //-----------------------Intent Data-----------------------

    private NewUserGroupActivityStartAo newUserGroupActivityStartAo = null;

    private void initIntentData() {
        // 获取传递的对象
        try {
            Intent intent = getIntent();
            Optional.ofNullable(intent)
                    .map(i -> (NewUserGroupActivityStartAo)i.getSerializableExtra(NewUserGroupActivityStartAo.class.getName()))
                    .ifPresent(ao -> {
                        this.newUserGroupActivityStartAo = ao;
                        handleUserGroupEnum(ao.userGroupEnum);
                    });
        } catch (Exception e) {
            Log.e(TAG, "initIntentData::getSerializableExtra Error: ", e);
            finish();
        }
        if (newUserGroupActivityStartAo == null) {
            Log.e(TAG, "initIntentData::newUserGroupActivityStartAo is null");
            finish();
        }
    }

    private void handleUserGroupEnum(UserGroupEnum userGroupEnum) {
        if (UserGroupEnum.USER.equals(userGroupEnum)){
            title = getString(com.czy.customviewlib.R.string.new_friends);
        }
        else if (UserGroupEnum.GROUP.equals(userGroupEnum)){
            title = getString(com.czy.customviewlib.R.string.new_group);
        }
        else {
            Log.e(TAG, "handleUserGroupEnum::userGroupEnum is null or not in UserGroupEnum");
            finish();
        }
    }

    //-----------------------View-----------------------

    private void initView(){
        binding.topBar.setTitle(title);

        initRecyclerView();
    }

    private void initRecyclerView(){

        newUserGroupViewModel.newUserGroupVo.addContactListVo = new AddContactListVo();
        AddContactAdapter adapter = new AddContactAdapter(
                newUserGroupViewModel.newUserGroupVo.addContactListVo.contactItemList.getValue(),
                position -> {
            Log.d(TAG, "position:" + position);
        });
        binding.rclvContent.setAdapter(adapter);
    }

    //-----------------------ViewModel-----------------------

    private NewUserGroupViewModel newUserGroupViewModel;

    private void initViewModel(){
        ApiViewModelFactory apiViewModelFactory = new ApiViewModelFactory(MainApplication.getApiRequestImplInstance(), MainApplication.getInstance().getMessageSender());
        newUserGroupViewModel = ViewModelUtil.newViewModel(this, apiViewModelFactory, NewUserGroupViewModel.class);

        initViewModelVo();

//        // 绑定viewModel
//        binding.setViewModel(newUserGroupViewModel);
//        // 设置监听者
//        binding.setLifecycleOwner(this);

        observeData();
    }

    private void initViewModelVo(){
        NewUserGroupVo newUserGroupVo = new NewUserGroupVo();
        // 是否为User界面 如果为 null 或不等于 USER，默认设置为 true
        newUserGroupVo.isUserNotGroup = Optional.ofNullable(newUserGroupActivityStartAo)
                .map(ao -> ao.userGroupEnum)
                .map(userGroupEnum -> userGroupEnum.equals(UserGroupEnum.USER))
                .orElse(true);

        newUserGroupViewModel.init(newUserGroupVo);

        // 初始化ViewModel之后申请Data
        initViewModelData();
    }

    private void observeData(){
        // 观察RecyclerView
        Optional.ofNullable(newUserGroupViewModel)
                .map(vm -> vm.newUserGroupVo)
                .map(newUserGroupVo -> newUserGroupVo.addContactListVo)
                .map(addContactListVo -> addContactListVo.contactItemList)
                .ifPresent(listLd -> listLd.observe(this, list -> {
                    Optional.ofNullable(binding.rclvContent.getAdapter())
                            .map(adapter -> (AddContactAdapter)adapter)
                            .ifPresent(a -> a.setChatItems(list));
                }));
    }

    // 初始化ViewModel之后申请Data
    private void initViewModelData() {
        newUserGroupViewModel.getNewUserData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (newUserGroupViewModel != null){
            newUserGroupViewModel.onDestroy();
        }
    }
}