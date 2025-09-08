package com.czy.smartmedicine.viewModel.fragment.medicine.children

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.ViewModel
import com.czy.appcore.network.netty.api.send.SocketMessageSender
import com.czy.appview.view.ImageSliderAdapter
import com.czy.dao.networkRepository.ApiRequestImpl
import com.czy.domain.fragmentActivityAo.medicine.AppointmentFAo


open class AppointmentVm(
    private val apiRequestImpl: ApiRequestImpl,
    private val socketMessageSender: SocketMessageSender
) : ViewModel(){

    companion object {
        val TAG: String = AppointmentVm::class.java.name
    }

    //---------------------------FAo Ld---------------------------

    open lateinit var imageSliderAdapter: ImageSliderAdapter

    open var aao = AppointmentFAo()

    fun initAdapter(){
        aao.imageList = listOf(
            com.czy.appview.R.drawable.round_corners_bg_commend,
            com.czy.appview.R.drawable.round_corners_bg_commend,
            com.czy.appview.R.drawable.round_corners_bg_commend
        )
        imageSliderAdapter = ImageSliderAdapter(aao.imageList)

        startAutoScroll()
    }

    //---------------------------NetWork---------------------------



    //---------------------------Logic---------------------------
    private val handler = Handler(Looper.getMainLooper())
    private fun startAutoScroll() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                aao.currentItem.value = (aao.currentItem.value!! + 1) % aao.imageList.size
                handler.postDelayed(this, 2000) // 2 秒间隔
            }
        }, 2000)
    }

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacksAndMessages(null) // 清除所有回调
    }
}