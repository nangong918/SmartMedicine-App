package com.czy.appcore.network.api.api

import com.czy.appcore.BaseConfig
import com.czy.baseUtilsLib.network.BaseResponse
import com.czy.dal.constant.backEnd.BackEndConstant
import com.czy.dal.dto.http.request.BaseHttpRequest
import com.czy.dal.dto.http.request.FuzzySearchRequest
import com.czy.dal.dto.http.request.GetMyFriendsRequest
import com.czy.dal.dto.http.request.GetSinglePostRequest
import com.czy.dal.dto.http.request.IsRegisterRequest
import com.czy.dal.dto.http.request.LoginUserRequest
import com.czy.dal.dto.http.request.PhoneLoginInfoRequest
import com.czy.dal.dto.http.request.PostPublishRequest
import com.czy.dal.dto.http.request.RecommendPostRequest
import com.czy.dal.dto.http.request.RegisterUserRequest
import com.czy.dal.dto.http.request.SearchUserRequest
import com.czy.dal.dto.http.request.SendSmsRequest
import com.czy.dal.dto.http.request.UserBriefRequest
import com.czy.dal.dto.http.response.FuzzySearchResponse
import com.czy.dal.dto.http.response.GetAddMeRequestListResponse
import com.czy.dal.dto.http.response.GetHandleMyAddUserResponseListResponse
import com.czy.dal.dto.http.response.GetMyFriendsResponse
import com.czy.dal.dto.http.response.IsRegisterResponse
import com.czy.dal.dto.http.response.LoginSignResponse
import com.czy.dal.dto.http.response.PostPublishResponse
import com.czy.dal.dto.http.response.RecommendPostResponse
import com.czy.dal.dto.http.response.SearchUserResponse
import com.czy.dal.dto.http.response.SendSmsResponse
import com.czy.dal.dto.http.response.SinglePostResponse
import com.czy.dal.dto.http.response.UserBriefResponse
import com.czy.dal.dto.http.response.UserRegisterResponse
import com.czy.dal.dto.netty.request.FetchUserMessageRequest
import com.czy.dal.dto.netty.response.ChatUploadFileResponse
import com.czy.dal.dto.netty.response.FetchUserMessageResponse
import com.czy.dal.dto.netty.response.FileDownloadBytesResponse
import com.czy.dal.dto.netty.response.FileUploadResponse
import com.czy.dal.dto.netty.response.UserNewMessageResponse
import com.czy.dal.vo.entity.UserEntityVo
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query


/**
 * 协程接口ApiRequest
 * RxJava -> Kotlin:
 * 使用 suspend：在函数定义前添加 suspend 修饰符，以便可以在协程中调用这些函数。
 * 返回类型：直接返回响应类型而不是 Observable，因为协程会处理异步操作。
 * suspend: 挂起：函数可以在协程中被挂起，这意味着可以在该函数内部执行长时间运行的任务（例如网络请求）而不会阻塞当前线程
 */
interface CoroutineApiRequest {

    //--------------登录注册--------------

    /**
     * 请求发送短信验证码
     * @param request 主要是手机号
     * @return
     */
    @POST(BackEndConstant.USER_RELATION + "/login/sendSms")
    suspend fun sendSms(@Body request: SendSmsRequest): BaseResponse<SendSmsResponse>

    /**
     * 短信验证码登录
     * @param request
     * @return
     */
    @POST(BackEndConstant.USER_RELATION + "/login/smsLogin")
    suspend fun smsLogin(@Body request: PhoneLoginInfoRequest): BaseResponse<LoginSignResponse>

    /**
     * 注册用户
     * @param request
     * @return
     */
    @POST(BackEndConstant.USER_RELATION + "/login/register")
    suspend fun register(@Body request: RegisterUserRequest): BaseResponse<UserRegisterResponse>

    /**
     * 注册上传用户头像信息
     * @param img       头像图片
     * @param phone     手机号
     * @param userId    用户id
     * @return          用户画像
     */
    @Multipart
    @POST(BackEndConstant.USER_RELATION + "/userFile/register")
    suspend fun registerUserUploadImg(
        @Part img: MultipartBody.Part,
        @Part("phone") phone: RequestBody,
        @Part("userId") userId: RequestBody
    ): BaseResponse<UserEntityVo>

