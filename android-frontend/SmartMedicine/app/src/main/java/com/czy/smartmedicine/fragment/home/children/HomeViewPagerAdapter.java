package com.czy.smartmedicine.fragment.home.children;

import android.util.Log;
import android.util.SparseArray;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.czy.appview.view.home.HomeViewPagerEnum;

public class HomeViewPagerAdapter extends FragmentStateAdapter {

    private static final String TAG = HomeViewPagerAdapter.class.getName();

    public HomeViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public HomeViewPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    public HomeViewPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    private final SparseArray<Fragment> fragmentCache = new SparseArray<>(HomeViewPagerEnum.getHomeViewPagerCount());

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (fragmentCache.get(position) != null) {
            return fragmentCache.get(position);
        }

        HomeViewPagerEnum viewPagerEnum = HomeViewPagerEnum.getHomeViewPagerEnum(position);
        Fragment fragment = switch (viewPagerEnum) {
            case RECOMMEND -> new RecommendFragment();
            case POPULAR -> new PopularFragment();
            case FOLLOW -> new FollowFragment();
            case FRIEND_CIRCLE -> new FriendsCircleFragment();
            default -> {
                Log.w(TAG, "Unexpected value: " + viewPagerEnum);
                throw new IllegalStateException("Unexpected value: " + viewPagerEnum);
            }
        };

        fragmentCache.put(position, fragment);
        return fragment;
    }

    @Override
    public int getItemCount() {
        return HomeViewPagerEnum.getHomeViewPagerCount();
    }
}
