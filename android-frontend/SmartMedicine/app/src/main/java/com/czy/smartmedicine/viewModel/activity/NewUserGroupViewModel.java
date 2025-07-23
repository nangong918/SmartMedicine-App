package com.czy.smartmedicine.viewModel.activity;


import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.czy.appcore.network.netty.api.send.SocketMessageSender;
import com.czy.baseUtilsLib.network.BaseResponse;
import com.czy.customviewlib.view.addContact.AddContactAdapter;
import com.czy.dal.OnPositionItemButtonContentClick;
import com.czy.appcore.service.AddUserStateHandler;
import com.czy.dal.ao.chat.UserLoginInfoAo;
import com.czy.dal.ao.newUser.AddUserStatusAo;
import com.czy.dal.constant.newUserGroup.ApplyButtonStatusEnum;
import com.czy.dal.constant.newUserGroup.ApplyStatusEnum;
import com.czy.dal.constant.newUserGroup.HandleButtonStatusEnum;
import com.czy.dal.constant.newUserGroup.HandleStatusEnum;
import com.czy.dal.dto.netty.request.AddUserRequest;
import com.czy.dal.dto.http.request.BaseHttpRequest;
import com.czy.dal.dto.netty.request.HandleAddedUserRequest;
import com.czy.dal.dto.http.response.GetAddMeRequestListResponse;
import com.czy.dal.dto.http.response.GetHandleMyAddUserResponseListResponse;
import com.czy.dal.vo.entity.addContact.AddContactItemVo;

import com.czy.dal.vo.fragmentActivity.NewUserGroupVo;
import com.czy.dal.ao.newUser.NewUserItemAo;
import com.czy.datalib.networkRepository.ApiRequestImpl;
import com.czy.smartmedicine.MainApplication;
import com.czy.smartmedicine.utils.ViewModelUtil;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

// ViewModel的作用，保存LiveData，LiveData在ViewModel才起作用
public class NewUserGroupViewModel extends ViewModel {

    private static final String TAG = NewUserGroupViewModel.class.getName();

    private final ApiRequestImpl apiRequestImpl;
    private final SocketMessageSender socketMessageSender;

    public NewUserGroupViewModel(ApiRequestImpl apiRequestImpl, SocketMessageSender socketMessageSender) {
        this.apiRequestImpl = apiRequestImpl;
        this.socketMessageSender = socketMessageSender;
    }

    //---------------------------Vo Ld---------------------------

    public AddContactAdapter rclAdapter;

    public void updateRclList(){
        List<AddContactItemVo> list = Optional.ofNullable(newUserGroupVo)
                .map(vo -> vo.addContactListVo)
                .map(listVo -> listVo.contactItemList)
                .map(LiveData::getValue)
                .orElse(new LinkedList<>());
        if (rclAdapter != null){
            rclAdapter.setChatItems(list);
        }
    }

    public NewUserGroupVo newUserGroupVo = new NewUserGroupVo();

    public void init(NewUserGroupVo newUserGroupVo){
        initVo(newUserGroupVo);
    }

    private void initVo(NewUserGroupVo newUserGroupVo){
        this.newUserGroupVo = newUserGroupVo;
        this.listViewClickInstance = getListViewClick();

    }

    //---------------------------NetWork---------------------------
;
    //==========获取添加我的请求列表

    private void doGetAddMeRequestList(BaseHttpRequest request) {
        apiRequestImpl.getAddMeRequestList(
                request,
                this::handleGetAddMeRequestList,
                ViewModelUtil::globalThrowableToast
        );
    }

    // isAddMeNotResponse = true;
    private void handleGetAddMeRequestList(BaseResponse<GetAddMeRequestListResponse> response) {
        List<NewUserItemAo> list = Optional.ofNullable(response)
                        .map(BaseResponse::getData)
                        .map(data -> data.addMeRequestList)
                        .orElse(null);
        handleNewUserData(list);
    }

    //==========获取我请求添加的响应结果列表

    private void doGetHandleMyAddUserResponseList(BaseHttpRequest request) {
        apiRequestImpl.getHandleMyAddUserResponseList(
                request,
                this::handleGetHandleMyAddUserResponseList,
                ViewModelUtil::globalThrowableToast
        );
    }

