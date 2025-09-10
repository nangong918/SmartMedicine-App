package com.czy.dao.networkRepository;


import com.czy.appcore.network.api.api.ApiRequest;
import com.czy.baseutil.network.BaseApiRequestImpl;
import com.czy.baseutil.network.BaseResponse;
import com.czy.baseutil.network.OnSuccessCallback;
import com.czy.baseutil.network.OnThrowableCallback;
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

import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class ApiRequestImpl extends BaseApiRequestImpl {

    protected ApiRequest mApi;

    public ApiRequestImpl(ApiRequest apiRequest){
        this.mApi = apiRequest;
    }

    //    @POST("/login/sendSms")
    //    Observable<BaseResponse<String>> sendSms(@Body BaseRequest baseRequest);
    public void sendSms(SendSmsRequest request,
                        OnSuccessCallback<BaseResponse<SendSmsResponse>> onSuccessCallback,
                        OnThrowableCallback onThrowableCallback){
        sendRequestCallback(
                mApi.sendSms(request),
                onSuccessCallback,
                onThrowableCallback
        );
    }


    //    @POST("/login/smsLogin")
    //    Observable<BaseResponse<LoginSignResponse>> smsLogin(@Body PhoneLoginRequest loginRequest);
    public void smsLogin(PhoneLoginInfoRequest request,
                             OnSuccessCallback<BaseResponse<LoginSignResponse>> onSuccessCallback,
                             OnThrowableCallback onThrowableCallback){
        sendRequestCallback(
                mApi.smsLogin(request),
                onSuccessCallback,
                onThrowableCallback
        );
    }

    //    @POST("/login/register")
    //    Observable<BaseResponse<UserRegisterResponse>> register(@Body RegisterUserRequest request);
    public void register(RegisterUserRequest request,
                         OnSuccessCallback<BaseResponse<UserRegisterResponse>> onSuccessCallback,
                         OnThrowableCallback onThrowableCallback){
        sendRequestCallback(
                mApi.register(request),
                onSuccessCallback,
                onThrowableCallback
        );
    }

    //    @Multipart
    //    @POST("/registerUser/uploadImg")
    //    Observable<BaseResponse<UserEntityVo>> registerUserUploadImg(
    //            @Part MultipartBody.Part img,
    //            @Part("phone") RequestBody phone,
    //            @Part("userId") RequestBody userId
    //    );
    public void registerUserUploadImg(
            MultipartBody.Part img,
            RequestBody phone,
            RequestBody userId,
            OnSuccessCallback<BaseResponse<UserEntityVo>> onSuccessCallback,
            OnThrowableCallback onErrorCallback
    ){
        sendRequestCallback(
                mApi.registerUserUploadImg(
                        img,
                        phone,
                        userId
                ),
                onSuccessCallback,
                onErrorCallback
        );
    }

    //    @POST("/login/isPhoneRegistered")
    //    Observable<BaseResponse<IsRegisterResponse>> isPhoneRegistered(@Body IsRegisterRequest request);
    public void isPhoneRegistered(
            IsRegisterRequest request,
            OnSuccessCallback<BaseResponse<IsRegisterResponse>> onSuccessCallback,
            OnThrowableCallback onThrowableCallback){
        sendRequestCallback(
                mApi.isPhoneRegistered(request),
                onSuccessCallback,
                onThrowableCallback
        );
    }

    //    @POST("/login/pwdLogin")
    //    Observable<BaseResponse<LoginSignResponse>> passwordLogin(@Body LoginUserRequest request);
    public void passwordLogin(
            LoginUserRequest request,
            OnSuccessCallback<BaseResponse<LoginSignResponse>> onSuccessCallback,
            OnThrowableCallback onThrowableCallback){
        sendRequestCallback(
                mApi.passwordLogin(request),
                onSuccessCallback,
                onThrowableCallback
        );
    }

    //    @POST("/user/searchUser")
    //    Observable<BaseResponse<SearchUserResponse>> searchUsers(@Body BaseNettyRequest request);
    public void searchUsers(SearchUserRequest request,
                            OnSuccessCallback<BaseResponse<SearchUserResponse>> onSuccessCallback,
                            OnThrowableCallback onThrowableCallback){
        sendRequestCallback(
                mApi.searchUsers(request),
                onSuccessCallback,
                onThrowableCallback
        );
    }

    //    @POST("/user/addUser")
    //    Observable<BaseResponse<Void>> addUserFriend(@Body AddUserRequest addUserRequest);
//    public void addUserFriend(AddUserRequest request,
//                              OnSuccessCallback<BaseResponse<Void>> onSuccessCallback,
//                              OnThrowableCallback onThrowableCallback){
//        sendRequestCallback(
//                mApi.addUserFriend(request),
//                onSuccessCallback,
//                onThrowableCallback
//        );
//    }

    //    @POST("/user/handleAddedUser")
    //    Observable<BaseResponse<Void>> handleAddedUser(@Body HandleAddedUserRequest request);
//    public void handleAddedUser(HandleAddedUserRequest request,
//                                OnSuccessCallback<BaseResponse<Void>> onSuccessCallback,
//                                OnThrowableCallback onThrowableCallback){
//        sendRequestCallback(
//                mApi.handleAddedUser(request),
//                onSuccessCallback,
//                onThrowableCallback
//        );
//    }

    //     @POST("/user/getAddMeRequestList")
    //    Observable<BaseResponse<GetAddMeRequestListResponse>> getAddMeRequestList(@Body BaseNettyRequest request);
    public void getAddMeRequestList(BaseHttpRequest request,
                                    OnSuccessCallback<BaseResponse<GetAddMeRequestListResponse>> onSuccessCallback,
                                    OnThrowableCallback onThrowableCallback){
        sendRequestCallback(
                mApi.getAddMeRequestList(request),
                onSuccessCallback,
                onThrowableCallback
        );
    }

    //     @POST("/user/getHandleMyAddUserResponseList")
    //    Observable<BaseResponse<GetHandleMyAddUserResponseListResponse>> getHandleMyAddUserResponseList(@Body BaseNettyRequest request);
    public void getHandleMyAddUserResponseList(BaseHttpRequest request,
                                               OnSuccessCallback<BaseResponse<GetHandleMyAddUserResponseListResponse>> onSuccessCallback,
                                               OnThrowableCallback onThrowableCallback){
        sendRequestCallback(
                mApi.getHandleMyAddUserResponseList(request),
                onSuccessCallback,
                onThrowableCallback
        );
    }

    //     @POST("/user/getMyFriendList")
    //    Observable<BaseResponse<GetMyFriendsResponse>> getMyFriendList(@Body GetMyFriendsRequest request);
    public void getMyFriendList(GetMyFriendsRequest request,
                                OnSuccessCallback<BaseResponse<GetMyFriendsResponse>> onSuccessCallback,
                                OnThrowableCallback onThrowableCallback){
        sendRequestCallback(
                mApi.getMyFriendList(request),
                onSuccessCallback,
                onThrowableCallback
        );
    }

    //    @POST("/chat/getUserNewMessage")
    //    Observable<BaseResponse<UserNewMessageResponse>> getUserNewMessage(@Body BaseNettyRequest request);
    public void getUserNewMessage(BaseHttpRequest request,
                                  OnSuccessCallback<BaseResponse<UserNewMessageResponse>> onSuccessCallback,
                                  OnThrowableCallback onThrowableCallback){
        sendRequestCallback(
                mApi.getUserNewMessage(request),
                onSuccessCallback,
                onThrowableCallback
        );
    }

    //    @POST("/chat/fetchUserMessage")
    //    Observable<BaseResponse<FetchUserMessageResponse>> fetchUserMessage(@Body FetchUserMessageRequest request);
    public void fetchUserMessage(FetchUserMessageRequest request,
                                 OnSuccessCallback<BaseResponse<FetchUserMessageResponse>> successCallback,
                                 OnThrowableCallback onThrowableCallback){
        sendRequestCallback(
                mApi.fetchUserMessage(request),
                successCallback,
                onThrowableCallback
        );
    }

    //    @Multipart
    //    @POST("/chatFile/uploadAndSend")
    //    Observable<BaseResponse<ChatUploadFileResponse>> uploadAndSend(
    //            @Part MultipartBody.Part file,
    //            @Part("fileId") RequestBody fileId,
    //            @Part("senderId") RequestBody senderId,
    //            @Part("receiverId") RequestBody receiverId
    //    );
    public void uploadAndSend(MultipartBody.Part file,
                              RequestBody fileId,
                              RequestBody senderId,
                              RequestBody receiverId,
                              OnSuccessCallback<BaseResponse<ChatUploadFileResponse>> onSuccessCallback,
                              OnThrowableCallback onThrowableCallback){
        sendRequestCallback(
                mApi.uploadAndSend(file, fileId, senderId, receiverId),
                onSuccessCallback,
                onThrowableCallback
        );
    }

    //    @POST("/user/getMyFriendApplyList")
    //    Observable<BaseResponse<Integer>> getMyFriendApplyList(@Body BaseNettyRequest request);
    public void getMyFriendApplyList(BaseHttpRequest request,
                                     OnSuccessCallback<BaseResponse<Integer>> onSuccessCallback,
                                     OnThrowableCallback onThrowableCallback){
        sendRequestCallback(
                mApi.getMyFriendApplyList(request),
                onSuccessCallback,
                onThrowableCallback
        );
    }

    //    @POST(BaseConfig.AUTH_TOKEN_PREFIX + BackEndConstant.POST + "/userBrief/get")
    //    Observable<BaseResponse<UserBriefResponse>> getUserBrief(@Body UserBriefRequest request);
    public void getUserBrief(UserBriefRequest request,
                             OnSuccessCallback<BaseResponse<UserBriefResponse>> onSuccessCallback,
                             OnThrowableCallback onThrowableCallback){
        sendRequestCallback(
                mApi.getUserBrief(request),
                onSuccessCallback,
                onThrowableCallback
        );
    }

    //    @Multipart
    //    @POST("file/uploadImage")
    //    Observable<BaseResponse<FileUploadResponse>> fileUpload(
    //            @Part MultipartBody.Part file,
    //            @Part("name") RequestBody name,
    //            @Part("timestamp") RequestBody timestamp
    //    );
    @Deprecated
    public void fileUpload(
            MultipartBody.Part file,
            RequestBody name,
            RequestBody timestamp,
            OnSuccessCallback<BaseResponse<FileUploadResponse>> onSuccessCallback,
            OnThrowableCallback onThrowableCallback
    ){
        sendRequestCallback(
                mApi.fileUpload(file, name, timestamp),
                onSuccessCallback,
                onThrowableCallback
        );
    }

    //    @GET("/file/downloadImage")
    //    Observable<BaseResponse<FileDownloadBytesResponse>> downloadImage(@Query("url") String url);
    @Deprecated
    public void downloadImage(String url,
                             OnSuccessCallback<BaseResponse<FileDownloadBytesResponse>> onSuccessCallback,
                             OnThrowableCallback onThrowableCallback){
        sendRequestCallback(
                mApi.downloadImage(url),
                onSuccessCallback,
                onThrowableCallback
        );
    }

    //    @POST("/recommend/getPost")
    //    Observable<BaseResponse<RecommendPostResponse>> getPost(@Body RecommendPostRequest request);
    public void getRecommendPosts(RecommendPostRequest request,
                        OnSuccessCallback<BaseResponse<RecommendPostResponse>> onSuccessCallback,
                        OnThrowableCallback onThrowableCallback){
        sendRequestCallback(
                mApi.getRecommendPosts(request),
                onSuccessCallback,
                onThrowableCallback
        );
    }

    //    @POST(BaseConfig.AUTH_TOKEN_PREFIX + BackEndConstant.RECOMMEND + "/rec/post/test")
    //    Observable<BaseResponse<RecommendPostResponse>> recommendTestGetRandomPost(@Body RecommendPostRequest request);
    public void recommendTestGetRandomPost(RecommendPostRequest request,
                                           OnSuccessCallback<BaseResponse<RecommendPostResponse>> onSuccessCallback,
                                           OnThrowableCallback onThrowableCallback){
        sendRequestCallback(
                mApi.recommendTestGetRandomPost(request),
                onSuccessCallback,
                onThrowableCallback
        );
    }

    //    @POST(BaseConfig.AUTH_TOKEN_PREFIX + BackEndConstant.POST + "/post/getPost")
    //    Observable<BaseResponse<SinglePostResponse>> getSinglePost(@Body GetSinglePostRequest request);
    public void getSinglePost(GetSinglePostRequest request,
                              OnSuccessCallback<BaseResponse<SinglePostResponse>> onSuccessCallback,
                              OnThrowableCallback onThrowableCallback){
        sendRequestCallback(
                mApi.getSinglePost(request),
                onSuccessCallback,
                onThrowableCallback
        );
    }

    //    @POST("/post/postPublishFirst")
    //    Observable<BaseResponse<PostPublishResponse>> postPublishFirst(
    //            @Body PostPublishRequest request
    //    );
    public void postPublishFirst(PostPublishRequest request,
                                 OnSuccessCallback<BaseResponse<PostPublishResponse>> onSuccessCallback,
                                 OnThrowableCallback onThrowableCallback){
        sendRequestCallback(
                mApi.postPublishFirst(request),
                onSuccessCallback,
                onThrowableCallback
        );
    }

    //    @POST("/postFile/uploadPost")
    //    Observable<BaseResponse<PostVo>> uploadPostFile(
    //            @Part("files") List<MultipartBody.Part> files,
    //            @Part("postId") Long postId,
    //            @Part("userId") Long userId
    //    );

    public void uploadPostFile(List<MultipartBody.Part> files,
                               Long postId,
                               Long userId,
                               OnSuccessCallback<BaseResponse<PostVo>> onSuccessCallback,
                               OnThrowableCallback onThrowableCallback){
        this.sendRequestCallback(
                mApi.uploadPostFile(files, postId, userId),
                onSuccessCallback,
                onThrowableCallback
        );
    }

    //    @POST(BaseConfig.AUTH_TOKEN_PREFIX + BackEndConstant.SEARCH + "/main/fuzzy")
    //    Observable<BaseResponse<FuzzySearchResponse>> fuzzySearch(
    //            @Body FuzzySearchRequest request
    //    );
    public void fuzzySearch(FuzzySearchRequest request,
                            OnSuccessCallback<BaseResponse<FuzzySearchResponse>> onSuccessCallback,
                            OnThrowableCallback onThrowableCallback){
        this.sendRequestCallback(
                mApi.fuzzySearch(request),
                onSuccessCallback,
                onThrowableCallback
        );
    }


    //--------------Test--------------

    //    @Multipart
    //    @POST(BackEndConstant.OSS + "/oss/uploadTest")
    //    Observable<BaseResponse<String>> uploadImageTest(
    //            @Part MultipartBody.Part file
    //    );
    public void uploadImageTest(MultipartBody.Part file,
                                OnSuccessCallback<BaseResponse<String>> onSuccessCallback,
                                OnThrowableCallback onThrowableCallback){
        this.sendRequestCallback(
                mApi.uploadImageTest(file),
                onSuccessCallback,
                onThrowableCallback
        );
    }

    //    @POST(BaseConfig.AUTH_TOKEN_PREFIX + BackEndConstant.MEDICINE + "/appointment/getList")
    //    Observable<BaseResponse<GetRegisterAppointmentListRequest>> getRegisterAppointmentList(
    //            @Body GetRegisterAppointmentListRequest request
    //    );
    public void getRegisterAppointmentList(GetRegisterAppointmentListRequest request,
                                           OnSuccessCallback<BaseResponse<GetRegisterAppointmentListResponse>> onSuccessCallback,
                                           OnThrowableCallback onThrowableCallback){
        this.sendRequestCallback(
                mApi.getRegisterAppointmentList(request),
                onSuccessCallback,
                onThrowableCallback
        );
    }

    //    @POST(BaseConfig.AUTH_TOKEN_PREFIX + BackEndConstant.MEDICINE + "/appointment/getAllDate")
    //    Observable<BaseResponse<GetAllRegisterAppointmentDateResponse>> getRegisterAppointmentAllDate(
    //            @Body GetRegisterAppointmentListRequest request
    //    );
    public void getRegisterAppointmentAllDate(GetRegisterAppointmentListRequest request,
                                              OnSuccessCallback<BaseResponse<GetAllRegisterAppointmentDateResponse>> onSuccessCallback,
                                              OnThrowableCallback onThrowableCallback){
        this.sendRequestCallback(
                mApi.getRegisterAppointmentAllDate(request),
                onSuccessCallback,
                onThrowableCallback
        );
    }

    //    @POST(BaseConfig.AUTH_TOKEN_PREFIX + BackEndConstant.MEDICINE + "/appointment/apply")
    //    Observable<BaseResponse<AppointmentDoctorResponse>> appointmentDoctorMerchant(
    //            @Body AppointmentDoctorRequest request
    //    );
    public void appointmentDoctorMerchant(AppointmentDoctorRequest request,
                                          OnSuccessCallback<BaseResponse<AppointmentDoctorResponse>> onSuccessCallback,
                                          OnThrowableCallback onThrowableCallback){
        this.sendRequestCallback(
                mApi.appointmentDoctorMerchant(request),
                onSuccessCallback,
                onThrowableCallback
        );
    }

}
