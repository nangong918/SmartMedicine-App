package com.czy.smartmedicine.fragment.friends;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.czy.baseUtilLib.activity.BaseFragment;
import com.czy.baseUtilLib.viewModel.ViewModelUtil;
import com.czy.appview.view.contact.ContactAdapter;
import com.czy.domain.ao.userBrief.UserBriefIntentAo;
import com.czy.domain.vo.entity.contact.ContactListVo;
import com.czy.domain.fragmentActivityAo.ContactUserGroupVo;
import com.czy.smartmedicine.MainApplication;
import com.czy.smartmedicine.activity.UserBriefActivity;
import com.czy.smartmedicine.databinding.FragmentContactUserGroupBinding;
import com.czy.smartmedicine.viewModel.activity.ContactUserVm;
import com.czy.smartmedicine.viewModel.base.ApiViewModelFactory;

import java.util.ArrayList;
import java.util.Optional;


public class ContactUserGroupFragment extends BaseFragment<FragmentContactUserGroupBinding> {

    private int position = 0;

    public ContactUserGroupFragment(int position) {
        super(ContactUserGroupFragment.class);
        this.position = position;
    }

    @Override
    public FragmentContactUserGroupBinding getBinding() {
        return FragmentContactUserGroupBinding.inflate(getLayoutInflater());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViewModel();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        changeToDo(position);
    }

    private void initView(){

        initRecyclerView();
    }

    @Override
    protected void setListener() {
        super.setListener();

        binding.lyMain.setOnRefreshListener(() -> {
            viewModel.getMyFriendList(new ArrayList<>());
        });
    }

    //-----------------------ViewModel-----------------------

    private ContactUserVm viewModel;

    private void initViewModel() {
        ApiViewModelFactory apiViewModelFactory = new ApiViewModelFactory(MainApplication.getApiRequestImplInstance(), MainApplication.getInstance().getMessageSender());
        viewModel = ViewModelUtil.newViewModel(this, apiViewModelFactory, ContactUserVm.class);

        initViewModelVo();

        observeData();
    }

    private void initViewModelVo(){
        ContactUserGroupVo contactUserGroupVo = new ContactUserGroupVo();

        contactUserGroupVo.contactListVo = new ContactListVo();

        viewModel.init(contactUserGroupVo);

        // binding.setViewModel(contactUserGroupViewModel);
        // binding.setLifecycleOwner(this);

        // 初始化获取
//        contactUserGroupViewModel.getMyFriendList(new ArrayList<>());
    }

    private void observeData() {
        // 观察RecyclerView
        Optional.ofNullable(viewModel)
                .map(vm -> vm.contactUserGroupVo)
                .map(cvo -> cvo.contactListVo)
                .map(cvo -> cvo.contactItemList)
                .ifPresent(liveData -> {
                    liveData.observe(this, newList -> {
                        Optional.ofNullable(((ContactAdapter)binding.rvFriends.getAdapter()))
                                .ifPresent(contactAdapter -> contactAdapter.setCurrentList(newList));
                        // 取消下滑
                        binding.lyMain.setRefreshing(false);
                    });
                });
    }

    //-----------------------RecyclerView-----------------------

    @SuppressLint("ClickableViewAccessibility")
    private void initRecyclerView(){

        ContactAdapter adapter = new ContactAdapter(
                viewModel.contactUserGroupVo.contactListVo.contactItemList.getValue(),
                position -> {
            Log.d(TAG, "position:" + position);
            viewModel.onUserClicked(position, (ao) -> {
                // 启动用户详细信息界面
                Intent intent = new Intent(requireActivity(), UserBriefActivity.class);
                intent.putExtra(UserBriefIntentAo.class.getName(), ao);
                requireActivity().startActivity(intent);
            });
        });
        binding.rvFriends.setAdapter(adapter);
//        binding.rvFriends.setOnTouchListener((v, event) -> false);
    }

    //-----------------------test-----------------------

    public void changeToDo(int position) {
        String text = "position:" + position;
        binding.tvTest.setText(text);
    }

    private ClickTurnToOtherFragments clickTurnToOtherFragments;

    /**
     * 对外提供从此Fragment跳转到其他Fragment的接口
     * @param callback  跳转到其他Fragment的回调
     */
    public void setTurnToOtherFragmentListener(ClickTurnToOtherFragments callback) {
        clickTurnToOtherFragments = callback;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (viewModel != null){
            viewModel.onPause();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (viewModel != null){
            viewModel.onDestroy();
        }
    }
}