package com.czy.appcore.network.api.api;

import com.czy.appcore.BaseConfig;
import com.czy.baseutil.network.BaseResponse;
import com.czy.domain.constant.backEnd.BackEndConstant;
import com.czy.domain.dto.http.request.AppointmentDoctorRequest;
import com.czy.domain.dto.http.request.BaseHttpRequest;
import com.czy.domain.dto.http.request.FuzzySearchRequest;
import com.czy.domain.dto.http.request.GetMyFriendsRequest;
import com.czy.domain.dto.http.request.GetRegisterAppointmentListRequest;
import com.czy.domain.dto.http.request.GetSinglePostRequest;
import com.czy.domain.dto.http.request.IsRegisterRequest;
import com.czy.domain.dto.http.request.LoginUserRequest;
import com.czy.domain.dto.http.request.PhoneLoginInfoRequest;
import com.czy.domain.dto.http.request.PostPublishRequest;
import com.czy.domain.dto.http.request.RecommendPostRequest;
import com.czy.domain.dto.http.request.RegisterUserRequest;
import com.czy.domain.dto.http.request.SearchUserRequest;
import com.czy.domain.dto.http.request.SendSmsRequest;
import com.czy.domain.dto.http.request.UserBriefRequest;
import com.czy.domain.dto.http.response.AppointmentDoctorResponse;
import com.czy.domain.dto.http.response.FuzzySearchResponse;
import com.czy.domain.dto.http.response.GetAddMeRequestListResponse;
import com.czy.domain.dto.http.response.GetAllRegisterAppointmentDateResponse;
import com.czy.domain.dto.http.response.GetHandleMyAddUserResponseListResponse;
import com.czy.domain.dto.http.response.GetMyFriendsResponse;
import com.czy.domain.dto.http.response.GetRegisterAppointmentListResponse;
import com.czy.domain.dto.http.response.IsRegisterResponse;
import com.czy.domain.dto.http.response.LoginSignResponse;
import com.czy.domain.dto.http.response.PostPublishResponse;
import com.czy.domain.dto.http.response.RecommendPostResponse;
import com.czy.domain.dto.http.response.SearchUserResponse;
import com.czy.domain.dto.http.response.SendSmsResponse;
import com.czy.domain.dto.http.response.SinglePostResponse;
import com.czy.domain.dto.http.response.UserBriefResponse;
import com.czy.domain.dto.http.response.UserRegisterResponse;
import com.czy.domain.dto.netty.request.FetchUserMessageRequest;
import com.czy.domain.dto.netty.response.ChatUploadFileResponse;
import com.czy.domain.dto.netty.response.FetchUserMessageResponse;
import com.czy.domain.dto.netty.response.FileDownloadBytesResponse;
import com.czy.domain.dto.netty.response.FileUploadResponse;
import com.czy.domain.dto.netty.response.UserNewMessageResponse;
import com.czy.domain.vo.entity.UserEntityVo;
import com.czy.domain.vo.entity.home.PostVo;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

/**
 * @author 13225
 * 描述: 网络请求接口
 * 网络值不能交给livedata触发，而是接口，因为livedata相同结果不触发
 * 但是resultLiveData.setValue(newData); 每次更新都会通知观察者
 */
public interface ApiRequest {

    //--------------登录注册--------------

    /**
     * 请求发送短信验证码
     * @param request 主要是手机号
     * @return
     */
    @POST(BackEndConstant.USER_RELATION + "/login/sendSms")
    Observable<BaseResponse<SendSmsResponse>> sendSms(@Body SendSmsRequest request);

    /**
     * 短信验证码登录
     * @param request
     * @return
     */
    @POST(BackEndConstant.USER_RELATION + "/login/smsLogin")
    Observable<BaseResponse<LoginSignResponse>> smsLogin(@Body PhoneLoginInfoRequest request);

    /**
     * 注册用户
     * @param request
     * @return
     */
    @POST(BackEndConstant.USER_RELATION + "/login/register")
    Observable<BaseResponse<UserRegisterResponse>> register(@Body RegisterUserRequest request);

    /**
     * 注册上传用户头像信息
     * @param img       头像图片
     * @param phone     手机号
     * @param userId    用户id
     * @return          用户画像
     */
    @Multipart
    @POST(BackEndConstant.USER_RELATION + "/userFile/register")
    Observable<BaseResponse<UserEntityVo>> registerUserUploadImg(
            // paramName: img
            @Part MultipartBody.Part img,
            @Part("phone") RequestBody phone,
            @Part("userId") RequestBody userId
    );

