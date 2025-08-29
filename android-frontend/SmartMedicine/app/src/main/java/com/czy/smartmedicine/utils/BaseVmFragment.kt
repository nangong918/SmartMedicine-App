package com.czy.smartmedicine.utils

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding
import com.czy.baseutil.viewModel.ViewModelUtil
import com.czy.smartmedicine.MainApplication
import com.czy.smartmedicine.viewModel.base.ApiViewModelFactory
import kotlin.reflect.KClass

abstract class BaseVmFragment<VB : ViewBinding, VM : ViewModel>(
    fragmentClassType: KClass<out Fragment>,
    private val vmClassType: KClass<VM>,
) : Fragment() {

    protected open lateinit var vm: VM
    protected val fragmentName: String = fragmentClassType.simpleName ?: "BaseVmFragment"
    protected open val TAG : String = fragmentName

    abstract fun getBinding(): VB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        this.setListener()
        return getBinding().root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 此处binding才生效
        initView()
    }

    protected open fun setListener() {

    }
    protected open fun initView() {}

    protected open fun initViewModel() {
        val apiViewModelFactory = ApiViewModelFactory(
            MainApplication.getApiRequestImplInstance(),
            MainApplication.getInstance().getMessageSender()
        )

        vm = ViewModelUtil.newViewModel(this, apiViewModelFactory, vmClassType.java)
    }
}