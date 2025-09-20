package com.czy.smartmedicine.fragment.message.children

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import com.czy.domain.ao.intent.NewUserActivityIntentAo
import com.czy.domain.constant.newUserGroup.UserGroupEnum
import com.czy.domain.fragmentActivityAo.message.AddressBookFAo
import com.czy.smartmedicine.activity.NewUserActivity
import com.czy.smartmedicine.activity.search.SearchUserActivity
import com.czy.smartmedicine.databinding.FragmentAddressBookBinding
import com.czy.smartmedicine.utils.BaseVmFragment
import com.czy.smartmedicine.viewModel.fragment.message.AddressBookVm


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
    }

    private fun observeData(){
        vm.fao.newFriends.observe(viewLifecycleOwner){
            if (it != null){
                binding.vNewFriendsPrompt.setMessageNum(it)
            }
        }
    }

    private fun initRequest(){
        vm.initialNetworkRequest()
    }
}