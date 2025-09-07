package com.czy.smartmedicine.fragment.message.children;

import android.util.Log;
import android.util.SparseArray;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.czy.appview.view.medicine.MedicineViewPagerEnum;
import com.czy.appview.view.message.MessageViewPagerEnum;

public class MessageViewPagerAdapter extends FragmentStateAdapter {

    private static final String TAG = MessageViewPagerAdapter.class.getName();

    public MessageViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public MessageViewPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    public MessageViewPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    private final SparseArray<Fragment> fragmentCache = new SparseArray<>(MedicineViewPagerEnum.getCount());

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (fragmentCache.get(position) != null) {
            return fragmentCache.get(position);
        }

        MessageViewPagerEnum viewPagerEnum = MessageViewPagerEnum.getEnumByIndex(position);
        Fragment fragment = switch (viewPagerEnum) {
            case MESSAGE -> {
                yield new MessageFragment();
            }
            case ADDRESS_BOOK -> {
                yield new AddressBookFragment();
            }
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
        return MedicineViewPagerEnum.getCount();
    }
}
