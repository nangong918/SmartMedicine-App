package com.czy.smartmedicine.fragment.order

import android.util.Log
import android.util.SparseArray
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.czy.appview.view.medicine.order.OrderViewPagerEnum

class OrderViewPagerAdapter: FragmentStateAdapter {

    companion object{
        val TAG: String = OrderViewPagerAdapter::class.java.name
    }

    constructor(fragment: Fragment) : super(fragment)
    constructor(fragmentActivity: FragmentActivity) : super(fragmentActivity)
    constructor(fragmentManager: FragmentManager, lifecycle: Lifecycle) : super(fragmentManager, lifecycle)

    private val fragmentCache = SparseArray<Fragment>(OrderViewPagerEnum.values().size)

    override fun createFragment(position: Int): Fragment {
        if (fragmentCache[position] != null) {
            return fragmentCache[position]
        }

        val viewPagerEnum = OrderViewPagerEnum.getByValue(position)
        val fragment = when (viewPagerEnum) {
            OrderViewPagerEnum.APPOINTMENT_ORDER -> {
                OrderAppointmentFragment()
            }
            OrderViewPagerEnum.PURCHASE_ORDER -> {
                OrderPurchaseFragment()
            }
            else -> {
                Log.w(TAG, "Invalid viewPagerEnum: $viewPagerEnum")
                throw IllegalArgumentException("Invalid viewPagerEnum: $viewPagerEnum")
            }
        }

        fragmentCache.put(position, fragment)
        return fragment
    }

    override fun getItemCount(): Int {
        return OrderViewPagerEnum.values().size
    }


}