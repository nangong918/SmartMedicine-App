package com.czy.smartmedicine.fragment.medicine.children

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.czy.smartmedicine.databinding.FragmentAppointmentBinding
import com.czy.smartmedicine.utils.BaseVmFragment
import com.czy.smartmedicine.viewModel.fragment.medicine.children.AppointmentVm


class AppointmentFragment : BaseVmFragment<FragmentAppointmentBinding, AppointmentVm>(
    AppointmentFragment::class,
    AppointmentVm::class
) {
    override fun initBinding(): FragmentAppointmentBinding {
        return FragmentAppointmentBinding.inflate(layoutInflater)
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

        vm.initAdapter()
    }

    //---------------------------ViewModel---------------------------

    override fun initViewModel() {
        super.initViewModel()

        observeDate()
    }

    private fun observeDate() {
        vm.aao.currentItem.observe(viewLifecycleOwner){
            item ->
            run {
                binding.vpg2.setCurrentItem(item, true)
                when(item){
                    0 -> {
                        binding.vVpg1.setImageResource(com.czy.appview.R.color.green_200)
                        binding.vVpg2.setImageResource(com.czy.appview.R.color.green_100)
                        binding.vVpg3.setImageResource(com.czy.appview.R.color.green_100)
                    }
                    1 -> {
                        binding.vVpg1.setImageResource(com.czy.appview.R.color.green_100)
                        binding.vVpg2.setImageResource(com.czy.appview.R.color.green_200)
                        binding.vVpg3.setImageResource(com.czy.appview.R.color.green_100)
                    }
                    2 -> {
                        binding.vVpg1.setImageResource(com.czy.appview.R.color.green_100)
                        binding.vVpg2.setImageResource(com.czy.appview.R.color.green_100)
                        binding.vVpg3.setImageResource(com.czy.appview.R.color.green_200)
                    }
                }
            }
        }
    }
}