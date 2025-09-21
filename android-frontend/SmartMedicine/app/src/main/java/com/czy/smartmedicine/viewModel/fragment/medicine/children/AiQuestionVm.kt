package com.czy.smartmedicine.viewModel.fragment.medicine.children

import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.czy.appcore.network.netty.api.send.SocketMessageSender
import com.czy.dao.networkRepository.ApiRequestImpl
import com.czy.domain.fragmentActivityAo.chat.ChatVo
import com.czy.domain.fragmentActivityAo.medicine.AiQuestionFAo
import java.util.Optional


open class AiQuestionVm(
    private val apiRequestImpl: ApiRequestImpl,
    private val socketMessageSender: SocketMessageSender
) : ViewModel(){

    companion object {
        val TAG: String = AiQuestionVm::class.java.name
    }

    //---------------------------FAo Ld---------------------------

    open var fao = AiQuestionFAo()

    //---------------------------NetWork---------------------------



    //---------------------------Logic---------------------------

    fun getTextWatcher(): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.isNotEmpty()) {
                    fao.inputText.value = s.toString()
                }
            }

            override fun afterTextChanged(s: Editable) {
            }
        }
    }

}