    // isAddMeNotResponse = false;
    private void handleGetHandleMyAddUserResponseList(BaseResponse<GetHandleMyAddUserResponseListResponse> response) {
        List<NewUserItemAo> list = Optional.ofNullable(response)
                .map(BaseResponse::getData)
                .map(data -> data.handleMyAddUserResponseList)
                .orElse(null);
        handleNewUserData(list);
    }

    //==========添加用户 AddUserRequest
;
//    private void doAddUserFriend(
//            AddUserRequest request,
//            String handlerAccount) {
//        this.apiRequestImpl.addUserFriend(
//                request,
//                response -> handleAddUserFriend(response, request.applyType, handlerAccount),
//                ViewModelUtil::globalThrowableToast
//        );
//    }

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
            AddUserStatusAo ao = Optional.ofNullable(this.newUserGroupVo)
                    .map(vo -> vo.addContactListVo)
                    .map(avo -> avo.contactItemList)
                    .map(LiveData::getValue)
                    .map(list -> {
                        for (AddContactItemVo item : list) {
                            if (TextUtils.isEmpty(item.account)){
                                continue;
                            }
                            if (item.account.equals(applierAccount)){
                                return item.addUserStatusAo;
                            }
                        }
                        return new AddUserStatusAo();
                    })
                    .orElse(new AddUserStatusAo());
            ao.handleStatus = handleStatusEnum.code;

