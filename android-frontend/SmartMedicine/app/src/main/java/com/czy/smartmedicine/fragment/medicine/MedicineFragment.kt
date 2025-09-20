package com.czy.smartmedicine.fragment.medicine

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.czy.appview.R
import com.czy.appview.view.medicine.MedicineViewPagerEnum
import com.czy.domain.fragmentActivityAo.medicine.MedicineFAo
import com.czy.smartmedicine.activity.MainActivity
import com.czy.smartmedicine.databinding.FragmentMedicineBinding
import com.czy.smartmedicine.utils.BaseVmFragment
import com.czy.smartmedicine.viewModel.fragment.medicine.MedicineVm


class MedicineFragment : BaseVmFragment<FragmentMedicineBinding, MedicineVm>(
    MedicineFragment::class,
    MedicineVm::class
) {
    override fun initBinding(): FragmentMedicineBinding {
        return FragmentMedicineBinding.inflate(layoutInflater)
    }

    // init
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    // initView
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun setListener() {
        super.setListener()

        // 设置顶部导航栏的点击监听器
        binding.medicineSelectBar.setOnViewPagerBarClickListener { position ->
            vm.fao.currentPositionLd.value = position
            binding.vPager2.setCurrentItem(position, true)
        }

        // 注册页面变化回调
        binding.vPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                vm.fao.currentPositionLd.value = position
            }
        })
    }

    //---------------------------ViewModel---------------------------

    override fun initViewModel() {
        super.initViewModel()

        val fao = MedicineFAo()
        fao.currentPositionLd.value = MedicineViewPagerEnum.APPOINTMENT.index

        vm.init(fao, this)

        // 设置 ViewPager2 的适配器
        binding.vPager2.adapter = vm.medicineViewPagerAdapter

        observeData()
    }

    private fun observeData() {
        vm.fao.currentPositionLd.observe(viewLifecycleOwner){
                position ->
                if (position != null){
                    binding.medicineSelectBar.setCurrentPosition(position)
                    // 避免循环观察
//                    binding.vPager2.setCurrentItem(position, true)
                    if (isAdded){
                        val enum = MedicineViewPagerEnum.values()[position]
                        when (enum) {
                            MedicineViewPagerEnum.APPOINTMENT -> {
                                (requireActivity() as MainActivity).setBaseBarColorRes(
                                    R.color.green_0
                                )
                            }
                            MedicineViewPagerEnum.AI_QUESTION -> {
                                (requireActivity() as MainActivity).setBaseBarColorRes(
                                    R.color.green_100
                                )
                            }
                            MedicineViewPagerEnum.MEDICAL_WIKI -> {
                                (requireActivity() as MainActivity).setBaseBarColorRes(
                                    R.color.green_0
                                )
                            }
                            MedicineViewPagerEnum.MEDICAL_SHOPPING -> {
                                (requireActivity() as MainActivity).setBaseBarColorRes(
                                    R.color.green_0
                                )
                            }
                            MedicineViewPagerEnum.HEALTH_REMINDER -> {
                                (requireActivity() as MainActivity).setBaseBarColorRes(
                                    R.color.green_0
                                )
                            }
                        }
                    }
                }
        }
    }

}