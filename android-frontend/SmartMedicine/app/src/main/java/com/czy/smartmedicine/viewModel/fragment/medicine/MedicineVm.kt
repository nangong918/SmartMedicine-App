package com.czy.smartmedicine.viewModel.fragment.medicine

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import com.czy.appcore.network.netty.api.send.SocketMessageSender
import com.czy.dao.networkRepository.ApiRequestImpl
import com.czy.domain.fragmentActivityAo.medicine.MedicineFAo
import com.czy.smartmedicine.fragment.medicine.children.MedicineViewPagerAdapter

open class MedicineVm(
    private val apiRequestImpl: ApiRequestImpl,
    private val socketMessageSender: SocketMessageSender
) : ViewModel(){

    companion object {
        val TAG: String = MedicineVm::class.java.name
    }

    //---------------------------FAo Ld---------------------------

    open lateinit var medicineViewPagerAdapter : MedicineViewPagerAdapter

    open var fao : MedicineFAo = MedicineFAo()

    open fun init(medicineFAo : MedicineFAo, fragment : Fragment){
        this.fao = medicineFAo
        medicineViewPagerAdapter = MedicineViewPagerAdapter(
            fragment.childFragmentManager,
            fragment.lifecycle
        )
    }

    //---------------------------NetWork---------------------------



    //---------------------------Logic---------------------------

}