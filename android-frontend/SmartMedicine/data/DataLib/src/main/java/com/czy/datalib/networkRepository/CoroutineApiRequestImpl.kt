package com.czy.datalib.networkRepository

import com.czy.baseUtilsLib.network.BaseResponse

import com.czy.domain.dto.http.request.BaseHttpRequest
import com.czy.domain.dto.http.request.FuzzySearchRequest
import com.czy.domain.dto.http.request.GetMyFriendsRequest
import com.czy.domain.dto.http.request.GetSinglePostRequest
import com.czy.domain.dto.http.request.IsRegisterRequest
import com.czy.domain.dto.http.request.LoginUserRequest
import com.czy.domain.dto.http.request.PhoneLoginInfoRequest
import com.czy.domain.dto.http.request.PostPublishRequest
import com.czy.domain.dto.http.request.RecommendPostRequest
import com.czy.domain.dto.http.request.RegisterUserRequest
import com.czy.domain.dto.http.request.SearchUserRequest
import com.czy.domain.dto.http.request.UserBriefRequest
import com.czy.domain.dto.http.response.FuzzySearchResponse
import com.czy.domain.dto.http.response.GetAddMeRequestListResponse
import com.czy.domain.dto.http.response.GetHandleMyAddUserResponseListResponse
import com.czy.domain.dto.http.response.GetMyFriendsResponse
import com.czy.domain.dto.http.response.LoginSignResponse
import com.czy.domain.dto.http.response.PostPublishResponse
import com.czy.domain.dto.http.response.RecommendPostResponse
import com.czy.domain.dto.http.response.SearchUserResponse
import com.czy.domain.dto.http.response.SendSmsResponse
import com.czy.domain.dto.http.response.SinglePostResponse
import com.czy.domain.dto.http.response.UserBriefResponse
import com.czy.domain.dto.http.response.UserRegisterResponse
import com.czy.domain.dto.netty.request.FetchUserMessageRequest
import com.czy.domain.dto.netty.response.ChatUploadFileResponse
import com.czy.domain.dto.netty.response.FetchUserMessageResponse
import com.czy.domain.dto.netty.response.FileUploadResponse
import com.czy.domain.dto.netty.response.UserNewMessageResponse
import com.czy.domain.vo.entity.UserEntityVo
import okhttp3.MultipartBody
import okhttp3.RequestBody
import com.czy.appcore.network.api.api.CoroutineApiRequest
import com.czy.baseUtilsLib.network.CoroutineBaseApiRequestImpl
import com.czy.baseUtilsLib.network.OnSuccessCallback
import com.czy.baseUtilsLib.network.OnThrowableCallback
import com.czy.domain.dto.http.request.SendSmsRequest
import com.czy.domain.dto.http.response.IsRegisterResponse

open class CoroutineApiRequestImpl(val mApi: CoroutineApiRequest) : CoroutineBaseApiRequestImpl() {

    //    @POST(BackEndConstant.USER_RELATION + "/login/sendSms")
    //    suspend fun sendSms(@Body request: SendSmsRequest): BaseResponse<SendSmsResponse>
    fun sendSms(request: SendSmsRequest,
                onSuccessCallback: OnSuccessCallback<BaseResponse<SendSmsResponse>>?,
                onThrowableCallback: OnThrowableCallback?){
        sendRequestCallback(
            apiCall = { mApi.sendSms(request) },
            successCallback = onSuccessCallback,
            throwableCallback = onThrowableCallback
        )
    }

    //    @POST(BackEndConstant.USER_RELATION + "/login/smsLogin")
    //    suspend fun smsLogin(@Body request: PhoneLoginInfoRequest): BaseResponse<LoginSignResponse>
    fun smsLogin(request: PhoneLoginInfoRequest,
                 onSuccessCallback: OnSuccessCallback<BaseResponse<LoginSignResponse>>?,
                 onThrowableCallback: OnThrowableCallback?){
        sendRequestCallback(
            apiCall = { mApi.smsLogin(request) },
            successCallback = onSuccessCallback,
            throwableCallback = onThrowableCallback
        )
    }

    //    @POST(BackEndConstant.USER_RELATION + "/login/register")
    //    suspend fun register(@Body request: RegisterUserRequest): BaseResponse<UserRegisterResponse>
    fun register(request: RegisterUserRequest,
                 onSuccessCallback: OnSuccessCallback<BaseResponse<UserRegisterResponse>>?,
                 onThrowableCallback: OnThrowableCallback?){
        sendRequestCallback(
            apiCall = { mApi.register(request) },
            successCallback = onSuccessCallback,
            throwableCallback = onThrowableCallback
        )
    }

