package com.czy.smartmedicine.activity.order

import android.os.Bundle
import com.czy.appview.view.medicine.order.OrderViewPagerEnum
import com.czy.domain.fragmentActivityAo.medicine.order.OrderListAAo
import com.czy.smartmedicine.databinding.ActivityOrderListBinding
import com.czy.smartmedicine.fragment.order.OrderViewPagerAdapter
import com.czy.smartmedicine.utils.BaseVmActivity
import com.czy.smartmedicine.viewModel.activity.order.OrderListVm

class OrderListActivity : BaseVmActivity<ActivityOrderListBinding, OrderListVm>(
    OrderListActivity::class,
    OrderListVm::class
) {
    override fun initBinding(): ActivityOrderListBinding {
        return ActivityOrderListBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun setListener() {
        super.setListener()

        binding.vpg2.adapter = vm.viewPagerAdapter
        binding.vpgBarOrder.setOnViewPagerBarClickListener {
            position ->
            vm.aao.currentPageLd.value = position
            binding.vpg2.setCurrentItem(position, true)
        }
    }

    override fun initViewModel() {
        super.initViewModel()

        initVmAAo()

        observeData()

        initRequest()
    }

    private fun initVmAAo(){
        vm.aao = OrderListAAo()
        vm.aao.currentPageLd.value = OrderViewPagerEnum.APPOINTMENT_ORDER.index

        vm.viewPagerAdapter = OrderViewPagerAdapter(this)
    }

    private fun observeData(){
        vm.aao.currentPageLd.observe(this){
            currentPage ->
            binding.vpgBarOrder.setCurrentPosition(
                currentPage
            )
            binding.vpg2.setCurrentItem(
                currentPage,
                true
            )
        }
    }

    private fun initRequest(){

    }
}