    /**
     * 检查手机号是否已经注册了
     * @param request   手机号
     * @return          是否注册了
     */
    @POST(BackEndConstant.USER_RELATION + "/login/isPhoneRegistered")
    Observable<BaseResponse<IsRegisterResponse>> isPhoneRegistered(@Body IsRegisterRequest request);

    /**
     * 密码登录
     * @param request   手机号
     * @return          登录结果
     */
    @POST(BackEndConstant.USER_RELATION + "/login/pwdLogin")
    Observable<BaseResponse<LoginSignResponse>> passwordLogin(@Body LoginUserRequest request);

    //--------------用户关系--------------

    /**
     * 搜索用户
     * @param request   账号
     * @return          用户列表
     */
    @POST(BaseConfig.AUTH_TOKEN_PREFIX + BackEndConstant.USER_RELATION + "/relation/searchUser")
    Observable<BaseResponse<SearchUserResponse>> searchUsers(@Body SearchUserRequest request);

    /**
     * 获取添加我的请求
     * @param request   用户基本信息
     * @return          添加我的请求
     */
    @POST(BaseConfig.AUTH_TOKEN_PREFIX + BackEndConstant.USER_RELATION + "/relation/getAddMeRequestList")
    Observable<BaseResponse<GetAddMeRequestListResponse>> getAddMeRequestList(@Body BaseHttpRequest request);

    /**
     * 获取处理我的添加用户请求
     * @param request   用户基本信息
     * @return          处理我的添加用户请求
     */
    @POST(BaseConfig.AUTH_TOKEN_PREFIX + BackEndConstant.USER_RELATION + "/relation/getHandleMyAddUserResponseList")
    Observable<BaseResponse<GetHandleMyAddUserResponseListResponse>> getHandleMyAddUserResponseList(@Body BaseHttpRequest request);

    /**
     * 获取我的全部好友
     * @param request   用户基本信息
     * @return          好友列表
     */
    @POST(BaseConfig.AUTH_TOKEN_PREFIX + BackEndConstant.USER_RELATION + "/relation/getMyFriendList")
    Observable<BaseResponse<GetMyFriendsResponse>> getMyFriendList(@Body GetMyFriendsRequest request);

    /**
     * 获取与我相关的好友申请数量 [添加我的 + 我添加的]
     * @param request   用户基本信息
     * @return          好友申请数量
     */
    @POST(BaseConfig.AUTH_TOKEN_PREFIX + BackEndConstant.USER_RELATION + "/relation/getMyFriendApplyList")
    Observable<BaseResponse<Integer>> getMyFriendApplyList(@Body BaseHttpRequest request);

    /**
     * 获取用户简略信息
     * @param request   用户基本信息
     * @return          用户简略信息
     */
    @POST(BaseConfig.AUTH_TOKEN_PREFIX + BackEndConstant.POST + "/userBrief/get")
    Observable<BaseResponse<UserBriefResponse>> getUserBrief(@Body UserBriefRequest request);

    //--------------聊天相关--------------

    /**
     * Message Fragment
     * 拉取用户的全部聊天消息(限制200条，超过就流式传输)：某个用户跟所有用户的1条最新消息List
     * @param request   用户基本信息
     * @return  List<用户消息最新一条消息, 未读消息数量>
     */
    @POST(BaseConfig.AUTH_TOKEN_PREFIX + BackEndConstant.MESSAGE + "/chat/getUserNewMessage")
    Observable<BaseResponse<UserNewMessageResponse>> getUserNewMessage(@Body BaseHttpRequest request);

    /**
     * Chat Activity
     * 拉取用户和某个用户全部聊天消息(分页：一次拉取50条最新聊天消息)
     * @param request 请求获取消息：timestampIndex 消息起始索引；消息条数messageCount（max 200）
     * @return 获取消息
     */
    @POST(BaseConfig.AUTH_TOKEN_PREFIX + BackEndConstant.MESSAGE + "/chat/fetchUserMessage")
    Observable<BaseResponse<FetchUserMessageResponse>> fetchUserMessage(@Body FetchUserMessageRequest request);

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
    Observable<BaseResponse<ChatUploadFileResponse>> uploadAndSend(
            @Part MultipartBody.Part file,
            @Part("fileId") RequestBody fileId,
            @Part("senderId") RequestBody senderId,
            @Part("receiverId") RequestBody receiverId
    );

    //-------------oss直属文件--------------

    /**
     * 上传文件
     * @param file      file
     * @param name      name
     * @param timestamp 时间戳
     * @return          文件上传响应
     */
    //@Part 注解用于标识 Multipart 请求体的一部分,这里的 file 就是文件部分
    @Deprecated(since = "2025/8/1: 直接去从响应体获取byte[]已成历史，后端把数据写入响应体会出现io阻塞")
    @Multipart
    @POST(BaseConfig.AUTH_TOKEN_PREFIX + BackEndConstant.OSS + "/oss/upload")
    Observable<BaseResponse<FileUploadResponse>> fileUpload(
            // paramName: file
            @Part MultipartBody.Part file,
            @Part("name") RequestBody name,
            @Part("timestamp") RequestBody timestamp
    );