    /**
     * 检查手机号是否已经注册了
     * @param request   手机号
     * @return          是否注册了
     */
    @POST(BackEndConstant.USER_RELATION + "/login/isPhoneRegistered")
    suspend fun isPhoneRegistered(@Body request: IsRegisterRequest): BaseResponse<IsRegisterResponse>

    /**
     * 密码登录
     * @param request   手机号
     * @return          登录结果
     */
    @POST(BackEndConstant.USER_RELATION + "/login/pwdLogin")
    suspend fun passwordLogin(@Body request: LoginUserRequest): BaseResponse<LoginSignResponse>

    //--------------用户关系--------------

    /**
     * 搜索用户
     * @param request   账号
     * @return          用户列表
     */
    @POST(BaseConfig.AUTH_TOKEN_PREFIX + BackEndConstant.USER_RELATION + "/relation/searchUser")
    suspend fun searchUsers(@Body request: SearchUserRequest): BaseResponse<SearchUserResponse>

    /**
     * 获取添加我的请求
     * @param request   用户基本信息
     * @return          添加我的请求
     */
    @POST(BaseConfig.AUTH_TOKEN_PREFIX + BackEndConstant.USER_RELATION + "/relation/getAddMeRequestList")
    suspend fun getAddMeRequestList(@Body request: BaseHttpRequest): BaseResponse<GetAddMeRequestListResponse>

    /**
     * 获取处理我的添加用户请求
     * @param request   用户基本信息
     * @return          处理我的添加用户请求
     */
    @POST(BaseConfig.AUTH_TOKEN_PREFIX + BackEndConstant.USER_RELATION + "/relation/getHandleMyAddUserResponseList")
    suspend fun getHandleMyAddUserResponseList(@Body request: BaseHttpRequest): BaseResponse<GetHandleMyAddUserResponseListResponse>

    /**
     * 获取我的全部好友
     * @param request   用户基本信息
     * @return          好友列表
     */
    @POST(BaseConfig.AUTH_TOKEN_PREFIX + BackEndConstant.USER_RELATION + "/relation/getMyFriendList")
    suspend fun getMyFriendList(@Body request: GetMyFriendsRequest): BaseResponse<GetMyFriendsResponse>

    /**
     * 获取与我相关的好友申请数量 [添加我的 + 我添加的]
     * @param request   用户基本信息
     * @return          好友申请数量
     */
    @POST(BaseConfig.AUTH_TOKEN_PREFIX + BackEndConstant.USER_RELATION + "/relation/getMyFriendApplyList")
    suspend fun getMyFriendApplyList(@Body request: BaseHttpRequest): BaseResponse<Int>

    /**
     * 获取用户简略信息
     * @param request   用户基本信息
     * @return          用户简略信息
     */
    @POST(BaseConfig.AUTH_TOKEN_PREFIX + BackEndConstant.POST + "/userBrief/get")
    suspend fun getUserBrief(@Body request: UserBriefRequest): BaseResponse<UserBriefResponse>

    //--------------聊天相关--------------

    /**
     * 拉取用户的全部聊天消息
     * @param request   用户基本信息
     * @return  List<用户消息最新一条消息, 未读消息数量>
     */
    @POST(BaseConfig.AUTH_TOKEN_PREFIX + BackEndConstant.MESSAGE + "/chat/getUserNewMessage")
    suspend fun getUserNewMessage(@Body request: BaseHttpRequest): BaseResponse<UserNewMessageResponse>

    /**
     * 拉取用户和某个用户全部聊天消息
     * @param request 请求获取消息
     * @return 获取消息
     */
    @POST(BaseConfig.AUTH_TOKEN_PREFIX + BackEndConstant.MESSAGE + "/chat/fetchUserMessage")
    suspend fun fetchUserMessage(@Body request: FetchUserMessageRequest): BaseResponse<FetchUserMessageResponse>

