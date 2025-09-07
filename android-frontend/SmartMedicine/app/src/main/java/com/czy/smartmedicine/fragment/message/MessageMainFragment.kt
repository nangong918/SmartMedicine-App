package com.czy.smartmedicine.fragment.message

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.czy.appview.R
import com.czy.appview.view.message.MessageViewPagerEnum
import com.czy.domain.fragmentActivityAo.message.MessageMainFAo
import com.czy.smartmedicine.activity.MainActivity
import com.czy.smartmedicine.databinding.FragmentMessageMainBinding
import com.czy.smartmedicine.utils.BaseVmFragment
import com.czy.smartmedicine.viewModel.fragment.message.MessageMainVm


class MessageMainFragment : BaseVmFragment<FragmentMessageMainBinding, MessageMainVm>(
    MessageMainFragment::class,
    MessageMainVm::class
) {
    override fun initBinding(): FragmentMessageMainBinding {
        return FragmentMessageMainBinding.inflate(layoutInflater)
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

        // 设置顶部导航栏的点击监听器
        binding.messageSelectBar.setOnViewPagerBarClickListener { position ->
            vm.fao.currentPosition.value = position
            binding.vPager2.setCurrentItem(position, true)
        }

        // 注册页面变化回调
        binding.vPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                vm.fao.currentPosition.value = position
            }
        })
    }

    //---------------------------ViewModel---------------------------

    override fun initViewModel() {
        super.initViewModel()

        val fao = MessageMainFAo()
        fao.currentPosition.value = MessageViewPagerEnum.MESSAGE.index

        vm.init(fao, this)

        // 设置 ViewPager2 的适配器
        binding.vPager2.adapter = vm.messageViewPagerAdapter

        observeData()
    }

    private fun observeData() {
        vm.fao.currentPosition.observe(viewLifecycleOwner){
            position ->
            run {
                if (!isAdded){
                    return@observe
                }
                val enum = MessageViewPagerEnum.getEnumByIndex(position)
                when (enum) {
                    MessageViewPagerEnum.MESSAGE -> {
                        (requireActivity() as MainActivity).setBaseBarColorRes(
                            R.color.green_0
                        )
                    }
                    MessageViewPagerEnum.ADDRESS_BOOK -> {
                        (requireActivity() as MainActivity).setBaseBarColorRes(
                            R.color.green_50
                        )
                    }
                }
            }
        }
    }
}