    //    @Multipart
    //    @POST(BackEndConstant.USER_RELATION + "/userFile/register")
    //    suspend fun registerUserUploadImg(
    //        @Part img: MultipartBody.Part,
    //        @Part("phone") phone: RequestBody,
    //        @Part("userId") userId: RequestBody
    //    ): BaseResponse<UserEntityVo>
    fun registerUserUploadImg(img: MultipartBody.Part,
                              phone: RequestBody,
                              userId: RequestBody,
                              onSuccessCallback: OnSuccessCallback<BaseResponse<UserEntityVo>>?,
                              onThrowableCallback: OnThrowableCallback?){
        sendRequestCallback(
            apiCall = { mApi.registerUserUploadImg(img, phone, userId) },
            successCallback = onSuccessCallback,
            throwableCallback = onThrowableCallback
        )
    }

    //    @POST(BackEndConstant.USER_RELATION + "/login/isPhoneRegistered")
    //    suspend fun isPhoneRegistered(@Body request: IsRegisterRequest): BaseResponse<IsRegisterResponse>
    fun isPhoneRegistered(request: IsRegisterRequest,
                          onSuccessCallback: OnSuccessCallback<BaseResponse<IsRegisterResponse>>?,
                          onThrowableCallback: OnThrowableCallback?){
        sendRequestCallback(
            apiCall = { mApi.isPhoneRegistered(request) },
            successCallback = onSuccessCallback,
            throwableCallback = onThrowableCallback
        )
    }

    //    @POST(BackEndConstant.USER_RELATION + "/login/pwdLogin")
    //    suspend fun passwordLogin(@Body request: LoginUserRequest): BaseResponse<LoginSignResponse>
    fun passwordLogin(request: LoginUserRequest,
                      onSuccessCallback: OnSuccessCallback<BaseResponse<LoginSignResponse>>?,
                      onThrowableCallback: OnThrowableCallback?){
        sendRequestCallback(
            apiCall = { mApi.passwordLogin(request) },
            successCallback = onSuccessCallback,
            throwableCallback = onThrowableCallback
        )
    }

    //    @POST(BaseConfig.AUTH_TOKEN_PREFIX + BackEndConstant.USER_RELATION + "/relation/searchUser")
    //    suspend fun searchUsers(@Body request: SearchUserRequest): BaseResponse<SearchUserResponse>
    fun searchUsers(request: SearchUserRequest,
                    onSuccessCallback: OnSuccessCallback<BaseResponse<SearchUserResponse>>?,
                    onThrowableCallback: OnThrowableCallback?){
        sendRequestCallback(
            apiCall = { mApi.searchUsers(request) },
            successCallback = onSuccessCallback,
            throwableCallback = onThrowableCallback
        )
    }

    //    @POST(BaseConfig.AUTH_TOKEN_PREFIX + BackEndConstant.USER_RELATION + "/relation/getAddMeRequestList")
    //    suspend fun getAddMeRequestList(@Body request: BaseHttpRequest): BaseResponse<GetAddMeRequestListResponse>
    fun getAddMeRequestList(request: BaseHttpRequest,
                            onSuccessCallback: OnSuccessCallback<BaseResponse<GetAddMeRequestListResponse>>?,
                            onThrowableCallback: OnThrowableCallback?){
        sendRequestCallback(
            apiCall = { mApi.getAddMeRequestList(request) },
            successCallback = onSuccessCallback,
            throwableCallback = onThrowableCallback
        )
    }

    //    @POST(BaseConfig.AUTH_TOKEN_PREFIX + BackEndConstant.USER_RELATION + "/relation/getHandleMyAddUserResponseList")
    //    suspend fun getHandleMyAddUserResponseList(@Body request: BaseHttpRequest): BaseResponse<GetHandleMyAddUserResponseListResponse>
    fun getHandleMyAddUserResponseList(request: BaseHttpRequest,
                                       onSuccessCallback: OnSuccessCallback<BaseResponse<GetHandleMyAddUserResponseListResponse>>?,
                                       onThrowableCallback: OnThrowableCallback?){
        sendRequestCallback(
            apiCall = { mApi.getHandleMyAddUserResponseList(request) },
            successCallback = onSuccessCallback,
            throwableCallback = onThrowableCallback
        )
    }

    //    @POST(BaseConfig.AUTH_TOKEN_PREFIX + BackEndConstant.USER_RELATION + "/relation/getMyFriendList")
    //    suspend fun getMyFriendList(@Body request: GetMyFriendsRequest): BaseResponse<GetMyFriendsResponse>
    fun getMyFriendList(request: GetMyFriendsRequest,
                        onSuccessCallback: OnSuccessCallback<BaseResponse<GetMyFriendsResponse>>?,
                        onThrowableCallback: OnThrowableCallback?){
        sendRequestCallback(
            apiCall = { mApi.getMyFriendList(request) },
            successCallback = onSuccessCallback,
            throwableCallback = onThrowableCallback
        )
    }

