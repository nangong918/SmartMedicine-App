package com.czy.appcore.network.api;

import com.czy.baseUtilsLib.network.BaseResponse;
import com.czy.dal.dto.http.request.BaseHttpRequest;
import com.czy.dal.dto.http.request.GetMyFriendsRequest;
import com.czy.dal.dto.http.request.PhoneLoginRequest;
import com.czy.dal.dto.http.request.PostPublishRequest;
import com.czy.dal.dto.http.request.RecommendPostRequest;
import com.czy.dal.dto.http.request.SendSmsRequest;
import com.czy.dal.dto.http.response.GetAddMeRequestListResponse;
import com.czy.dal.dto.http.response.GetHandleMyAddUserResponseListResponse;
import com.czy.dal.dto.http.response.GetMyFriendsResponse;
import com.czy.dal.dto.http.response.LoginSignResponse;
import com.czy.dal.dto.http.response.PostPublishResponse;
import com.czy.dal.dto.http.response.RecommendPostResponse;
import com.czy.dal.dto.http.response.SearchUserResponse;
import com.czy.dal.dto.http.response.SinglePostResponse;
import com.czy.dal.dto.netty.request.FetchUserMessageRequest;
import com.czy.dal.dto.netty.response.FetchUserMessageResponse;
import com.czy.dal.dto.netty.response.FileDownloadBytesResponse;
import com.czy.dal.dto.netty.response.FileUploadResponse;
import com.czy.dal.dto.netty.response.UserNewMessageResponse;

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

    /**
     * 请求发送短信验证码
     * @param request 主要是手机号
     * @return
     */
    @POST("/login/sendSms")
    Observable<BaseResponse<String>> sendSms(@Body SendSmsRequest request);

    /**
     * 短信验证码登录
     * @param request
     * @return
     */
    @POST("/login/smsLoginUser")
    Observable<BaseResponse<LoginSignResponse>> smsLoginUser(@Body PhoneLoginRequest request);

    /**
     * 搜索用户
     * @param request   账号
     * @return          用户列表
     */
    @POST("/relation/searchUser")
    Observable<BaseResponse<SearchUserResponse>> searchUsers(@Body BaseHttpRequest request);

//    /**
//     * 添加用户
//     * @param request    添加的用户信息
//     * @return                  添加的用户信息
//     */
//    @POST("/user/addUser")
//    Observable<BaseResponse<Void>> addUserFriend(@Body AddUserRequest request);
//
//    /**
//     * 处理添加用户
//     * @param request   处理添加用户的请求
//     * @return          处理添加用户的响应
//     */
//    @POST("/user/handleAddedUser")
//    Observable<BaseResponse<Void>> handleAddedUser(@Body HandleAddedUserRequest request);

    /**
     * 获取添加我的请求
     * @param request   用户基本信息
     * @return          添加我的请求
     */
    @POST("/relation/getAddMeRequestList")
    Observable<BaseResponse<GetAddMeRequestListResponse>> getAddMeRequestList(@Body BaseHttpRequest request);

    /**
     * 获取处理我的添加用户请求
     * @param request   用户基本信息
     * @return          处理我的添加用户请求
     */
    @POST("/relation/getHandleMyAddUserResponseList")
    Observable<BaseResponse<GetHandleMyAddUserResponseListResponse>> getHandleMyAddUserResponseList(@Body BaseHttpRequest request);

    /**
     * 获取我的全部好友
     * @param request   用户基本信息
     * @return          好友列表
     */
    @POST("/relation/getMyFriendList")
    Observable<BaseResponse<GetMyFriendsResponse>> getMyFriendList(@Body GetMyFriendsRequest request);

    /**
     * Message Fragment
     * 拉取用户的全部聊天消息(限制200条，超过就流式传输)：某个用户跟所有用户的1条最新消息List
     * @param request
     * @return  List<用户消息最新一条消息, 未读消息数量>
     */
    @POST("/chat/getUserNewMessage")
    Observable<BaseResponse<UserNewMessageResponse>> getUserNewMessage(@Body BaseHttpRequest request);

    /**
     * Chat Activity
     * 拉取用户和某个用户全部聊天消息(分页：一次拉取50条最新聊天消息)
     * @param request 请求获取消息：timestampIndex 消息起始索引；消息条数messageCount（max 200）
     * @return
     */
    @POST("/chat/fetchUserMessage")
    Observable<BaseResponse<FetchUserMessageResponse>> fetchUserMessage(@Body FetchUserMessageRequest request);

    /**
     * 获取与我相关的好友申请数量 [添加我的 + 我添加的]
     * @param request   用户基本信息
     * @return          好友申请数量
     */
    @POST("/relation/getMyFriendApplyList")
    Observable<BaseResponse<Integer>> getMyFriendApplyList(@Body BaseHttpRequest request);

    /**
     * 上传文件
     * @param file      file
     * @param name      name
     * @param timestamp 时间戳
     * @return          文件上传响应
     */
    //@Part 注解用于标识 Multipart 请求体的一部分,这里的 file 就是文件部分
    @Multipart
    @POST("/file/uploadImage")
    Observable<BaseResponse<FileUploadResponse>> fileUpload(
            @Part MultipartBody.Part file,
            @Part("name") RequestBody name,
            @Part("timestamp") RequestBody timestamp
    );

    /**
     * 下载图片
     * @param url   url
     * @return      文件下载响应
     */
    @GET("/file/downloadImage")
    Observable<BaseResponse<FileDownloadBytesResponse>> downloadImage(@Query("url") String url);

    /**
     * 获取推荐帖子列表
     * @param request   请求
     * @return          推荐帖子
     */
    @POST("/recommend/getPost")
    Observable<BaseResponse<RecommendPostResponse>> getRecommendPosts(@Body RecommendPostRequest request);

    /**
     * 获取单个帖子
     * @param postId    帖子id
     * @param pageNum   页码
     * @return          帖子
     */
    @GET("/post/getPost")
    Observable<BaseResponse<SinglePostResponse>> getSinglePost(
            @Query("postId") Long postId,
            @Query("pageNum") Long pageNum
    );

    /**
     * 发布帖子（首次【因为1.需要审核是否发过，以及内容是否合法2.oss上传速度较慢，可以后台上传】）
     * @param request   请求
     * @return  发布帖子
     */
    @POST("/post/postPublishFirst")
    Observable<BaseResponse<PostPublishResponse>> postPublishFirst(
            @Body PostPublishRequest request
    );

    /**
     * 发布帖子（首次结束拿到雪花id之后上传file到oss）
     * @param files         文件
     * @param postId        帖子id
     * @param userAccount   用户账号
     * @return              发布帖子
     */
    @POST("/postFile/uploadPost")
    Observable<BaseResponse<String>> uploadPostFile(
            @Part List<MultipartBody.Part> files,
            @Part("postId") Long postId,
            @Part("userAccount") String userAccount
    );
}
// 1.重构响应 -> 在Friend和Message中显示 -> ChatList发送消息
// 消息前后端持久化策略
// 图片，文件，语音，视频传输
// 2.WebRTC 语音视频通话；通话请求Http请求
// 推文，动态发布
// 推荐算法

/**
 * 何时开始重构？
 * 1.添加好友，发送消息，语音视频通话；
 * 2.图片文件，语音，视频传输； -> 存储策略；大数据传输策略
 * findJob：看大公司，思考必要
 * 3.推文，动态发布；
 * 4.推荐算法；
 *
 * 接口重构从前端开始重构；因为前端需要什么 就是后端的需求，后端拿到需求再开始设计
 *
 * 需求查资料，给出解决方案
 */