    /**
     * 聊天发送图片消息的后续上传oss
     * @param file           文件
     * @param fileId         文件id
     * @param senderId       发送者id
     * @param receiverId     接收者id
     * @return               上传结果（id + url）
     */
    @Multipart
    @POST(BaseConfig.AUTH_TOKEN_PREFIX + BackEndConstant.MESSAGE + "/chatFile/uploadAndSend")
    suspend fun uploadAndSend(
        @Part file: MultipartBody.Part,
        @Part("fileId") fileId: RequestBody,
        @Part("senderId") senderId: RequestBody,
        @Part("receiverId") receiverId: RequestBody
    ): BaseResponse<ChatUploadFileResponse>

    //-------------oss直属文件--------------

    /**
     * 上传文件
     * @param file      file
     * @param name      name
     * @param timestamp 时间戳
     * @return          文件上传响应
     */
    @Multipart
    @POST(BaseConfig.AUTH_TOKEN_PREFIX + BackEndConstant.OSS + "/oss/upload")
    suspend fun fileUpload(
        @Part file: MultipartBody.Part,
        @Part("name") name: RequestBody,
        @Part("timestamp") timestamp: RequestBody
    ): BaseResponse<FileUploadResponse>

    /**
     * 下载图片
     * @param url   url
     * @return      文件下载响应
     */
    @Deprecated("直接去从响应体获取byte[]已成历史，后端把数据写入响应体会出现io阻塞")
    @GET(BackEndConstant.OSS + "/oss/downloadImage")
    suspend fun downloadImage(@Query("url") url: String): BaseResponse<FileDownloadBytesResponse>

    //-------------帖子相关--------------

    /**
     * 获取推荐帖子列表
     * @param request   请求
     * @return          推荐帖子
     */
    @POST(BaseConfig.AUTH_TOKEN_PREFIX + BackEndConstant.RECOMMEND + "/rec/post/get")
    suspend fun getRecommendPosts(@Body request: RecommendPostRequest): BaseResponse<RecommendPostResponse>

    // 前后端联调测试接口：获取随机post
    @POST(BaseConfig.AUTH_TOKEN_PREFIX + BackEndConstant.RECOMMEND + "/rec/post/test")
    suspend fun recommendTestGetRandomPost(@Body request: RecommendPostRequest): BaseResponse<RecommendPostResponse>

    /**
     * 获取单个帖子
     * @param request    帖子id + 页码
     * @return           帖子
     */
    @POST(BaseConfig.AUTH_TOKEN_PREFIX + BackEndConstant.POST + "/post/getPost")
    suspend fun getSinglePost(@Body request: GetSinglePostRequest): BaseResponse<SinglePostResponse>

    /**
     * 发布帖子（首次）
     * @param request   请求
     * @return  发布帖子
     */
    @POST(BaseConfig.AUTH_TOKEN_PREFIX + BackEndConstant.POST + "/post/postPublishFirst")
    suspend fun postPublishFirst(@Body request: PostPublishRequest): BaseResponse<PostPublishResponse>

    /**
     * 发布帖子（首次结束拿到雪花id之后上传file到oss）
     * @param files         文件
     * @param postId        帖子id
     * @param userAccount   用户账号
     * @return              发布帖子
     */
    @POST(BaseConfig.AUTH_TOKEN_PREFIX + BackEndConstant.POST + "/postFile/uploadPost")
    suspend fun uploadPostFile(
        @Part files: List<MultipartBody.Part>,
        @Part("postId") postId: Long,
        @Part("userAccount") userAccount: String
    ): BaseResponse<String>

    //-------------搜索相关--------------

    /**
     * 模糊搜索帖子
     * @param request   请求
     * @return          模糊搜索帖子结果
     */
    @POST(BaseConfig.AUTH_TOKEN_PREFIX + BackEndConstant.SEARCH + "/main/fuzzy")
    suspend fun fuzzySearch(@Body request: FuzzySearchRequest): BaseResponse<FuzzySearchResponse>

    //--------------Test--------------

    @Multipart
    @POST(BackEndConstant.OSS + "/oss/uploadTest")
    suspend fun uploadImageTest(@Part file: MultipartBody.Part): BaseResponse<String>
}