package com.czy.smartmedicine.viewModel.fragment.message

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.czy.appcore.network.netty.api.receive.FriendApiHandler
import com.czy.appcore.network.netty.api.send.SocketMessageSender
import com.czy.appview.view.contact.ContactAdapter
import com.czy.baseutil.network.BaseResponse
import com.czy.dao.networkRepository.ApiRequestImpl
import com.czy.domain.ao.message.ContactItemAo
import com.czy.domain.ao.newUser.MyFriendItemAo
import com.czy.domain.ao.userBrief.UserBriefIntentAo
import com.czy.domain.constant.NettyConstants
import com.czy.domain.dto.http.request.BaseHttpRequest
import com.czy.domain.dto.http.request.GetMyFriendsRequest
import com.czy.domain.dto.http.response.GetMyFriendsResponse
import com.czy.domain.dto.netty.response.AddUserToTargetUserResponse
import com.czy.domain.dto.netty.response.HandleAddUserResponse
import com.czy.domain.entity.UserViewEntity
import com.czy.domain.fragmentActivityAo.message.AddressBookFAo
import com.czy.smartmedicine.MainApplication
import com.czy.smartmedicine.fragment.message.children.AddressBookFragment
import com.czy.smartmedicine.manager.HttpRequestManager
import com.czy.smartmedicine.utils.ViewModelUtil
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.LinkedList
import java.util.Optional
import java.util.stream.Collectors

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

    open var adapter : ContactAdapter? = null


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


    //==========获取我的全部好友
    private fun doGetMyFriendList(request: GetMyFriendsRequest) {
        apiRequestImpl.getMyFriendList(
            request,
            { response ->
                this.handleGetMyFriendListResponse(response)
            },
            { throwable -> Log.e(TAG, "handleGetMyFriendListResponse: ", throwable) }
        )
    }

    private fun handleGetMyFriendListResponse(response : BaseResponse<GetMyFriendsResponse>) {
        if (ViewModelUtil.handleResponse(response)) {
            // 获取响应的list
            val myFriendItemAos = Optional.ofNullable<BaseResponse<GetMyFriendsResponse>>(response)
                .map { obj: BaseResponse<GetMyFriendsResponse> -> obj.data }
                .map<List<MyFriendItemAo>> { data: GetMyFriendsResponse -> data.addMeRequestList }
                .orElse(ArrayList())

//            // 通过两者计算获取新增的list
            val list = myFriendItemAos.stream()
                .map { myFriendItemAo: MyFriendItemAo ->
                    // 使用 Optional 检查 userViewEntity 是否为 null
                    val userViewEntity =
                        Optional.ofNullable(myFriendItemAo.userViewEntity)
                            .orElse(UserViewEntity())

                    val contactItem =
                        ContactItemAo()
                    // 设置头像 URL
                    contactItem.contactItemVo.avatarUrl =
                        (Optional.ofNullable(userViewEntity.avatarUrl).orElse(""))
                    // 设置名称
                    contactItem.contactItemVo.name =
                        (Optional.ofNullable(userViewEntity.userName).orElse(""))
                    // 设置账号
                    contactItem.contactAccount =
                        (Optional.ofNullable(userViewEntity.userAccount).orElse(""))
                    // userId
                    contactItem.contactId = (Optional.ofNullable(userViewEntity.userId)
                        .orElse(NettyConstants.ERROR_ID))
                    contactItem
                }
                .collect(Collectors.toList())

            fao.contactListVo.contactItemList.postValue(list)
        }
    }


    //==========获取用户的好友列表
    /**
     * 将本地的数据交给后端，避免重复数据申请，代码待实现
     * @param accountList   不使用的时候不能传入null；暂时传入new LinkedList<>();
     */
    fun getMyFriendList(accountList: List<String?>?) {
        var finalList = accountList
        if (accountList == null) {
            finalList = LinkedList()
        }
        val request = GetMyFriendsRequest()
        request.senderId = MainApplication.getInstance().userLoginInfoAo?.userId
        request.receiverId = NettyConstants.SERVER_ID
        request.accountList = finalList
        doGetMyFriendList(request)
    }


    //==========点击用户
    fun onUserClicked(position: Int, onFinish: OnUserClickedFinish) {
        Log.d(TAG, "onUserClicked::position: $position")
        val ccAo = fao.contactListVo.contactItemList?.value?.get(position) ?: ContactItemAo()

        val ubAo = UserBriefIntentAo()

        ubAo.avatarUrl = ccAo.contactItemVo?.avatarUrl ?: ""

        ubAo.userAccount = ccAo.contactAccount?: ""
        ubAo.userName = ccAo.contactItemVo?.name?: ""

        ubAo.userId = ccAo.contactId?: NettyConstants.ERROR_ID

        onFinish.onFinish(ubAo)
    }

    interface OnUserClickedFinish {
        fun onFinish(ao: UserBriefIntentAo?)
    }


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
            request.senderId = MainApplication.getInstance().userLoginInfoAo?.userId?:NettyConstants.ERROR_ID

            // 取与我相关的添加请求
            doGetMyFriendApplyList(request)
            // 获取好友列表
            getMyFriendList(LinkedList())
        }
        else {
            val num = MainApplication.getInstance().friendsApplyNum
            fao.newFriends.postValue(num)

            val cacheList: List<ContactItemAo> = MainApplication.getInstance().friendList?: ArrayList()
            fao.contactListVo.contactItemList.postValue(cacheList)
        }
    }

    //==========获取与我相关的添加请求
    fun doGetMyFriendApplyList(request: BaseHttpRequest) {
        apiRequestImpl.getMyFriendApplyList(
            request,
            {
                response -> this.handleGetMyFriendApplyList(response)
            },
            {
                throwable -> Log.e(TAG, "handleGetMyFriendApplyList: ", throwable)
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

    //---------------------------Logic---------------------------

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

    private fun storage() {
        MainApplication.getInstance().friendsApplyNum = fao.newFriends.value ?:0
        MainApplication.getInstance().friendList = fao.contactListVo.contactItemList.value?: listOf()
    }

    fun onPause() {
        storage()
    }

    fun onDestroy() {
        storage()
        unInitEventBus()
    }
}