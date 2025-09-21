package com.czy.smartmedicine.fragment.message.children

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import com.czy.appview.view.contact.ContactAdapter
import com.czy.domain.ao.intent.NewUserActivityIntentAo
import com.czy.domain.ao.userBrief.UserBriefIntentAo
import com.czy.domain.constant.NettyConstants
import com.czy.domain.constant.newUserGroup.UserGroupEnum
import com.czy.domain.dto.http.request.BaseHttpRequest
import com.czy.domain.fragmentActivityAo.message.AddressBookFAo
import com.czy.smartmedicine.MainApplication
import com.czy.smartmedicine.activity.NewUserActivity
import com.czy.smartmedicine.activity.UserBriefActivity
import com.czy.smartmedicine.activity.search.SearchUserActivity
import com.czy.smartmedicine.databinding.FragmentAddressBookBinding
import com.czy.smartmedicine.utils.BaseVmFragment
import com.czy.smartmedicine.viewModel.fragment.message.AddressBookVm
import com.czy.smartmedicine.viewModel.fragment.message.AddressBookVm.OnUserClickedFinish
import java.util.LinkedList


class AddressBookFragment : BaseVmFragment<FragmentAddressBookBinding, AddressBookVm>(
    AddressBookFragment::class,
    AddressBookVm::class
) {
    override fun initBinding(): FragmentAddressBookBinding {
        return FragmentAddressBookBinding.inflate(layoutInflater)
    }

    // init
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    // initView
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun setListener() {
        super.setListener()

        setActivityLauncher()

        binding.lyNewFriends.setOnClickListener {
            val intent = Intent(
                requireActivity(),
                NewUserActivity::class.java
            )
            val newUserActivityIntentAo = NewUserActivityIntentAo()
            newUserActivityIntentAo.userGroupEnum = UserGroupEnum.USER
            intent.putExtra(NewUserActivityIntentAo.INTENT_KEY, newUserActivityIntentAo)
            newFriendLauncher?.launch(intent)
        }

        binding.lySearchFriends.setOnClickListener{
            val intent = Intent(
                requireActivity(),
                SearchUserActivity::class.java
            )
            searchUserLauncher?.launch(intent)
        }

        binding.lyFriendLabels.setOnClickListener {
        }

        // 刷新
        binding.lyMain.setOnRefreshListener {
            val request = BaseHttpRequest()
            request.senderId = MainApplication.getInstance().userLoginInfoAo?.userId?:NettyConstants.ERROR_ID
            // 取与我相关的添加请求
            vm.doGetMyFriendApplyList(request)
            // 获取好友列表
            vm.getMyFriendList(LinkedList())
        }
    }

    private var searchUserLauncher: ActivityResultLauncher<Intent>? = null
    private var newFriendLauncher: ActivityResultLauncher<Intent>? = null

    private fun setActivityLauncher() {
        searchUserLauncher = registerForActivityResult(
            StartActivityForResult()
        ) { result: ActivityResult? -> {
            // 返回之后刷新
        }}

        newFriendLauncher = registerForActivityResult(
            StartActivityForResult()
        ) { result: ActivityResult? -> {
            // 处理返回刷新
        }}
    }

    //---------------------------ViewModel---------------------------

    override fun initViewModel() {
        super.initViewModel()

        initVmFAo()

        observeData()

        initRequest()
    }

    private fun initVmFAo(){
        vm.init(AddressBookFAo())

        vm.adapter = ContactAdapter(
            vm.fao.contactListVo.contactItemList.value ?: emptyList()
        ) { position ->
            Log.d(TAG, "position:$position")
            vm.onUserClicked(
                position,
                object : OnUserClickedFinish {
                    override fun onFinish(ao: UserBriefIntentAo?) {
                        // 启动用户详细信息界面
                        val intent = Intent(requireActivity(), UserBriefActivity::class.java)
                        intent.putExtra(UserBriefIntentAo::class.java.name, ao)
                        requireActivity().startActivity(intent)
                    }
                })
        }

        binding.rclvContact.adapter = vm.adapter
    }

    private fun observeData(){
        vm.fao.newFriends.observe(viewLifecycleOwner){
            if (it != null){
                binding.vNewFriendsPrompt.setMessageNum(it)
            }
        }

        // 观察RecyclerView
        vm.fao.contactListVo.contactItemList.observe(viewLifecycleOwner){
            if (it != null){
                vm.adapter?.apply {
                    setCurrentList(it)
                }
            }

            // 取消下滑
            binding.lyMain.isRefreshing = false
        }
    }

    private fun initRequest(){
        vm.initialNetworkRequest()
    }

    override fun onPause() {
        super.onPause()
        vm.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        vm.onDestroy()
    }
}