    //    @POST(BaseConfig.AUTH_TOKEN_PREFIX + BackEndConstant.USER_RELATION + "/relation/getMyFriendApplyList")
    //    suspend fun getMyFriendApplyList(@Body request: BaseHttpRequest): BaseResponse<Int>
    fun getMyFriendApplyList(request: BaseHttpRequest,
                             onSuccessCallback: OnSuccessCallback<BaseResponse<Int>>?,
                             onThrowableCallback: OnThrowableCallback?){
        sendRequestCallback(
            apiCall = { mApi.getMyFriendApplyList(request) },
            successCallback = onSuccessCallback,
            throwableCallback = onThrowableCallback
        )
    }

    //    @POST(BaseConfig.AUTH_TOKEN_PREFIX + BackEndConstant.POST + "/userBrief/get")
    //    suspend fun getUserBrief(@Body request: UserBriefRequest): BaseResponse<UserBriefResponse>
    fun getUserBrief(request: UserBriefRequest,
                     onSuccessCallback: OnSuccessCallback<BaseResponse<UserBriefResponse>>?,
                     onThrowableCallback: OnThrowableCallback?){
        sendRequestCallback(
            apiCall = { mApi.getUserBrief(request) },
            successCallback = onSuccessCallback,
            throwableCallback = onThrowableCallback
        )
    }

    //    @POST(BaseConfig.AUTH_TOKEN_PREFIX + BackEndConstant.MESSAGE + "/chat/getUserNewMessage")
    //    suspend fun getUserNewMessage(@Body request: BaseHttpRequest): BaseResponse<UserNewMessageResponse>
    fun getUserNewMessage(request: BaseHttpRequest,
                          onSuccessCallback: OnSuccessCallback<BaseResponse<UserNewMessageResponse>>?,
                          onThrowableCallback: OnThrowableCallback?){
        sendRequestCallback(
            apiCall = { mApi.getUserNewMessage(request) },
            successCallback = onSuccessCallback,
            throwableCallback = onThrowableCallback
        )
    }

    //    @POST(BaseConfig.AUTH_TOKEN_PREFIX + BackEndConstant.MESSAGE + "/chat/fetchUserMessage")
    //    suspend fun fetchUserMessage(@Body request: FetchUserMessageRequest): BaseResponse<FetchUserMessageResponse>
    fun fetchUserMessage(request: FetchUserMessageRequest,
                         onSuccessCallback: OnSuccessCallback<BaseResponse<FetchUserMessageResponse>>?,
                         onThrowableCallback: OnThrowableCallback?){
        sendRequestCallback(
            apiCall = { mApi.fetchUserMessage(request) },
            successCallback = onSuccessCallback,
            throwableCallback = onThrowableCallback
        )
    }

    //    @Multipart
    //    @POST(BaseConfig.AUTH_TOKEN_PREFIX + BackEndConstant.MESSAGE + "/chatFile/uploadAndSend")
    //    suspend fun uploadAndSend(
    //        @Part file: MultipartBody.Part,
    //        @Part("fileId") fileId: RequestBody,
    //        @Part("senderId") senderId: RequestBody,
    //        @Part("receiverId") receiverId: RequestBody
    //    ): BaseResponse<ChatUploadFileResponse>
    fun uploadAndSend(file: MultipartBody.Part,
                      fileId: RequestBody,
                      senderId: RequestBody,
                      receiverId: RequestBody,
                      onSuccessCallback: OnSuccessCallback<BaseResponse<ChatUploadFileResponse>>?,
                      onThrowableCallback: OnThrowableCallback?){
        sendRequestCallback(
            apiCall = { mApi.uploadAndSend(file, fileId, senderId, receiverId) },
            successCallback = onSuccessCallback,
            throwableCallback = onThrowableCallback
        )
    }

    //    @Multipart
    //    @POST(BaseConfig.AUTH_TOKEN_PREFIX + BackEndConstant.OSS + "/oss/upload")
    //    suspend fun fileUpload(
    //        @Part file: MultipartBody.Part,
    //        @Part("name") name: RequestBody,
    //        @Part("timestamp") timestamp: RequestBody
    //    ): BaseResponse<FileUploadResponse>
    @Deprecated(message = "直接去从响应体获取byte[]已成历史，后端把数据写入响应体会出现io阻塞")
    fun fileUpload(file: MultipartBody.Part,
                   name: RequestBody,
                   timestamp: RequestBody,
                   onSuccessCallback: OnSuccessCallback<BaseResponse<FileUploadResponse>>?,
                   onThrowableCallback: OnThrowableCallback?){
        sendRequestCallback(
            apiCall = { mApi.fileUpload(file, name, timestamp) },
            successCallback = onSuccessCallback,
            throwableCallback = onThrowableCallback
        )
    }