            // 更新ui
            Optional.ofNullable(this.newUserGroupVo)
                    .map(vo -> vo.addContactListVo)
                    .map(addContactListVo -> addContactListVo.getByAccount(applierAccount))
                    .ifPresent(item -> {
                        Integer[] handleButtonState = AddUserStateHandler.getHandleStateButton(ao);
                        Log.i(TAG, "handleHandleAddUser1:: handleButtonState = " + Arrays.toString(handleButtonState));
                        // 更新操作
                        item.buttonStates = (handleButtonState);
                    });
        }
    }

    // 很具 applyType 和 applierAccount 更新按钮状态
    private void updateUiByApplyTypeAndApplierAccount(Integer applyType, String handlerAccount){
        if (!TextUtils.isEmpty(handlerAccount)){
            // 处理类型
            ApplyStatusEnum applyStatusEnum = ApplyStatusEnum.getByCode(applyType);
            if (applyStatusEnum == null){
                Log.w(TAG, "handleHandleAddUser2:: handleType值有问题");
                return;
            }

            // 更新Ao
            AddUserStatusAo ao = Optional.ofNullable(this.newUserGroupVo)
                    .map(vo -> vo.addContactListVo)
                    .map(avo -> avo.contactItemList)
                    .map(LiveData::getValue)
                    .map(list -> {
                        for (AddContactItemVo item : list) {
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
            Optional.ofNullable(this.newUserGroupVo)
                    .map(vo -> vo.addContactListVo)
                    .map(addContactListVo -> addContactListVo.getByAccount(handlerAccount))
                    .ifPresent(item -> {
                        Integer[] handleButtonState = AddUserStateHandler.getApplyStateButton(ao);
                        // 更新操作
                        item.buttonStates = (handleButtonState);
                    });
        }
    }

    // 用于记录两个响应的user user list
    private final List<NewUserItemAo> newUserItemAoList = new LinkedList<>();

    //==========获取最新的添加信息消息

    public void getNewUserData(){
        // 首先先清空数据缓存
        newUserItemAoList.clear();

        // 请求
        BaseHttpRequest request = new BaseHttpRequest();
        request.senderId = MainApplication.getInstance().getUserLoginInfoAo().userId;

        // 获取添加我的消息List
        doGetAddMeRequestList(request);

        // 获取我添加的消息响应List
        doGetHandleMyAddUserResponseList(request);
    }

    private synchronized void handleNewUserData(List<NewUserItemAo> list){
        // 非空添加
        Optional.ofNullable(list)
                        .ifPresent(newUserItemAoList::addAll);

        // 更新Data List
        this.newUserGroupVo.newUserItemListLd.setValue(newUserItemAoList);

        // 更新View List
        List<AddContactItemVo> newList = Optional.ofNullable(this.newUserGroupVo)
                .map(vo -> vo.addContactListVo)
                .map(ctlist -> ctlist.contactItemList)
                .map(LiveData::getValue)
                .orElse(new ArrayList<>());
        if (list != null){
            for(NewUserItemAo ao : list){
                AddContactItemVo itemVo = new AddContactItemVo();
                Optional.ofNullable(ao)
                        .map(a -> a.userViewEntity)
                        .ifPresent(uv -> {
                            itemVo.account = (uv.userAccount);
                            itemVo.avatarUrlOrUri = (uv.avatarUrl);
                            itemVo.name = (ao.userViewEntity.userName);
                            // isBeAdd
                            ao.isBeAdd = ao.addUserStatusAo.isBeAdd(MainApplication.getInstance().
                                    getUserLoginInfoAo().account);
                            itemVo.isBeAdd = ao.isBeAdd;
                            Log.i("TAG", "isBeAdd::" + itemVo.isBeAdd);
                        });
                if (ao != null){
//                    itemVo.isBeAdd = ao.isBeAdd;
                    itemVo.addUserStatusAo = ao.addUserStatusAo;
                    Integer[] handleButtonState = AddUserStateHandler.getHandleStateButton(itemVo.addUserStatusAo, itemVo.isBeAdd);
                    Log.i(TAG, "handleHandleAddUser3:: handleButtonState = " + Arrays.toString(handleButtonState));
                    itemVo.buttonStates = (handleButtonState);
                    itemVo.onPositionButtonContentClick = this.getListViewClick();
                }
                newList.add(itemVo);
            }
        }
        // 取消部分livedata；list类型如果使用livedata是无法观察到内部数据的。list<livedata<item>>又太耗性能
        Optional.ofNullable(this.newUserGroupVo.addContactListVo.contactItemList)
                .ifPresent(listLd -> listLd.setValue(newList));
        updateRclList();
//                        .map(LiveData::getValue)
//                        .ifPresent(l -> l.addAll(addContactListVo.contactItemList.getValue()));
    }

    public void addUserFriend(AddUserRequest request, String handlerAccount){
        // Http请求
//        doAddUserFriend(
//                request,
//                handlerAccount
//        );
        // Socket请求
        socketMessageSender.addFriend(request);
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            uiUpdateAddUserFriend(request.applyType, handlerAccount);
            // 延迟500毫秒
        }, 500);
    }

    //==========处理当前当前List的点击响应

    public OnPositionItemButtonContentClick listViewClickInstance;

    // 获取单例
    public OnPositionItemButtonContentClick getListViewClick(){
        if (this.listViewClickInstance == null){
            this.listViewClickInstance = (position, buttonId, content) -> {
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
                        UserLoginInfoAo userLoginInfoAo = MainApplication.getInstance().getUserLoginInfoAo();
                        addUserRequest.senderId = userLoginInfoAo.userId;
                        addUserRequest.receiverId = vo.uid;
                        addUserRequest.addUserAccount = vo.account;
                        addUserRequest.myAccount = userLoginInfoAo.account;
                        addUserRequest.myName = MainApplication.getInstance().getUserLoginInfoAo().userName;
                        addUserRequest.addContent = content;
                        addUserRequest.applyType = ApplyStatusEnum.APPLYING.code;
                        addUserFriend(addUserRequest, vo.account);
                    }
                    // 取消申请
                    else if (ApplyButtonStatusEnum.CANCEL_APPLY.code == buttonId){
                        Log.i(TAG, "取消申请");
                        AddUserRequest addUserRequest = new AddUserRequest();
                        UserLoginInfoAo userLoginInfoAo = MainApplication.getInstance().getUserLoginInfoAo();
                        addUserRequest.senderId = userLoginInfoAo.userId;
                        addUserRequest.receiverId = vo.uid;
                        addUserRequest.addUserAccount = vo.account;
                        addUserRequest.myAccount = userLoginInfoAo.account;
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
                        UserLoginInfoAo userLoginInfoAo = MainApplication.getInstance().getUserLoginInfoAo();
                        addUserRequest.senderId = userLoginInfoAo.userId;
                        addUserRequest.receiverId = vo.uid;
                        addUserRequest.addUserAccount = vo.account;
                        addUserRequest.myAccount = userLoginInfoAo.account;
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
                        UserLoginInfoAo userLoginInfoAo = MainApplication.getInstance().getUserLoginInfoAo();
                        handleAddedUserRequest.senderId = userLoginInfoAo.userId;
                        handleAddedUserRequest.receiverId = vo.uid;
                        handleAddedUserRequest.handleType = HandleStatusEnum.AGREE.code;
                        handleAddedUserRequest.additionalContent = content;
                        doHandleAddUser(handleAddedUserRequest, vo.account);
                    }
                    // 拒绝
                    else if (HandleButtonStatusEnum.REFUSED.code == buttonId){
                        Log.i(TAG, "拒绝");
                        HandleAddedUserRequest handleAddedUserRequest = new HandleAddedUserRequest();
                        UserLoginInfoAo userLoginInfoAo = MainApplication.getInstance().getUserLoginInfoAo();
                        handleAddedUserRequest.senderId = userLoginInfoAo.userId;
                        handleAddedUserRequest.receiverId = vo.uid;
                        handleAddedUserRequest.handleType = HandleStatusEnum.REFUSED.code;
                        handleAddedUserRequest.additionalContent = content;
                        doHandleAddUser(handleAddedUserRequest, vo.account);
                    }
                    // 已拒绝
                    else if (HandleButtonStatusEnum.HAVE_REFUSED.code == buttonId){
                        Log.i(TAG, "已拒绝");
                        MainApplication.getInstance().showGlobalToast(com.czy.customviewlib.R.string.have_refused_user);
                    }
                    // 已同意
                    else if (HandleButtonStatusEnum.HAVE_AGREED.code == buttonId){
                        Log.i(TAG, "已同意");
                        MainApplication.getInstance().showGlobalToast(com.czy.customviewlib.R.string.have_agreed_user);
                    }
                    // 拉黑
                    else if (HandleButtonStatusEnum.BLACK.code == buttonId){
                        Log.i(TAG, "拉黑");
                        HandleAddedUserRequest handleAddedUserRequest = new HandleAddedUserRequest();
                        UserLoginInfoAo userLoginInfoAo = MainApplication.getInstance().getUserLoginInfoAo();
                        handleAddedUserRequest.senderId = userLoginInfoAo.userId;
                        handleAddedUserRequest.receiverId = vo.uid;
                        handleAddedUserRequest.handleType = HandleStatusEnum.BLACK.code;
                        handleAddedUserRequest.additionalContent = content;
                        doHandleAddUser(handleAddedUserRequest, vo.account);
                    }
                    // 解除拉黑
                    else if (HandleButtonStatusEnum.UN_BLACK.code == buttonId){
                        Log.i(TAG, "解除拉黑");
                        HandleAddedUserRequest handleAddedUserRequest = new HandleAddedUserRequest();
                        UserLoginInfoAo userLoginInfoAo = MainApplication.getInstance().getUserLoginInfoAo();
                        handleAddedUserRequest.senderId = userLoginInfoAo.userId;
                        handleAddedUserRequest.receiverId = vo.uid;
                        handleAddedUserRequest.handleType = HandleStatusEnum.NOT_HANDLE.code;
                        handleAddedUserRequest.additionalContent = content;
                        doHandleAddUser(handleAddedUserRequest, vo.account);
                    }
                    // 已取消
                    else if (HandleButtonStatusEnum.BE_CANCELED.code == buttonId){
                        Log.i(TAG, "已取消");
                        MainApplication.getInstance().showGlobalToast(com.czy.customviewlib.R.string.canceled_add);
                    }
                }
            };
        }
        return this.listViewClickInstance;
    }

    private AddContactItemVo getAddContactItemVo(int position){
        return Optional.of(newUserGroupVo)
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

    public void onDestroy() {
        // 清空数据缓存
        newUserItemAoList.clear();
    }
}
