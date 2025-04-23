package com.czy.easysocial.fragment.friends;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;

import com.czy.baseUtilsLib.view.viewPager.GenericPagerAdapter;

public class ViewPagerUserGroupAdapter extends GenericPagerAdapter<ContactUserGroupFragment> {

    public ViewPagerUserGroupAdapter(@NonNull FragmentActivity fragmentActivity, int fragmentCount) {
        super(fragmentActivity, fragmentCount);
    }

    public ViewPagerUserGroupAdapter(@NonNull Fragment fragment, int fragmentCount) {
        super(fragment, fragmentCount);
    }

    public ViewPagerUserGroupAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, int fragmentCount) {
        super(fragmentManager, lifecycle, fragmentCount);
    }

    @Override
    protected ContactUserGroupFragment createFragmentInstance(int position) {
        return new ContactUserGroupFragment(position);
    }
}
