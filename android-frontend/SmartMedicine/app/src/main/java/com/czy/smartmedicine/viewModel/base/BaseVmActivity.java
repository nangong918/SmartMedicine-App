package com.czy.smartmedicine.viewModel.base;


import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewbinding.ViewBinding;

import com.czy.baseutil.activity.BaseActivity;
import com.czy.smartmedicine.MainApplication;


/**
 * 解决ViewBinding重复代码    （通过反射实现）
 * @param <VB>     视图绑定类型
 * @param <VM>     视图模型类型
 */
public abstract class BaseVmActivity<VB extends ViewBinding, VM extends ViewModel> extends BaseActivity<VB> {

    protected VB binding;
    protected VM vm;
    private final Class<VM> viewModelClass;

    public abstract VB getBinding();

    public BaseVmActivity(Class<? extends FragmentActivity> classType, Class<VM> viewModelClass){
        super(classType);
        this.viewModelClass = viewModelClass;
    }

    protected void initViewModel() {
        ApiViewModelFactory apiViewModelFactory = new ApiViewModelFactory(
                MainApplication.getApiRequestImplInstance(),
                MainApplication.getInstance().getMessageSender()
        );

        // 使用 ViewModelProvider 创建 ViewModel 实例
        vm = new ViewModelProvider(this, apiViewModelFactory).get(viewModelClass);
    }
}
