package com.czy.datalib.networkRepository;


import com.czy.appcore.network.api.ApiRequest;
import com.czy.baseUtilsLib.network.BaseApiRequestImpl;
import com.czy.baseUtilsLib.network.BaseResponse;
import com.czy.baseUtilsLib.network.OnSuccessCallback;
import com.czy.baseUtilsLib.network.OnThrowableCallback;
import com.czy.dal.dto.http.request.RecommendPostRequest;
import com.czy.dal.dto.http.response.RecommendPostResponse;
import com.czy.dal.dto.netty.request.AddUserRequest;
import com.czy.dal.dto.http.request.BaseNettyRequest;
import com.czy.dal.dto.http.request.GetMyFriendsRequest;
import com.czy.dal.dto.netty.request.HandleAddedUserRequest;
import com.czy.dal.dto.http.request.PhoneLoginRequest;
import com.czy.dal.dto.http.request.SendSmsRequest;
import com.czy.dal.dto.http.response.GetAddMeRequestListResponse;
import com.czy.dal.dto.http.response.GetHandleMyAddUserResponseListResponse;
import com.czy.dal.dto.http.response.GetMyFriendsResponse;
import com.czy.dal.dto.http.response.LoginSignResponse;
import com.czy.dal.dto.http.response.SearchUserResponse;
import com.czy.dal.dto.netty.request.FetchUserMessageRequest;
import com.czy.dal.dto.netty.response.FetchUserMessageResponse;
import com.czy.dal.dto.netty.response.FileDownloadBytesResponse;
import com.czy.dal.dto.netty.response.FileUploadResponse;
import com.czy.dal.dto.netty.response.UserNewMessageResponse;

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
                        OnSuccessCallback<BaseResponse<String>> onSuccessCallback,
                        OnThrowableCallback onThrowableCallback){
        sendRequestCallback(
                mApi.sendSms(request),
                onSuccessCallback,
                onThrowableCallback
        );
    }


    //    @POST("/login/smsLoginUser")
    //    Observable<BaseResponse<LoginSignResponse>> smsLoginUser(@Body PhoneLoginRequest loginRequest);
    public void smsLoginUser(PhoneLoginRequest request,
                            OnSuccessCallback<BaseResponse<LoginSignResponse>> onSuccessCallback,
                            OnThrowableCallback onThrowableCallback){
        sendRequestCallback(
                mApi.smsLoginUser(request),
                onSuccessCallback,
                onThrowableCallback
        );
    }

    //    @POST("/user/searchUser")
    //    Observable<BaseResponse<SearchUserResponse>> searchUsers(@Body BaseNettyRequest request);
    public void searchUsers(BaseNettyRequest request,
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
    public void getAddMeRequestList(BaseNettyRequest request,
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
    public void getHandleMyAddUserResponseList(BaseNettyRequest request,
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
    public void getUserNewMessage(BaseNettyRequest request,
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

    //    @POST("/user/getMyFriendApplyList")
    //    Observable<BaseResponse<Integer>> getMyFriendApplyList(@Body BaseNettyRequest request);
    public void getMyFriendApplyList(BaseNettyRequest request,
                                    OnSuccessCallback<BaseResponse<Integer>> onSuccessCallback,
                                    OnThrowableCallback onThrowableCallback){
        sendRequestCallback(
                mApi.getMyFriendApplyList(request),
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
    public void getPost(RecommendPostRequest request,
                        OnSuccessCallback<BaseResponse<RecommendPostResponse>> onSuccessCallback,
                        OnThrowableCallback onThrowableCallback){
        sendRequestCallback(
                mApi.getPost(request),
                onSuccessCallback,
                onThrowableCallback
        );
    }

}
