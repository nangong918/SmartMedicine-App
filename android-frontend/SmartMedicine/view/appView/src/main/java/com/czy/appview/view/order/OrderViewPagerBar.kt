package com.czy.appview.view.order

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.czy.appview.R
import com.czy.appview.databinding.OrderViewpagerBarBinding
import com.czy.domain.OnPositionItemClick

class OrderViewPagerBar : ConstraintLayout {

    private var binding: OrderViewpagerBarBinding
    private var currentPosition: Int = OrderViewPagerEnum.APPOINTMENT_ORDER.value
    private var onViewPagerBarClickListener: OnPositionItemClick? = null

    constructor(context: Context) : super(context) {
        binding = OrderViewpagerBarBinding.inflate(LayoutInflater.from(context), this, true)
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        binding = OrderViewpagerBarBinding.inflate(LayoutInflater.from(context), this, true)
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        binding = OrderViewpagerBarBinding.inflate(LayoutInflater.from(context), this, true)
        init()
    }

    private fun init() {
        setOnClickListener()
    }

    private fun setOnClickListener() {
        val linearLayouts = arrayOf(binding.lyClick1, binding.lyClick2)
        for (i in linearLayouts.indices) {
            linearLayouts[i].setOnClickListener {
                currentPosition = OrderViewPagerEnum.values()[i].value
                onViewPagerBarClickListener?.onPositionItemClick(OrderViewPagerEnum.values()[i].value)
                updateUI()
            }
        }
    }

    private fun updateUI() {
        val medicineViewPagerEnum = OrderViewPagerEnum.getByValue(currentPosition)

        binding.tvAppointment.setTextColor(
            if (OrderViewPagerEnum.APPOINTMENT_ORDER == medicineViewPagerEnum) {
                ContextCompat.getColor(context, R.color.green_1000)
            }
            else {
                ContextCompat.getColor(context, R.color.green_900)
            }
        )
        binding.vBar1.visibility = if (OrderViewPagerEnum.APPOINTMENT_ORDER == medicineViewPagerEnum)
            VISIBLE else GONE

        binding.tvPurchase.setTextColor(
            if (OrderViewPagerEnum.PURCHASE_ORDER == medicineViewPagerEnum) {
                ContextCompat.getColor(context, R.color.green_1000)
            }
            else {
                ContextCompat.getColor(context, R.color.green_900)
            }
        )
        binding.vBar2.visibility = if (OrderViewPagerEnum.PURCHASE_ORDER == medicineViewPagerEnum)
            VISIBLE else GONE
    }

}