    /**
     * 下载图片
     * @param url   url
     * @return      文件下载响应
     */
    @Deprecated(since = "2025/8/1: 直接去从响应体获取byte[]已成历史，后端把数据写入响应体会出现io阻塞")
    @GET(BackEndConstant.OSS + "/oss/downloadImage")
    Observable<BaseResponse<FileDownloadBytesResponse>> downloadImage(@Query("url") String url);

    //-------------帖子相关--------------

    /**
     * 获取推荐帖子列表
     * @param request   请求
     * @return          推荐帖子
     */
    @POST(BaseConfig.AUTH_TOKEN_PREFIX + BackEndConstant.POST + "/rec/post/get")
    Observable<BaseResponse<RecommendPostResponse>> getRecommendPosts(@Body RecommendPostRequest request);

    // 前后端联调测试接口：获取随机post
    @POST(BaseConfig.AUTH_TOKEN_PREFIX + BackEndConstant.POST + "/rec/post/test")
    Observable<BaseResponse<RecommendPostResponse>> recommendTestGetRandomPost(@Body RecommendPostRequest request);

    /**
     * 获取单个帖子
     * @param request    帖子id + 页码
     * @return           帖子
     */
    @POST(BaseConfig.AUTH_TOKEN_PREFIX + BackEndConstant.POST + "/post/getPost")
    Observable<BaseResponse<SinglePostResponse>> getSinglePost(@Body GetSinglePostRequest request);

    /**
     * 发布帖子（首次【因为1.需要审核是否发过，以及内容是否合法2.oss上传速度较慢，可以后台上传】）
     * @param request   请求
     * @return  发布帖子
     */
    @POST(BaseConfig.AUTH_TOKEN_PREFIX + BackEndConstant.POST + "/post/postPublishFirst")
    Observable<BaseResponse<PostPublishResponse>> postPublishFirst(
            @Body PostPublishRequest request
    );

    /**
     * 发布帖子（首次结束拿到雪花id之后上传file到oss）
     * @param files         文件
     * @param postId        帖子id
     * @param userId        userId
     * @return              发布帖子
     */
    @Multipart
    @POST(BaseConfig.AUTH_TOKEN_PREFIX + BackEndConstant.POST + "/postFile/uploadPost/immediately")
    Observable<BaseResponse<PostVo>> uploadPostFile(
            @Part List<MultipartBody.Part> files,
            @Part("postId") Long postId,
            @Part("userId") Long userId
    );

    //-------------搜索相关--------------

    /**
     * 模糊搜索帖子
     * @param request   请求
     * @return          模糊搜索帖子结果
     */
    @POST(BaseConfig.AUTH_TOKEN_PREFIX + BackEndConstant.POST + "/search/fuzzy")
    Observable<BaseResponse<FuzzySearchResponse>> fuzzySearch(
            @Body FuzzySearchRequest request
    );

    //--------------Test--------------

    @Multipart
    @POST(BackEndConstant.OSS + "/oss/uploadTest")
    Observable<BaseResponse<String>> uploadImageTest(
            @Part MultipartBody.Part file
    );

    //-------------预约相关--------------
    ;
    /**
     * 获取预约列表
     * @param request   获取预约列表请求
     * @return          获取预约列表响应
     */
    @POST(BaseConfig.AUTH_TOKEN_PREFIX + BackEndConstant.MEDICINE + "/appointment/getList")
    Observable<BaseResponse<GetRegisterAppointmentListResponse>> getRegisterAppointmentList(
            @Body GetRegisterAppointmentListRequest request
    );

    /**
     * 获取所有预约时间
     * @param request   获取预约列表请求
     * @return          获取预约列表响应
     */
    @POST(BaseConfig.AUTH_TOKEN_PREFIX + BackEndConstant.MEDICINE + "/appointment/getAllDate")
    Observable<BaseResponse<GetAllRegisterAppointmentDateResponse>> getRegisterAppointmentAllDate(
            @Body GetRegisterAppointmentListRequest request
    );

    /**
     * 预约
     * @param request   预约请求
     * @return          预约响应
     */
    @POST(BaseConfig.AUTH_TOKEN_PREFIX + BackEndConstant.MEDICINE + "/appointment/apply")
    Observable<BaseResponse<AppointmentDoctorResponse>> appointmentDoctorMerchant(
            @Body AppointmentDoctorRequest request
    );
}
