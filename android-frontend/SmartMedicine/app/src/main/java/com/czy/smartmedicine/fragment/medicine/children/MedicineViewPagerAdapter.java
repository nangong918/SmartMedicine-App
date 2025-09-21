package com.czy.smartmedicine.fragment.medicine.children;

import android.util.Log;
import android.util.SparseArray;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.czy.appview.view.medicine.MedicineViewPagerEnum;

public class MedicineViewPagerAdapter extends FragmentStateAdapter {

    private static final String TAG = MedicineViewPagerAdapter.class.getName();

    public MedicineViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public MedicineViewPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    public MedicineViewPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    private final SparseArray<Fragment> fragmentCache = new SparseArray<>(MedicineViewPagerEnum.getCount());

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (fragmentCache.get(position) != null) {
            return fragmentCache.get(position);
        }

        MedicineViewPagerEnum viewPagerEnum = MedicineViewPagerEnum.getEnumByIndex(position);
        Fragment fragment = switch (viewPagerEnum) {
            case APPOINTMENT -> new AppointmentFragment();
            case AI_QUESTION -> new AiQuestionFragment();
            case MEDICAL_WIKI -> new MedicalWikiFragment();
            case MEDICAL_SHOPPING -> new MedicalShoppingFragment();
            case HEALTH_REMINDER -> new HealthReminderFragment();
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
