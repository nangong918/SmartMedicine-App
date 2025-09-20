package com.czy.smartmedicine.viewModel.fragment.message

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.czy.appcore.network.netty.api.receive.FriendApiHandler
import com.czy.appcore.network.netty.api.send.SocketMessageSender
import com.czy.baseutil.network.BaseResponse
import com.czy.dao.networkRepository.ApiRequestImpl
import com.czy.domain.ao.chat.UserLoginInfoAo
import com.czy.domain.constant.NettyConstants
import com.czy.domain.dto.http.request.BaseHttpRequest
import com.czy.domain.dto.netty.response.AddUserToTargetUserResponse
import com.czy.domain.dto.netty.response.HandleAddUserResponse
import com.czy.domain.fragmentActivityAo.message.AddressBookFAo
import com.czy.smartmedicine.MainApplication
import com.czy.smartmedicine.fragment.message.children.AddressBookFragment
import com.czy.smartmedicine.manager.HttpRequestManager
import com.czy.smartmedicine.utils.ViewModelUtil
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.Optional

open class AddressBookVm(
    private val apiRequestImpl: ApiRequestImpl,
    private val socketMessageSender: SocketMessageSender
) : ViewModel(){

    companion object {
        val TAG: String = AddressBookVm::class.java.name
    }

    //---------------------------FAo Ld---------------------------

    open var fao: AddressBookFAo = AddressBookFAo()

    open fun init(fao: AddressBookFAo) {
        this.fao = fao
        initReceiveAddUserApi()
    }

    //---------------------------NetWork---------------------------

    //---------------------------NetWork---------------------------

    private var friendApiHandler: FriendApiHandler? = null

    // 消息队列
    private val messageHandler = Handler(Looper.getMainLooper())

    // 首次进入好友列表申请根本就不在FriendsFragment，而是在ViewPager的Fragment中
    private fun initReceiveAddUserApi() {
        initEventBus()
        friendApiHandler = object : FriendApiHandler{
            override fun receiveAddedFriend(response: AddUserToTargetUserResponse) {
                Log.i(TAG, "receiveAddedFriend: " + response.toJsonString())
                messageHandler.post {
                    processFriendsMessage()
                }
                // TODO 消息弹窗提示
            }

            override fun receiveBeDeleted(response: AddUserToTargetUserResponse) {
                Log.i(TAG, "receiveBeDeleted: " + response.toJsonString())
            }

            override fun receiveAddFriendResult(response: HandleAddUserResponse) {
                Log.i(TAG, "receiveAddFriendResult: " + response.toJsonString())
                messageHandler.post {
                    processFriendsMessage()
                }

            }

        }
    }


    //---------------------------Logic---------------------------

    @Synchronized
    private fun processFriendsMessage() {
        var newFriends = Optional.ofNullable<AddressBookFAo>(fao)
            .map { vo: AddressBookFAo -> vo.newFriends }
            .map<Int> { obj: MutableLiveData<Int> -> obj.value }
            .orElse(0)
        newFriends += 1
        val finalNewFriends = newFriends
        Optional.ofNullable<AddressBookFAo>(fao)
            .map { vo: AddressBookFAo -> vo.newFriends }
            .ifPresent { ld: MutableLiveData<Int> -> ld.postValue(finalNewFriends) }
    }

    fun initialNetworkRequest() {
        // 首次打开：Http请求
        if (HttpRequestManager.getIsFirstOpen(AddressBookFragment::class.java.name)) {
            val request = BaseHttpRequest()
            request.senderId = Optional.ofNullable(MainApplication.getInstance().userLoginInfoAo)
                .map { ao: UserLoginInfoAo -> ao.userId }
                .orElse(NettyConstants.ERROR_ID)
            if (NettyConstants.ERROR_ID == request.senderId) {
                Log.w(TAG, "doGetUserNewMessage: senderId is empty")
                return
            }
            doGetMyFriendApplyList(request)
        } else {
            val num = MainApplication.getInstance().friendsApplyNum
            fao.newFriends.postValue(num)
        }
    }

    //==========获取与我相关的添加请求
    private fun doGetMyFriendApplyList(request: BaseHttpRequest) {
        apiRequestImpl.getMyFriendApplyList(
            request,
            {
                response -> this.handleGetMyFriendApplyList(response)
            },
            {
                throwable -> ViewModelUtil.globalThrowableToast(throwable)
            }
        )
    }

    private fun handleGetMyFriendApplyList(response: BaseResponse<Int>) {
        if (ViewModelUtil.handleResponse(response)) {
            Optional.ofNullable<AddressBookFAo>(fao)
                .map { vo -> vo.newFriends }
                .ifPresent { ld -> ld.postValue(response.data) }
        }
    }

    //---------------------------EventBus---------------------------

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onMessageReceived(response: AddUserToTargetUserResponse?) {
        if (response != null) {
            friendApiHandler?.receiveAddedFriend(response)
        }
        // 移除已处理的粘性事件
        EventBus.getDefault().removeStickyEvent(response)
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onMessageReceived(response: HandleAddUserResponse?) {
        if (response != null) {
            friendApiHandler?.receiveAddFriendResult(response)
        }
        // 移除已处理的粘性事件
        EventBus.getDefault().removeStickyEvent(response)
    }

    private fun initEventBus() {
        EventBus.getDefault().register(this)
    }

    private fun unInitEventBus() {
        EventBus.getDefault().unregister(this)
    }

    //---------------------------logic---------------------------
    fun storage() {
        MainApplication.getInstance().friendsApplyNum = fao.newFriends.value ?:0
    }

    fun onDestroy() {
        storage()
        unInitEventBus()
    }
}