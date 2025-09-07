package com.czy.smartmedicine.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.czy.baseutil.image.ImageLoadUtil
import com.czy.baseutil.ui.ToastUtils
import com.czy.domain.fragmentActivityAo.mine.MineFAo
import com.czy.smartmedicine.activity.OrderActivity
import com.czy.smartmedicine.databinding.FragmentMineBinding
import com.czy.smartmedicine.utils.BaseVmFragment
import com.czy.smartmedicine.viewModel.fragment.MineVm
import java.util.Optional

class MineFragment : BaseVmFragment<FragmentMineBinding, MineVm>(
    MineFragment::class,
    MineVm::class
) {
    override fun initBinding(): FragmentMineBinding {
        return FragmentMineBinding.inflate(layoutInflater)
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

        // 社区动态
        binding.lyCommunityDynamic.setOnClickListener {
            if(vm.fao.isFinishedLdMap["社区动态"]?.value == false){
                ToastUtils.showToast(requireContext(), requireContext().getString(
                    com.czy.appview.R.string.coding
                ))
                return@setOnClickListener
            }
        }

        // 关注
        binding.lyFollow.setOnClickListener {
            if(vm.fao.isFinishedLdMap["关注"]?.value == false){
                ToastUtils.showToast(requireContext(), requireContext().getString(
                    com.czy.appview.R.string.coding
                ))
                return@setOnClickListener
            }
        }

        // 粉丝
        binding.lyFans.setOnClickListener {
            if(vm.fao.isFinishedLdMap["粉丝"]?.value == false){
                ToastUtils.showToast(requireContext(), requireContext().getString(
                    com.czy.appview.R.string.coding
                ))
                return@setOnClickListener
            }
        }

        // 我的动态
        binding.lyMyDynamic.setOnClickListener {
            if(vm.fao.isFinishedLdMap["我的动态"]?.value == false){
                ToastUtils.showToast(requireContext(), requireContext().getString(
                    com.czy.appview.R.string.coding
                ))
                return@setOnClickListener
            }
        }

        // 我的收藏
        binding.lyMyCollect.setOnClickListener {
            if(vm.fao.isFinishedLdMap["我的收藏"]?.value == false){
                ToastUtils.showToast(requireContext(), requireContext().getString(
                    com.czy.appview.R.string.coding
                ))
                return@setOnClickListener
            }
        }

        // 我的运动
        binding.lyMySport.setOnClickListener {
            if(vm.fao.isFinishedLdMap["我的运动"]?.value == false){
                ToastUtils.showToast(requireContext(), requireContext().getString(
                    com.czy.appview.R.string.coding
                ))
                return@setOnClickListener
            }
        }

        // 我的饮食
        binding.lyMyDiet.setOnClickListener {
            if(vm.fao.isFinishedLdMap["我的饮食"]?.value == false){
                ToastUtils.showToast(requireContext(), requireContext().getString(
                    com.czy.appview.R.string.coding
                ))
                return@setOnClickListener
            }
        }

        // 我的健康提醒
        binding.lyMyHealthReminder.setOnClickListener {
            if(vm.fao.isFinishedLdMap["我的健康提醒"]?.value == false){
                ToastUtils.showToast(requireContext(), requireContext().getString(
                    com.czy.appview.R.string.coding
                ))
                return@setOnClickListener
            }
        }

        // 我的购物
        binding.lyMyShopping.setOnClickListener {
            if(vm.fao.isFinishedLdMap["我的购物"]?.value == false){
                ToastUtils.showToast(requireContext(), requireContext().getString(
                    com.czy.appview.R.string.coding
                ))
                return@setOnClickListener
            }
        }

        // 我的健康
        binding.lyMyHealth.setOnClickListener {
            if(vm.fao.isFinishedLdMap["我的健康"]?.value == false){
                ToastUtils.showToast(requireContext(), requireContext().getString(
                    com.czy.appview.R.string.coding
                ))
                return@setOnClickListener
            }
        }

        // 我的订单
        binding.lyMyOrder.setOnClickListener {
            if(vm.fao.isFinishedLdMap["我的订单"]?.value == false){
                ToastUtils.showToast(requireContext(), requireContext().getString(
                    com.czy.appview.R.string.coding
                ))
                return@setOnClickListener
            }

            val intent = Intent(requireActivity(), OrderActivity::class.java)
            startActivity(intent)
        }

        // 充值
        binding.btnRecharge.setOnClickListener {
            if(vm.fao.isFinishedLdMap["充值"]?.value == false){
                ToastUtils.showToast(requireContext(), requireContext().getString(
                    com.czy.appview.R.string.coding
                ))
                return@setOnClickListener
            }
        }

        // 流水记录
        binding.btnTransactionRecord.setOnClickListener {
            if(vm.fao.isFinishedLdMap["流水记录"]?.value == false){
                ToastUtils.showToast(requireContext(), requireContext().getString(
                    com.czy.appview.R.string.coding
                ))
                return@setOnClickListener
            }
        }

        // 设置
        binding.btnSetting.setOnClickListener {
            if(vm.fao.isFinishedLdMap["设置"]?.value == false){
                ToastUtils.showToast(requireContext(), requireContext().getString(
                    com.czy.appview.R.string.coding
                ))
                return@setOnClickListener
            }
        }
    }

    //---------------------------ViewModel---------------------------

    override fun initViewModel() {
        super.initViewModel()

        initFAo()

        // 观察数据
        observeData()

        // init request
    }



    private fun initFAo(){
        val mineFAo = MineFAo()

        // todo 从全局变量MainApplication获取数据， 避免频繁访问后端

        vm.init(mineFAo)
    }

    private fun observeData() {
        // user
        vm.fao.userNameLd.observe(viewLifecycleOwner) { userName ->
            Optional.ofNullable(userName)
                .filter { it.isNotEmpty() }
                .ifPresent { name -> binding.tvUserName.text = name }
        }
        vm.fao.userAccountLd.observe(viewLifecycleOwner) { userAccount ->
            Optional.ofNullable(userAccount)
                .filter { it.isNotEmpty() }
                .ifPresent { account -> binding.tvUserAccount.text = account }
        }

        // user Image
        vm.fao.avatarUrlLd.observe(viewLifecycleOwner) { avatarUrl ->
            Optional.ofNullable(avatarUrl)
                .filter { it.isNotEmpty() }
                .ifPresent { url ->
                    ImageLoadUtil.loadImageViewByResource(
                        url,
                        binding.imgvAvatar
                    )
                }
        }

        // 动态 - 关注 - 粉丝
        vm.fao.myDynamicLd.observe(viewLifecycleOwner) { myDynamic ->
            Optional.ofNullable(myDynamic)
                .filter { it.isNotEmpty() }
                .ifPresent { dynamic -> binding.tvCommunityDynamic.text = dynamic }
        }
        vm.fao.myFollowLd.observe(viewLifecycleOwner) { myFollow ->
            Optional.ofNullable(myFollow)
                .filter { it.isNotEmpty() }
                .ifPresent { follow -> binding.tvFollow.text = follow }
        }
        vm.fao.myFansLd.observe(viewLifecycleOwner) { myFans ->
            Optional.ofNullable(myFans)
                .filter { it.isNotEmpty() }
                .ifPresent { fans -> binding.tvFans.text = fans }
        }

        // money
        vm.fao.moneyLd.observe(viewLifecycleOwner) { money ->
            Optional.ofNullable(money)
                .filter { it.isNotEmpty() }
                .ifPresent { m -> binding.tvMoney.text = m }
        }
    }
}