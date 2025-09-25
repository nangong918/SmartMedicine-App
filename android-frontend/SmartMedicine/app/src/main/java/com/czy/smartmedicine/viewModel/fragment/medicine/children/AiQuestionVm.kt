package com.czy.smartmedicine.viewModel.fragment.medicine.children

import android.content.Context
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.czy.appcore.BaseConfig
import com.czy.appcore.network.api.handle.SyncRequestCallback
import com.czy.appcore.network.netty.api.send.SocketMessageSender
import com.czy.appview.view.chatMessage.ChatMessageAdapter
import com.czy.baseutil.date.DateUtils
import com.czy.baseutil.network.BaseResponse
import com.czy.baseutil.ui.ToastUtils
import com.czy.dao.networkRepository.ApiRequestImpl
import com.czy.domain.dto.http.request.QuestionRequest
import com.czy.domain.fragmentActivityAo.chat.ChatVo
import com.czy.domain.fragmentActivityAo.medicine.AiQuestionFAo
import com.czy.domain.vo.entity.chat.ChatMessageItemVo
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import java.io.IOException
import java.time.LocalDateTime
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

    var adapter: ChatMessageAdapter? = null

    //---------------------------NetWork---------------------------

    private val gson = Gson()

    fun doSendQuestion(context: Context, callback: SyncRequestCallback){
        val question = fao.inputText.value
        if (TextUtils.isEmpty(question)){
            ToastUtils.showToastActivity(context, "请输入问题")
            callback.onThrowable(Throwable("请输入问题"))
            return
        }

        val chatMessageItemVo = ChatMessageItemVo()
        chatMessageItemVo.content = question
        chatMessageItemVo.time = DateUtils.yyyyMMddHHmmssToString(LocalDateTime.now())
        chatMessageItemVo.viewType = ChatMessageItemVo.VIEW_TYPE_SENDER

        fao.chatList.add(chatMessageItemVo)
        adapter?.setCurrentList(fao.chatList)
//        fao.chatCountLd.value = fao.chatList.size

        val request = QuestionRequest()
        request.question = question

        // 使用 Gson 序列化请求体
        val json = gson.toJson(request)

        // 创建 OkHttpClient 实例
        val client = OkHttpClient()

        // 创建请求体
        val requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json)

        val url: String = "http://" + BaseConfig.LOCAL_DNS.trim() + ":52333/ai/question"
        Log.i(TAG, "url: $url")

        // 创建请求
        val httpRequest = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        // 发起请求
        client.newCall(httpRequest).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback.onThrowable(e)
            }

            override fun onResponse(call: Call, response: Response) {
                handleSendQuestion(call, response, callback)
            }
        })
    }

    private fun handleSendQuestion(call: Call, response: Response, callback: SyncRequestCallback){
        if (response.isSuccessful) {
            response.body()?.let { responseBody ->
                val responseData = responseBody.string()

                Log.i(TAG, "Response: $responseData")

                // 使用 Gson 解析响应数据
                val baseResponseType = object : TypeToken<BaseResponse<String>>() {}.type
                val baseResponse: BaseResponse<String> = gson.fromJson(responseData, baseResponseType)

                // 处理解析后的数据
                if (baseResponse.code == "200") {

                    val chatMessageItemVo = ChatMessageItemVo()
                    chatMessageItemVo.content = baseResponse.data
                    chatMessageItemVo.time = DateUtils.yyyyMMddHHmmssToString(LocalDateTime.now())
                    chatMessageItemVo.viewType = ChatMessageItemVo.VIEW_TYPE_RECEIVER

                    fao.chatList.add(chatMessageItemVo)
                    adapter?.setCurrentList(fao.chatList)
//                    fao.chatCountLd.value = fao.chatList.size

                    callback.onAllRequestSuccess()
                }
                else {
                    callback.onThrowable(Throwable(SyncRequestCallback.RESPONSE_BASE_ERROR))
                }
            }
        }
        else {
            callback.onThrowable(Throwable(SyncRequestCallback.RESPONSE_BASE_ERROR))
        }
    }

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