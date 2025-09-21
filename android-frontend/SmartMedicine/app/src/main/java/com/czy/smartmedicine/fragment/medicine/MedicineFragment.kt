package com.czy.smartmedicine.fragment.medicine

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.viewpager2.widget.ViewPager2
import com.czy.appview.R
import com.czy.appview.view.medicine.MedicineViewPagerEnum
import com.czy.domain.fragmentActivityAo.medicine.MedicineFAo
import com.czy.smartmedicine.activity.MainActivity
import com.czy.smartmedicine.databinding.FragmentMedicineBinding
import com.czy.smartmedicine.utils.BaseVmFragment
import com.czy.smartmedicine.viewModel.fragment.medicine.MedicineVm


open class MedicineFragment : BaseVmFragment<FragmentMedicineBinding, MedicineVm>(
    MedicineFragment::class,
    MedicineVm::class
) {

    private var initPosition = MedicineViewPagerEnum.APPOINTMENT.index
    private var isFirstOnPageSelected = true

    fun setInitPosition(position: Int){
        initPosition = position
        Log.i(TAG, "setInitPosition::initPosition: $initPosition")
    }

    override fun onResume() {
        super.onResume()
        Log.i(TAG, "onResume::initPosition: $initPosition")
        if (vm.fao.currentPositionLd.value != initPosition){
            vm.fao.currentPositionLd.value = initPosition
            binding.vPager2.setCurrentItem(vm.fao.currentPositionLd.value!!, true)
        }
    }

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
                if (isFirstOnPageSelected){
                    isFirstOnPageSelected = false;
                    return@onPageSelected
                }
                vm.fao.currentPositionLd.value = position
            }
        })
    }

    //---------------------------ViewModel---------------------------

    override fun initViewModel() {
        super.initViewModel()

        val fao = MedicineFAo()
        fao.currentPositionLd.value = initPosition

        Log.i(TAG, "initPosition1: ${fao.currentPositionLd.value}")
        vm.init(fao, this)
        Log.i(TAG, "initPosition2: ${vm.fao.currentPositionLd.value}")

        observeData()

        // 设置 ViewPager2 的适配器
        binding.vPager2.adapter = vm.medicineViewPagerAdapter

//        val handler = Handler(Looper.getMainLooper())
//        handler.postDelayed({
//            binding.vPager2.setCurrentItem(initPosition, true)
//        }, 1000L) // 延迟1秒

        binding.vPager2.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener{
            override fun onGlobalLayout() {
                // 布局完成后设置当前项
                Log.i(TAG, "viewTreeObserver: ${vm.fao.currentPositionLd.value}")
                binding.vPager2.setCurrentItem(vm.fao.currentPositionLd.value!!, true)
                // 移除监听器，避免重复调用
                binding.vPager2.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }

        })
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