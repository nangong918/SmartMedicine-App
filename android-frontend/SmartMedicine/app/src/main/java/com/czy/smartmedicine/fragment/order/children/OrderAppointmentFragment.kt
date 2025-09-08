package com.czy.smartmedicine.fragment.order.children

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.czy.smartmedicine.databinding.FragmentOrderAppointmentBinding
import com.czy.smartmedicine.utils.BaseVmFragment
import com.czy.smartmedicine.viewModel.fragment.order.OrderAppointmentFVm


class OrderAppointmentFragment : BaseVmFragment<FragmentOrderAppointmentBinding, OrderAppointmentFVm>(
    OrderAppointmentFragment::class,
    OrderAppointmentFVm::class
) {
    override fun initBinding(): FragmentOrderAppointmentBinding {
        return FragmentOrderAppointmentBinding.inflate(layoutInflater)
    }

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun setListener() {
        super.setListener()
    }

    override fun initViewModel() {
        super.initViewModel()
    }
}