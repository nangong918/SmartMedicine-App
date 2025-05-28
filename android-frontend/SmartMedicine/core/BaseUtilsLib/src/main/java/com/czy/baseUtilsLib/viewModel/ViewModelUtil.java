package com.czy.baseUtilsLib.viewModel;


import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

/**
 * @author 13225
 * 创建ViewModel
 */
public class ViewModelUtil {

    protected static final String TAG = ViewModel.class.getSimpleName();

    public static <T extends ViewModel> T newViewModel(ViewModelStoreOwner owner, Class<T> modelClass) {
        return new ViewModelProvider(owner).get(modelClass);
    }

    public static <T extends ViewModel> T newViewModel(ViewModelStoreOwner owner, ViewModelProvider.Factory factory, Class<T> modelClass) {
        return new ViewModelProvider(owner, factory).get(modelClass);
    }
}
