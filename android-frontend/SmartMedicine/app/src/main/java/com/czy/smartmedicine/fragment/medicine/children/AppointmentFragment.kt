package com.czy.smartmedicine.fragment.medicine.children

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.czy.smartmedicine.activity.AppointmentActivity
import com.czy.smartmedicine.databinding.FragmentAppointmentBinding
import com.czy.smartmedicine.utils.BaseVmFragment
import com.czy.smartmedicine.viewModel.fragment.medicine.children.AppointmentFVm
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class AppointmentFragment : BaseVmFragment<FragmentAppointmentBinding, AppointmentFVm>(
    AppointmentFragment::class,
    AppointmentFVm::class
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

        binding.vpg2.adapter = vm.imageSliderAdapter
    }

    override fun setListener() {
        super.setListener()

        binding.btnSearch.setOnClickListener({
            val intent = Intent(activity, AppointmentActivity::class.java)
            startActivity(intent)
        })
    }

    //---------------------------ViewModel---------------------------

    override fun initViewModel() {
        super.initViewModel()

        vm.aao.province.value = "广东"
        vm.aao.city.value = "深圳"
        vm.aao.area.value = "南山"
        vm.aao.date.value = LocalDateTime.now()
        vm.aao.department.value = "皮肤科"

        vm.initAdapter()

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

        // location
        vm.aao.province.observe(viewLifecycleOwner) {
            province ->
            val city = vm.aao.city.value
            val area = vm.aao.area.value
            val location = "$province-$city-$area"
            binding.tvLocation.text = location
        }
        vm.aao.city.observe(viewLifecycleOwner) {
            city ->
            val province = vm.aao.province.value
            val area = vm.aao.area.value
            val location = "$province-$city-$area"
            binding.tvLocation.text = location
        }
        vm.aao.area.observe(viewLifecycleOwner) {
            area ->
            val province = vm.aao.province.value
            val city = vm.aao.city.value
            val location = "$province-$city-$area"
            binding.tvLocation.text = location
        }

        // date
        vm.aao.date.observe(viewLifecycleOwner) {
            date ->
            val formatter = DateTimeFormatter.ofPattern("MM月dd日")
            val dateStr = date.format(formatter)
            binding.tvDate.text = dateStr
        }

        // department
        vm.aao.department.observe(viewLifecycleOwner) {
            department ->
            binding.tvDepartment.text = department
        }
    }
}