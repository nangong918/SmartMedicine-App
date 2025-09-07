package com.czy.smartmedicine.fragment.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.czy.smartmedicine.databinding.FragmentOrderMainBinding
import com.czy.smartmedicine.utils.BaseVmFragment
import com.czy.smartmedicine.viewModel.fragment.order.OrderMainVm


class OrderMainFragment : BaseVmFragment<FragmentOrderMainBinding, OrderMainVm>(
    OrderMainFragment::class,
    OrderMainVm::class
) {
    override fun initBinding(): FragmentOrderMainBinding {
        return FragmentOrderMainBinding.inflate(layoutInflater)
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