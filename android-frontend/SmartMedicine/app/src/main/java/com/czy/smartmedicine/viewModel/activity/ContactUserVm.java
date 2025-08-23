package com.czy.smartmedicine.viewModel.activity;


import android.util.Log;


import androidx.lifecycle.LiveData;

import androidx.lifecycle.ViewModel;

import com.czy.appcore.network.netty.api.send.SocketMessageSender;

import com.czy.baseUtilLib.network.BaseResponse;

import com.czy.domain.ao.chat.ChatContactItemAo;
import com.czy.domain.ao.newUser.MyFriendItemAo;
import com.czy.domain.ao.userBrief.UserBriefIntentAo;
import com.czy.domain.constant.NettyConstants;
import com.czy.domain.dto.http.request.GetMyFriendsRequest;
import com.czy.domain.dto.http.response.GetMyFriendsResponse;
import com.czy.domain.entity.UserViewEntity;
import com.czy.domain.fragmentActivityAo.ContactUserGroupVo;
import com.czy.dao.networkRepository.ApiRequestImpl;
import com.czy.smartmedicine.MainApplication;
import com.czy.smartmedicine.fragment.friends.ContactUserGroupFragment;
import com.czy.smartmedicine.manager.HttpRequestManager;
import com.czy.smartmedicine.utils.ViewModelUtil;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ContactUserVm extends ViewModel {

    private static final String TAG = ContactUserVm.class.getName();

    private final ApiRequestImpl apiRequestImpl;
    private final SocketMessageSender socketMessageSender;

    public ContactUserVm(ApiRequestImpl apiRequestImpl, SocketMessageSender socketMessageSender){
        this.apiRequestImpl = apiRequestImpl;
        this.socketMessageSender = socketMessageSender;
    }

    //---------------------------Vo Ld---------------------------

    public ContactUserGroupVo contactUserGroupVo = new ContactUserGroupVo();

    public void init(ContactUserGroupVo contactUserGroupVo){
        this.contactUserGroupVo = contactUserGroupVo;

        initVo(contactUserGroupVo);
        initialNetworkRequest();
    }

    private void initVo(ContactUserGroupVo contactUserGroupVo){
        this.contactUserGroupVo = contactUserGroupVo;
    }


    //---------------------------NetWork---------------------------

    private void initialNetworkRequest() {
        // 首次打开：Http请求
        if (HttpRequestManager.getIsFirstOpen(ContactUserGroupFragment.class.getName())){
            getMyFriendList(new LinkedList<>());
        }
        else {
            List<ChatContactItemAo> cacheList = Optional.ofNullable(MainApplication.getInstance().friendList)
                    .orElse(new ArrayList<>());
            contactUserGroupVo.contactListVo.contactItemList.postValue(cacheList);
        }
    }

    //==========获取我的全部好友

    private void doGetMyFriendList(GetMyFriendsRequest request){
        apiRequestImpl.getMyFriendList(
                request,
                this::handleGetMyFriendListResponse,
                ViewModelUtil::globalThrowableToast
        );
    }

    private void handleGetMyFriendListResponse(BaseResponse<GetMyFriendsResponse> response){
        if (ViewModelUtil.handleResponse(response)){
            // 获取响应的list
            List<MyFriendItemAo> myFriendItemAos = Optional.ofNullable(response)
                    .map(BaseResponse::getData)
                    .map(data -> data.addMeRequestList)
                    .orElse(new ArrayList<>());

//            if (myFriendItemAos.isEmpty()) {
//                return;
//            }

//            // 查询当前的list
//            List<ContactItemVo> list = Optional.ofNullable(contactUserGroupVo)
//                    .map(vo -> vo.contactListVo)
//                    .map(cvo -> cvo.contactItemList)
//                    .map(LiveData::getValue)
//                    .orElse(new ArrayList<>());
//
//            // 通过两者计算获取新增的list
            List<ChatContactItemAo> list = myFriendItemAos.stream()
                    .map(myFriendItemAo -> {
                        // 使用 Optional 检查 userViewEntity 是否为 null
                        UserViewEntity userViewEntity = Optional.ofNullable(myFriendItemAo.userViewEntity).orElse(new UserViewEntity());

                        ChatContactItemAo contactItem = new ChatContactItemAo();
                        // 设置头像 URL
                        contactItem.chatContactItemVo.avatarUrlOrUri = (Optional.ofNullable(userViewEntity.avatarUrl).orElse(""));
                        // 设置名称
                        contactItem.chatContactItemVo.name = (Optional.ofNullable(userViewEntity.userName).orElse(""));
                        // 设置账号
                        contactItem.contactAccount = (Optional.ofNullable(userViewEntity.userAccount).orElse(""));
                        // userId
                        contactItem.userId = (Optional.ofNullable(userViewEntity.userId).orElse(NettyConstants.ERROR_ID));
                        return contactItem;
                    })
                    .collect(Collectors.toList());

            contactUserGroupVo.contactListVo.contactItemList.postValue(list);
        }
    }

    //==========获取用户的好友列表

    /**
     * 将本地的数据交给后端，避免重复数据申请，代码待实现
     * @param accountList   不使用的时候不能传入null；暂时传入new LinkedList<>();
     */
    public void getMyFriendList(List<String> accountList){
        if (accountList == null){
            accountList = new LinkedList<>();
        }
        GetMyFriendsRequest request = new GetMyFriendsRequest();
        request.senderId = MainApplication.getInstance().getUserLoginInfoAo().userId;
        request.receiverId = NettyConstants.SERVER_ID;
        request.accountList = accountList;
        doGetMyFriendList(request);
    }

    //==========点击用户

    public void onUserClicked(int position, OnUserClickedFinish onFinish){
        Log.d(TAG, "onUserClicked::position: " + position);
        ChatContactItemAo ccAo = Optional.ofNullable(contactUserGroupVo)
                .map(v -> v.contactListVo)
                .map(c -> c.contactItemList)
                .map(LiveData::getValue)
                .map(list -> list.get(position))
                .orElse(new ChatContactItemAo());

        UserBriefIntentAo ubAo = new UserBriefIntentAo();
        ubAo.avatarUrl = Optional.ofNullable(ccAo.chatContactItemVo).map(vo -> vo.avatarUrlOrUri).orElse("");
        ubAo.userAccount = Optional.ofNullable(ccAo.contactAccount).orElse("");
        ubAo.userName = Optional.ofNullable(ccAo.chatContactItemVo).map(vo -> vo.name).orElse("");
        ubAo.userId = Optional.ofNullable(ccAo.userId).orElse(NettyConstants.ERROR_ID);
//        ChatActivityStartAo ubAo = new ChatActivityStartAo();
//        ubAo.chatMessageListItemVo = new ArrayList<>();
//        ubAo.avatarUrl = Optional.ofNullable(ccAo.avatarUrlOrUri).map(LiveData::getValue).orElse("");
//        ubAo.contactAccount = Optional.ofNullable(ccAo.account).map(LiveData::getValue).orElse("");
//        ubAo.contactName = Optional.ofNullable(ccAo.name).map(LiveData::getValue).orElse("");

        onFinish.onFinish(ubAo);
    }

    public interface OnUserClickedFinish{
        void onFinish(UserBriefIntentAo ao);
    }

    private void storage(){
        MainApplication.getInstance().friendList = Optional.ofNullable(contactUserGroupVo)
                .map(vo -> vo.contactListVo)
                .map(clvo -> clvo.contactItemList)
                .map(LiveData::getValue)
                .orElse(new ArrayList<>());
    }

    public void onPause() {
        storage();
    }

    public void onDestroy() {
//        storage();
    }
}