    //    @POST(BaseConfig.AUTH_TOKEN_PREFIX + BackEndConstant.RECOMMEND + "/rec/post/get")
    //    suspend fun getRecommendPosts(@Body request: RecommendPostRequest): BaseResponse<RecommendPostResponse>
    fun getRecommendPosts(request: RecommendPostRequest,
                          onSuccessCallback: OnSuccessCallback<BaseResponse<RecommendPostResponse>>?,
                          onThrowableCallback: OnThrowableCallback?){
        sendRequestCallback(
            apiCall = { mApi.getRecommendPosts(request) },
            successCallback = onSuccessCallback,
            throwableCallback = onThrowableCallback
        )
    }

    //    @POST(BaseConfig.AUTH_TOKEN_PREFIX + BackEndConstant.RECOMMEND + "/rec/post/test")
    //    suspend fun recommendTestGetRandomPost(@Body request: RecommendPostRequest): BaseResponse<RecommendPostResponse>
    fun recommendTestGetRandomPost(request: RecommendPostRequest,
                                   onSuccessCallback: OnSuccessCallback<BaseResponse<RecommendPostResponse>>?,
                                   onThrowableCallback: OnThrowableCallback?){
        sendRequestCallback(
            apiCall = { mApi.recommendTestGetRandomPost(request) },
            successCallback = onSuccessCallback,
            throwableCallback = onThrowableCallback
        )
    }

    //    @POST(BaseConfig.AUTH_TOKEN_PREFIX + BackEndConstant.POST + "/post/getPost")
    //    suspend fun getSinglePost(@Body request: GetSinglePostRequest): BaseResponse<SinglePostResponse>
    fun getSinglePost(request: GetSinglePostRequest,
                      onSuccessCallback: OnSuccessCallback<BaseResponse<SinglePostResponse>>?,
                      onThrowableCallback: OnThrowableCallback?){
        sendRequestCallback(
            apiCall = { mApi.getSinglePost(request) },
            successCallback = onSuccessCallback,
            throwableCallback = onThrowableCallback
        )
    }

    //    @POST(BaseConfig.AUTH_TOKEN_PREFIX + BackEndConstant.POST + "/post/postPublishFirst")
    //    suspend fun postPublishFirst(@Body request: PostPublishRequest): BaseResponse<PostPublishResponse>
    fun postPublishFirst(request: PostPublishRequest,
                         onSuccessCallback: OnSuccessCallback<BaseResponse<PostPublishResponse>>?,
                         onThrowableCallback: OnThrowableCallback?){
        sendRequestCallback(
            apiCall = { mApi.postPublishFirst(request) },
            successCallback = onSuccessCallback,
            throwableCallback = onThrowableCallback
        )
    }

    //    @POST(BaseConfig.AUTH_TOKEN_PREFIX + BackEndConstant.POST + "/postFile/uploadPost")
    //    suspend fun uploadPostFile(
    //        @Part files: List<MultipartBody.Part>,
    //        @Part("postId") postId: Long,
    //        @Part("userAccount") userAccount: String
    //    ): BaseResponse<String>
    fun uploadPostFile(files: List<MultipartBody.Part>,
                       postId: Long,
                       userAccount: String,
                       onSuccessCallback: OnSuccessCallback<BaseResponse<String>>?,
                       onThrowableCallback: OnThrowableCallback?){
        sendRequestCallback(
            apiCall = { mApi.uploadPostFile(files, postId, userAccount) },
            successCallback = onSuccessCallback,
            throwableCallback = onThrowableCallback
        )
    }

    //    @POST(BaseConfig.AUTH_TOKEN_PREFIX + BackEndConstant.SEARCH + "/main/fuzzy")
    //    suspend fun fuzzySearch(@Body request: FuzzySearchRequest): BaseResponse<FuzzySearchResponse>
    fun fuzzySearch(request: FuzzySearchRequest,
                    onSuccessCallback: OnSuccessCallback<BaseResponse<FuzzySearchResponse>>?,
                    onThrowableCallback: OnThrowableCallback?){
        sendRequestCallback(
            apiCall = { mApi.fuzzySearch(request) },
            successCallback = onSuccessCallback,
            throwableCallback = onThrowableCallback
        )
    }

    //    @Multipart
    //    @POST(BackEndConstant.OSS + "/oss/uploadTest")
    //    suspend fun uploadImageTest(@Part file: MultipartBody.Part): BaseResponse<String>
    fun uploadImageTest(file: MultipartBody.Part,
                        onSuccessCallback: OnSuccessCallback<BaseResponse<String>>?,
                        onThrowableCallback: OnThrowableCallback?){
        sendRequestCallback(
            apiCall = { mApi.uploadImageTest(file) },
            successCallback = onSuccessCallback,
            throwableCallback = onThrowableCallback
        )
    }

}