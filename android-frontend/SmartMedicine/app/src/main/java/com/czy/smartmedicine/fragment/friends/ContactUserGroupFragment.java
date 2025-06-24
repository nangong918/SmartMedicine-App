package com.czy.smartmedicine.fragment.friends;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.czy.baseUtilsLib.activity.BaseFragment;
import com.czy.baseUtilsLib.viewModel.ViewModelUtil;
import com.czy.customviewlib.view.contact.ContactAdapter;
import com.czy.dal.ao.userBrief.UserBriefStartAo;
import com.czy.dal.vo.entity.contact.ContactListVo;
import com.czy.dal.vo.viewModelVo.contactUserGroup.ContactUserGroupVo;
import com.czy.smartmedicine.MainApplication;
import com.czy.smartmedicine.activity.UserBriefActivity;
import com.czy.smartmedicine.databinding.FragmentContactUserGroupBinding;
import com.czy.smartmedicine.viewModel.base.ApiViewModelFactory;
import com.czy.smartmedicine.viewModel.activity.ContactUserGroupViewModel;

import java.util.Optional;


public class ContactUserGroupFragment extends BaseFragment<FragmentContactUserGroupBinding> {

    private int position = 0;

    public ContactUserGroupFragment(int position) {
        super(ContactUserGroupFragment.class);
        this.position = position;
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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        changeToDo(position);
    }

    @Override
    protected void init() {
        super.init();
        initView();
    }

    private void initView(){
        initViewModel();

        initRecyclerView();
    }

    @Override
    protected void setListener() {
        super.setListener();
    }

    //-----------------------ViewModel-----------------------

    private ContactUserGroupViewModel contactUserGroupViewModel;

    private void initViewModel() {
        ApiViewModelFactory apiViewModelFactory = new ApiViewModelFactory(MainApplication.getApiRequestImplInstance(), MainApplication.getInstance().getMessageSender());
        contactUserGroupViewModel = ViewModelUtil.newViewModel(this, apiViewModelFactory, ContactUserGroupViewModel.class);

        initViewModelVo();

        observeData();
    }

    private void initViewModelVo(){
        ContactUserGroupVo contactUserGroupVo = new ContactUserGroupVo();

        contactUserGroupVo.contactListVo = new ContactListVo();

        contactUserGroupViewModel.init(contactUserGroupVo);

        // binding.setViewModel(contactUserGroupViewModel);
        // binding.setLifecycleOwner(this);

        // 初始化获取
//        contactUserGroupViewModel.getMyFriendList(new ArrayList<>());
    }

    private void observeData() {
        // 观察RecyclerView
        Optional.ofNullable(contactUserGroupViewModel)
                .map(vm -> vm.contactUserGroupVo)
                .map(cvo -> cvo.contactListVo)
                .map(cvo -> cvo.contactItemList)
                .ifPresent(liveData -> {
                    liveData.observe(this, newList -> {
                        Optional.ofNullable(((ContactAdapter)binding.rvFriends.getAdapter()))
                                .ifPresent(contactAdapter -> contactAdapter.setCurrentList(newList));
                    });
                });
    }

    //-----------------------RecyclerView-----------------------

    @SuppressLint("ClickableViewAccessibility")
    private void initRecyclerView(){

        ContactAdapter adapter = new ContactAdapter(
                contactUserGroupViewModel.contactUserGroupVo.contactListVo.contactItemList.getValue(),
                position -> {
            Log.d(TAG, "position:" + position);
            contactUserGroupViewModel.onUserClicked(position, (ao) -> {
                // 启动用户详细信息界面
                Intent intent = new Intent(requireActivity(), UserBriefActivity.class);
                intent.putExtra(UserBriefStartAo.class.getName(), ao);
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
        if (contactUserGroupViewModel != null){
            contactUserGroupViewModel.onPause();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (contactUserGroupViewModel != null){
            contactUserGroupViewModel.onDestroy();
        }
    }
}