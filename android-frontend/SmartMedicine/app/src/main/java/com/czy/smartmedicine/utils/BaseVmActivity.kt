package com.czy.smartmedicine.utils

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding
import com.czy.baseutil.viewModel.ViewModelUtil
import com.czy.smartmedicine.MainApplication
import com.czy.smartmedicine.viewModel.base.ApiViewModelFactory
import kotlin.reflect.KClass

abstract class BaseVmActivity<VB : ViewBinding, VM : ViewModel> (
    activityClassType: KClass<out FragmentActivity>,
    private val vmClassType: KClass<VM>
) : FragmentActivity(){

    protected open lateinit var vm: VM
    protected open lateinit var binding: VB

    private val activityName : String = activityClassType.java.name
    protected open val TAG : String = activityName

    abstract fun initBinding(): VB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = initBinding()
        setContentView(binding.root)

        initViewModel()

        initView()

        setListener()
    }

    protected open fun initView(){

    }

    protected open fun setListener() {

    }

    protected open fun initViewModel() {
        val apiViewModelFactory = ApiViewModelFactory(
            MainApplication.getApiRequestImplInstance(),
            MainApplication.getInstance().getMessageSender()
        )

        vm = ViewModelUtil.newViewModel(this, apiViewModelFactory, vmClassType.java)
    }
}