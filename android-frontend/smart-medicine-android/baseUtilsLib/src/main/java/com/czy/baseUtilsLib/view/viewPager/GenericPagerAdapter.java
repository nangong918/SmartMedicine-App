package com.czy.baseUtilsLib.view.viewPager;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * ViewPager的Adapter
 * @param <F>   ViewPager中显示的Fragment
 */
public abstract class GenericPagerAdapter<F extends Fragment> extends FragmentStateAdapter {


    public GenericPagerAdapter(@NonNull FragmentActivity fragmentActivity, int fragmentCount) {
        super(fragmentActivity);
        this.fragmentCount = fragmentCount;
        init();
    }

    public GenericPagerAdapter(@NonNull Fragment fragment, int fragmentCount) {
        super(fragment);
        this.fragmentCount = fragmentCount;
        init();
    }

    public GenericPagerAdapter(@NonNull FragmentManager fragmentManager,
                               @NonNull Lifecycle lifecycle,
                               int fragmentCount) {
        super(fragmentManager, lifecycle);
        this.fragmentCount = fragmentCount;
        init();
    }

    public List<F> fragmentList;
    private final int fragmentCount;

    private void init(){
        fragmentList = new ArrayList<>();
        for(int i = 0; i < fragmentCount; i++){
            fragmentList.add(i,null);
        }
    }

    // 需要在子类中实现具体的 Fragment 创建逻辑
    protected abstract F createFragmentInstance(int position);

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        F fragment = createFragmentInstance(position);
        fragmentList.add(position, fragment);
        return fragment;
    }

    @Override
    public int getItemCount() {
        return fragmentCount;
    }
}
