package com.czy.smartmedicine.viewModel.activity.search;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.czy.appcore.network.netty.api.send.SocketMessageSender;
import com.czy.appcore.service.AddUserStateHandler;
import com.czy.baseUtilsLib.network.BaseResponse;
import com.czy.dal.OnPositionItemButtonContentClick;
import com.czy.dal.ao.newUser.AddUserStatusAo;
import com.czy.dal.ao.newUser.SearchFriendApplyAo;
import com.czy.dal.constant.newUserGroup.AddSourceEnum;
import com.czy.dal.constant.newUserGroup.ApplyButtonStatusEnum;
import com.czy.dal.constant.newUserGroup.ApplyStatusEnum;
import com.czy.dal.constant.newUserGroup.HandleButtonStatusEnum;
import com.czy.dal.constant.newUserGroup.HandleStatusEnum;
import com.czy.dal.dto.http.request.SearchUserRequest;
import com.czy.dal.dto.http.response.SearchUserResponse;
import com.czy.dal.dto.netty.request.AddUserRequest;
import com.czy.dal.dto.netty.request.HandleAddedUserRequest;
import com.czy.dal.vo.entity.addContact.AddContactItemVo;
import com.czy.dal.vo.fragmentActivity.search.SearchUserVo;
import com.czy.datalib.networkRepository.ApiRequestImpl;
import com.czy.smartmedicine.MainApplication;
import com.czy.smartmedicine.utils.ViewModelUtil;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class SearchActivityUserViewModel extends ViewModel {

    private static final String TAG = SearchActivityUserViewModel.class.getSimpleName();

    private final ApiRequestImpl apiRequestImpl;
    private final SocketMessageSender socketMessageSender;

    public SearchActivityUserViewModel(ApiRequestImpl apiRequestImpl, SocketMessageSender socketMessageSender){
        this.apiRequestImpl = apiRequestImpl;
        this.socketMessageSender = socketMessageSender;
    }

    //---------------------------Vo Ld---------------------------

    public SearchUserVo searchUserVo = new SearchUserVo();

    public void init(SearchUserVo searchUserVo){
        initVo(searchUserVo);
    }

    private void initVo(SearchUserVo searchUserVo) {
        this.searchUserVo = searchUserVo;
    }

    private void showSearchUsers(List<SearchFriendApplyAo> userList) {
        List<AddContactItemVo> addContactList = Optional.ofNullable(userList)
                        .map(ul -> {
                            List<AddContactItemVo> list = new LinkedList<>();
                            for (SearchFriendApplyAo u : ul){
                                AddContactItemVo itemVo = new AddContactItemVo();
                                itemVo.account = (u.account);
                                itemVo.name = (u.userName);
                                itemVo.avatarUrlOrUri = (
                                        Optional.ofNullable(u.fileResAo)
                                                .map(f -> f.fileUrl)
                                                .orElse(null)
                                        );
                                // 设置查询到的userId
                                itemVo.uid = (u.userId);
                                // 通过返回的ao直接设置状态
                                Integer[] handleButtonState = AddUserStateHandler.getApplyStateButton(u.addUserStatusAo);
                                Log.i(TAG, "handleSearchUsers: " + Arrays.toString(handleButtonState));
                                itemVo.buttonStates = (handleButtonState);
                                itemVo.onPositionButtonContentClick = getListViewClick();
                                list.add(itemVo);
                            }
                            return list;
                        }).orElse(new LinkedList<>());
        searchUserVo.addContactListVo.contactItemList.setValue(addContactList);
    }

    //---------------------------NetWork---------------------------
    ;
    //==========搜索用户

    private void doSearchUsers(
            SearchUserRequest request) {
        this.apiRequestImpl.searchUsers(
                request,
                this::handleSearchUsers,
                ViewModelUtil::globalThrowableToast
        );
    }

    private void handleSearchUsers(BaseResponse<SearchUserResponse> response) {
        if (ViewModelUtil.handleResponse(response)){
            Optional.ofNullable(response)
                    .map(BaseResponse::getData)
                    .ifPresent(data -> showSearchUsers(data.userList));
        }
    }

    //==========添加用户

    private void doAddUserFriend(
            AddUserRequest request,
            String handlerAccount) {
        // Http
//        this.apiRequestImpl.addUserFriend(
//                request,
//                response -> handleAddUserFriend(response, request.applyType, handlerAccount),
//                ViewModelUtil::globalThrowableToast
//        );
        // Socket
        socketMessageSender.addFriend(request);
        new Handler(Looper.getMainLooper())
                .postDelayed(() -> {
                    uiUpdateAddUserFriend(request.applyType, handlerAccount);
                }, 500);
    }

//    private void handleAddUserFriend(BaseResponse<Void> response, Integer applyType, String handlerAccount) {
//        if (ViewModelUtil.handleResponse(response)){
//            uiUpdateAddUserFriend(applyType, handlerAccount);
//        }
//    }

    private void uiUpdateAddUserFriend(Integer applyType, String handlerAccount){
        if (applyType != null && applyType == ApplyStatusEnum.APPLYING.code){
            MainApplication.getInstance().showGlobalToast(com.czy.customviewlib.R.string.have_send_add_user_request);
        }
        else if(applyType != null && applyType == ApplyStatusEnum.NOT_APPLY.code){
            MainApplication.getInstance().showGlobalToast(com.czy.customviewlib.R.string.have_cancel_add_user_request);
        }
        updateUiByApplyTypeAndApplierAccount(applyType, handlerAccount);
    }

    //==========处理添加用户

    private void doHandleAddUser(HandleAddedUserRequest request, String applierAccount){
        // Http
//        apiRequestImpl.handleAddedUser(
//                request,
//                response -> this.handleHandleAddUser(response, request.handleType, applierAccount),
//                ViewModelUtil::globalThrowableToast
//        );
        // Socket
        socketMessageSender.handleAddedUser(request);
        new Handler(Looper.getMainLooper())
                .postDelayed(() -> {
                    uiUpdateHandleAddUser(request.handleType, applierAccount);
                }, 500);
    }

//    private void handleHandleAddUser(BaseResponse<Void> response, Integer handleType, String applierAccount) {
//        if (ViewModelUtil.handleResponse(response)){
//            uiUpdateHandleAddUser(handleType, applierAccount);
//        }
//    }

    private void uiUpdateHandleAddUser(Integer handleType, String applierAccount){
        if (handleType != null && handleType == HandleStatusEnum.NOT_HANDLE.code){
            MainApplication.getInstance().showGlobalToast(com.czy.customviewlib.R.string.have_cancel_add_user_request);
        }
        updateUiByHandleTypeAndApplierAccount(handleType, applierAccount);
    }

    //---------------------------logic---------------------------
    ;
    // 根据 handleType 和 applierAccount更新Ui
    private void updateUiByHandleTypeAndApplierAccount(Integer handleType, String applierAccount){
        if (!TextUtils.isEmpty(applierAccount)){
            // 处理类型
            HandleStatusEnum handleStatusEnum = HandleStatusEnum.getByCode(handleType);
            if (handleStatusEnum == null){
                Log.w(TAG, "handleHandleAddUser:: handleType值有问题");
                return;
            }

            // 更新Ao
            AddUserStatusAo ao = Optional.ofNullable(this.searchUserVo)
                    .map(vo -> vo.addContactListVo)
                    .map(avo -> avo.contactItemList)
                    .map(LiveData::getValue)
                    .map(list -> {
                        for (AddContactItemVo item : list) {
                            if (TextUtils.isEmpty(item.account)){
                                continue;
                            }
                            if (item.account.equals(applierAccount)){
                                // 设置状态
                                item.isBeAdd = item.addUserStatusAo.isBeAdd(
                                        MainApplication.getInstance().
                                                getUserLoginInfoAo().account
                                );
                                return item.addUserStatusAo;
                            }
                        }
                        return new AddUserStatusAo();
                    })
                    .orElse(new AddUserStatusAo());
            ao.handleStatus = handleStatusEnum.code;

            // 更新ui
            Optional.ofNullable(this.searchUserVo)
                    .map(vo -> vo.addContactListVo)
                    .map(addContactListVo -> addContactListVo.getByAccount(applierAccount))
                    .ifPresent(item -> {
                        Integer[] handleButtonState = AddUserStateHandler.getHandleStateButton(ao);
                        Log.i(TAG, "handleHandleAddUser:: handleButtonState = " + Arrays.toString(handleButtonState));
                        // 更新操作
                        item.buttonStates = (handleButtonState);
                    });
        }
    }
    // 根据 applyType 和 handlerAccount 更新按钮状态
    private void updateUiByApplyTypeAndApplierAccount(Integer applyType, String handlerAccount){
        if (!TextUtils.isEmpty(handlerAccount)){
            // 处理类型
            ApplyStatusEnum applyStatusEnum = ApplyStatusEnum.getByCode(applyType);
            if (applyStatusEnum == null){
                Log.w(TAG, "handleHandleAddUser:: handleType值有问题");
                return;
            }

            // 更新Ao
            AddUserStatusAo ao = Optional.ofNullable(this.searchUserVo)
                    .map(vo -> vo.addContactListVo)
                    .map(avo -> avo.contactItemList)
                    .map(LiveData::getValue)
                    .map(list -> {
                        for (AddContactItemVo item : list) {
                            item.isBeAdd = item.addUserStatusAo.isBeAdd(
                                    MainApplication.getInstance().
                                            getUserLoginInfoAo().account
                            );
                            if (TextUtils.isEmpty(item.account)){
                                continue;
                            }
                            if (item.account.equals(handlerAccount)){
                                return item.addUserStatusAo;
                            }

                        }
                        return new AddUserStatusAo();
                    })
                    .orElse(new AddUserStatusAo());
            ao.applyStatus = applyStatusEnum.code;

            // 更新ui
            Optional.ofNullable(this.searchUserVo)
                    .map(vo -> vo.addContactListVo)
                    .map(addContactListVo -> addContactListVo.getByAccount(handlerAccount))
                    .ifPresent(item -> {
                        Integer[] handleButtonState = AddUserStateHandler.getApplyStateButton(ao);
                        // 更新操作
                        item.buttonStates = (handleButtonState);
                    });
        }
    }

    // 搜索用户
    public void searchUsers(@NonNull String account){
        Log.i(TAG, "searchUsers: " + account);
        SearchUserRequest request = new SearchUserRequest();
        request.senderId = MainApplication.getInstance().getUserLoginInfoAo().userId;
        request.userData = account;
        doSearchUsers(request);
    }

    public void addUserFriend(AddUserRequest request, String handlerAccount){
        doAddUserFriend(
                request,
                handlerAccount
        );
    }

    //==========处理当前当前List的点击响应

    public OnPositionItemButtonContentClick getListViewClick(){

        return (position, buttonId, content) -> {
            AddContactItemVo vo = getAddContactItemVo(position);

            if (vo == null){
                Log.w(TAG, "getListViewClick:: vo为空");
                return;
            }
            // 申请方 (view不是被添加)
            if (vo.isBeAdd){
                // 申请
                if (ApplyButtonStatusEnum.APPLY_ADD.code == buttonId){
                    Log.i(TAG, "申请");
                    AddUserRequest addUserRequest = new AddUserRequest();
                    addUserRequest.source = AddSourceEnum.ACCOUNT.code;
                    addUserRequest.senderId = MainApplication.getInstance().getUserLoginInfoAo().userId;
                    addUserRequest.receiverId = vo.uid;
//                    addUserRequest.addUserAccount = addUserRequest.receiverId;
//                    addUserRequest.myAccount = addUserRequest.senderId;
                    addUserRequest.myName = MainApplication.getInstance().getUserLoginInfoAo().userName;
                    addUserRequest.addContent = content;
                    addUserRequest.applyType = ApplyStatusEnum.APPLYING.code;
                    addUserFriend(addUserRequest, vo.account);
                }
                // 取消申请
                else if (ApplyButtonStatusEnum.CANCEL_APPLY.code == buttonId){
                    Log.i(TAG, "取消申请");
                    AddUserRequest addUserRequest = new AddUserRequest();
                    addUserRequest.source = AddSourceEnum.ACCOUNT.code;
                    addUserRequest.senderId = MainApplication.getInstance().getUserLoginInfoAo().userId;
                    addUserRequest.receiverId = vo.uid;
//                    addUserRequest.addUserAccount = addUserRequest.receiverId;
//                    addUserRequest.myAccount = addUserRequest.senderId;
                    addUserRequest.myName = MainApplication.getInstance().getUserLoginInfoAo().userName;
                    addUserRequest.applyType = ApplyStatusEnum.NOT_APPLY.code;
                    addUserRequest.addContent = content;
                    addUserFriend(addUserRequest, vo.account);
                }
                // 已添加
                else if (ApplyButtonStatusEnum.ADDED.code == buttonId){
                    Log.i(TAG, "已添加");
                    MainApplication.getInstance().showGlobalToast(com.czy.customviewlib.R.string.have_added_user);
                }
                // 再次申请
                else if (ApplyButtonStatusEnum.BE_REFUSED.code == buttonId){
                    Log.i(TAG, "再次申请");
                    AddUserRequest addUserRequest = new AddUserRequest();
                    addUserRequest.source = AddSourceEnum.ACCOUNT.code;
                    addUserRequest.senderId = MainApplication.getInstance().getUserLoginInfoAo().userId;
                    addUserRequest.receiverId = vo.uid;
//                    addUserRequest.addUserAccount = addUserRequest.receiverId;
//                    addUserRequest.myAccount = addUserRequest.senderId;
                    addUserRequest.myName = MainApplication.getInstance().getUserLoginInfoAo().userName;
                    addUserRequest.addContent = content;
                    addUserFriend(addUserRequest, vo.account);
                }
                // 被拉黑
                else if (ApplyButtonStatusEnum.BE_BLACK.code == buttonId){
                    Log.i(TAG, "被拉黑");
                    MainApplication.getInstance().showGlobalToast(com.czy.customviewlib.R.string.be_blacked_user);
                }

            }

            // 处理方
            else {
                // 同意
                if (HandleButtonStatusEnum.AGREE.code == buttonId){
                    Log.i(TAG, "同意");
                    HandleAddedUserRequest handleAddedUserRequest = new HandleAddedUserRequest();
                    handleAddedUserRequest.senderId = MainApplication.getInstance().getUserLoginInfoAo().userId;
                    handleAddedUserRequest.receiverId = vo.uid;
                    handleAddedUserRequest.handleType = HandleStatusEnum.AGREE.code;
                    handleAddedUserRequest.additionalContent = content;
                    doHandleAddUser(handleAddedUserRequest, vo.account);
                }
                // 拒绝
                else if (HandleButtonStatusEnum.REFUSED.code == buttonId){
                    Log.i(TAG, "拒绝");
                    HandleAddedUserRequest handleAddedUserRequest = new HandleAddedUserRequest();
                    handleAddedUserRequest.senderId = MainApplication.getInstance().getUserLoginInfoAo().userId;
                    handleAddedUserRequest.receiverId = vo.uid;
                    handleAddedUserRequest.handleType = HandleStatusEnum.REFUSED.code;
                    handleAddedUserRequest.additionalContent = content;
                    doHandleAddUser(handleAddedUserRequest, vo.account);
                }
                // 已同意
                else if (HandleButtonStatusEnum.HAVE_AGREED.code == buttonId){
                    Log.i(TAG, "已同意");
                    MainApplication.getInstance().showGlobalToast(com.czy.customviewlib.R.string.have_agreed_user);
                }
                // 已拒绝
                else if (HandleButtonStatusEnum.HAVE_REFUSED.code == buttonId){
                    Log.i(TAG, "已拒绝");
                    MainApplication.getInstance().showGlobalToast(com.czy.customviewlib.R.string.have_refused_user);
                }
                // 拉黑
                else if (HandleButtonStatusEnum.BLACK.code == buttonId){
                    Log.i(TAG, "拉黑");
                    HandleAddedUserRequest handleAddedUserRequest = new HandleAddedUserRequest();
                    handleAddedUserRequest.senderId = MainApplication.getInstance().getUserLoginInfoAo().userId;
                    handleAddedUserRequest.receiverId = vo.uid;
                    handleAddedUserRequest.handleType = HandleStatusEnum.BLACK.code;
                    handleAddedUserRequest.additionalContent = content;
                    doHandleAddUser(handleAddedUserRequest, vo.account);
                }
                // 解除拉黑
                else if (HandleButtonStatusEnum.UN_BLACK.code == buttonId){
                    Log.i(TAG, "解除拉黑");
                    HandleAddedUserRequest handleAddedUserRequest = new HandleAddedUserRequest();
                    handleAddedUserRequest.senderId = MainApplication.getInstance().getUserLoginInfoAo().userId;
                    handleAddedUserRequest.receiverId = vo.uid;
                    handleAddedUserRequest.handleType = HandleStatusEnum.NOT_HANDLE.code;
                    handleAddedUserRequest.additionalContent = content;
                    doHandleAddUser(handleAddedUserRequest, vo.account);
                }
                // 已取消
                else if (HandleButtonStatusEnum.BE_CANCELED.code == buttonId){
                    Log.i(TAG, "对方已取消");
                    MainApplication.getInstance().showGlobalToast(com.czy.customviewlib.R.string.be_canceled_add);
                }
            }
        };
    }

    private AddContactItemVo getAddContactItemVo(int position){
        return Optional.of(searchUserVo)
                .map(v -> v.addContactListVo)
                .map(o -> o.contactItemList)
                .map(LiveData::getValue)
                .map(list -> {
                    if (list.size() >= position){
                        return list.get(position);
                    }
                    return null;
                })
                .orElse(null);
    }
}
