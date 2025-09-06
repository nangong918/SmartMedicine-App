package com.czy.smartmedicine.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.czy.baseutil.image.ImageLoadUtil
import com.czy.domain.fragmentActivityAo.mine.MineFAo
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

        // 我的动态
        binding.lyMyDynamic.setOnClickListener {
        }

        // 我的收藏
        binding.lyMyCollect.setOnClickListener {
        }

        // 我的运动
        binding.lyMySport.setOnClickListener {
        }

        // 我的饮食
        binding.lyMyDiet.setOnClickListener {
        }

        // 我的健康提醒
        binding.lyMyHealthReminder.setOnClickListener {
        }

        // 我的购物
        binding.lyMyShopping.setOnClickListener {
        }

        // 我的健康
        binding.lyMyHealth.setOnClickListener {
        }

        // 我的订单
        binding.lyMyOrder.setOnClickListener {
        }

        // 充值
        binding.btnRecharge.setOnClickListener {
        }

        // 流水记录
        binding.btnTransactionRecord.setOnClickListener {
        }

        // 设置
        binding.btnSetting.setOnClickListener {
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
        vm.mineFAo.userNameLd.observe(viewLifecycleOwner) { userName ->
            Optional.ofNullable(userName)
                .filter { it.isNotEmpty() }
                .ifPresent { name -> binding.tvUserName.text = name }
        }
        vm.mineFAo.userAccountLd.observe(viewLifecycleOwner) { userAccount ->
            Optional.ofNullable(userAccount)
                .filter { it.isNotEmpty() }
                .ifPresent { account -> binding.tvUserAccount.text = account }
        }

        // user Image
        vm.mineFAo.avatarUrlLd.observe(viewLifecycleOwner) { avatarUrl ->
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
        vm.mineFAo.myDynamicLd.observe(viewLifecycleOwner) { myDynamic ->
            Optional.ofNullable(myDynamic)
                .filter { it.isNotEmpty() }
                .ifPresent { dynamic -> binding.tvCommunityDynamic.text = dynamic }
        }
        vm.mineFAo.myFollowLd.observe(viewLifecycleOwner) { myFollow ->
            Optional.ofNullable(myFollow)
                .filter { it.isNotEmpty() }
                .ifPresent { follow -> binding.tvFollow.text = follow }
        }
        vm.mineFAo.myFansLd.observe(viewLifecycleOwner) { myFans ->
            Optional.ofNullable(myFans)
                .filter { it.isNotEmpty() }
                .ifPresent { fans -> binding.tvFans.text = fans }
        }

        // money
        vm.mineFAo.moneyLd.observe(viewLifecycleOwner) { money ->
            Optional.ofNullable(money)
                .filter { it.isNotEmpty() }
                .ifPresent { m -> binding.tvMoney.text = m }
        }
    }
}