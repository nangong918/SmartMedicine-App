package com.czy.smartmedicine.viewModel;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.czy.appcore.BaseConfig;
import com.czy.appcore.network.netty.api.receive.ReceiveMessageApi;
import com.czy.appcore.network.netty.api.send.SocketMessageSender;
import com.czy.appcore.service.chat.ChatListManager;
import com.czy.appcore.service.chat.MessageItem;
import com.czy.baseUtilsLib.image.ImageManager;
import com.czy.baseUtilsLib.network.BaseResponse;
import com.czy.dal.ao.chat.ChatActivityStartAo;
import com.czy.dal.bo.UserChatMessageBo;
import com.czy.dal.constant.MessageTypeEnum;
import com.czy.dal.dto.netty.forwardMessage.GroupTextDataResponse;
import com.czy.dal.dto.netty.forwardMessage.SendImageRequest;
import com.czy.dal.dto.netty.forwardMessage.SendTextDataRequest;
import com.czy.dal.dto.netty.forwardMessage.UserImageResponse;
import com.czy.dal.dto.netty.forwardMessage.UserTextDataResponse;
import com.czy.dal.dto.netty.request.FetchUserMessageRequest;
import com.czy.dal.dto.netty.response.FetchUserMessageResponse;
import com.czy.dal.dto.netty.response.FileDownloadBytesResponse;
import com.czy.dal.dto.netty.response.FileUploadResponse;
import com.czy.dal.dto.netty.response.HaveReadMessageResponse;
import com.czy.dal.vo.entity.message.ChatMessageItemVo;
import com.czy.dal.vo.viewModelVo.chat.ChatVo;
import com.czy.datalib.networkRepository.ApiRequestImpl;
import com.czy.smartmedicine.MainApplication;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class ChatViewModel extends ViewModel {

    private static final String TAG = ChatViewModel.class.getName();

    private final ApiRequestImpl apiRequestImpl;
    private final SocketMessageSender socketMessageSender;

    public ChatViewModel (ApiRequestImpl apiRequestImpl, SocketMessageSender socketMessageSender) {
        this.apiRequestImpl = apiRequestImpl;
        this.socketMessageSender = socketMessageSender;
    }

    //---------------------------Vo Ld---------------------------
    Handler messageHandler;
    public ChatVo chatVo = new ChatVo();
    public void init(ChatVo chatVo) {
        messageHandler = new Handler(Looper.getMainLooper());
        initVo(chatVo);
        initMessage();
        initEventBus();
        initChatListManager();
    }
    private void initVo(ChatVo chatVo){
        this.chatVo = chatVo;
    }

    public TextWatcher getTextWatcher(){
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0){
                    Optional.ofNullable(chatVo)
                            .map(chatVo -> chatVo.inputText)
                            .ifPresent(inputText -> inputText.setValue(s.toString()));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
    }

    //-----------------------Start-----------------------

    public void setStartAo(ChatActivityStartAo ao){
        chatVo.name.setValue(ao.contactName);
        chatVo.inputText.setValue(ao.inputText);
        chatVo.contactAccount = ao.contactAccount;
        chatVo.avatarUrlOrUri.setValue(ao.avatarUrl);
        List<ChatMessageItemVo> chatMessageList = ao.chatMessageListItemVo;
        if (chatMessageList != null && !chatMessageList.isEmpty()){
            Optional.ofNullable(chatVo.chatListVo)
                    .map(chatListVo -> chatListVo.chatMessageList)
                    .ifPresent(ls -> {
                        messageHandler.post(() -> {
                            ls.postValue(chatMessageList);
                        });
                    });
        }
    }
    // TODO 改为下拉刷新view
    //---------------------------NetWork---------------------------
    public void initialNetworkRequest(String contactAccount){
        // TODO 重构的时候todo
//        String key = ChatActivity.class.getName() + ":" + contactAccount;
//        if (HttpRequestManager.getIsFirstOpen(key)){
//            fetchUserMessage(System.currentTimeMillis(), 20);
//        }
//        else {
//            // TODO
//        }
        fetchUserMessage(System.currentTimeMillis(), 20);
    }
    //==========主动与此好友的消息

    public void fetchUserMessage(Long timestampIndex, Integer messageCount){
        FetchUserMessageRequest request = new FetchUserMessageRequest();
        request.receiverAccount = chatVo.contactAccount;
        request.timestampIndex = timestampIndex;
        request.messageCount = messageCount;
        request.senderAccount = MainApplication.getInstance().getUserLoginInfoAo().account;
        request.senderId = MainApplication.getInstance().getUserLoginInfoAo().account;

        apiRequestImpl.fetchUserMessage(request
                , this::handleFetchUserMessage
                , ViewModelUtil::globalThrowableToast
        );
    }

    private void handleFetchUserMessage(BaseResponse<FetchUserMessageResponse> response){
        if(ViewModelUtil.handleResponse(response)){
            List<MessageItem> messageList = new ArrayList<>();
            for (UserChatMessageBo messageBo : response.getData().messageList) {
                MessageItem messageItem = MessageItem.getByChatMessageItemVo(messageBo);
                messageList.add(messageItem);
            }
            chatListManager.cacheAddMessage(messageList);
        }
    }

    //---------------------------logic---------------------------

    public void sendMessage(){
        String message = chatVo.inputText.getValue();
        String receiverAccount = chatVo.contactAccount;
        // 用Netty长连接发送消息
        SendTextDataRequest request = new SendTextDataRequest();
        request.setContent(message);
        request.setSenderId(MainApplication.getInstance().getUserLoginInfoAo().account);
        request.setReceiverId(receiverAccount);
        request.setTimestamp(String.valueOf(System.currentTimeMillis()));

        // 发送消息
        socketMessageSender.sendTextToUser(request);

        // 本地展示
        MessageItem messageItem = MessageItem.getBySendTextDataRequest(request);
        chatListManager.immediatelyAddMessage(messageItem);
    }

    //===========ChatListManager

    public ChatListManager chatListManager;

    private void initChatListManager(){
        // ChatList：2.内存数据源
        chatListManager = new ChatListManager(list -> {
            chatVo.isLoading.setValue(false);
            // 数据类型转换
            List<ChatMessageItemVo> chatMessageItemVos = new ArrayList<>();
            for (MessageItem item : list){
                ChatMessageItemVo chatMessageItemVo = item.toChatMessageItemVo(
                        MainApplication.getInstance().getUserLoginInfoAo().account
                );

                // 图片消息
                if (MessageTypeEnum.image.code == item.messageType){
                    Log.e("Intercep", "receive chatMessageItemVo timestamp: " + chatMessageItemVo.timestamp);
                    Log.e("Intercep", "receive item timestamp: " + item.timestamp);
                    downloadMessageImage(
                            chatMessageItemVo.content,
                            chatMessageItemVo.timestamp
                    );
                }

                chatMessageItemVos.add(chatMessageItemVo);
            }
            // 设置LiveData
            Optional.ofNullable(chatVo)
                    .map(chatVo -> chatVo.chatListVo)
                    .map(chatListVo -> chatListVo.chatMessageList)
                    .ifPresent(ls -> {
                        messageHandler.post(() -> {
                            ls.postValue(chatMessageItemVos);
                        });
                    });
        });
    }

    //===========Message

    private void initMessage(){
        initReceiveMessageApi();
    }

    // receive
    private ReceiveMessageApi receiveMessageApi;

    private void initReceiveMessageApi(){
        receiveMessageApi = new ReceiveMessageApi() {
            @Override
            public void receiveUserText(@NonNull UserTextDataResponse response) {
                MessageItem item = MessageItem.getByUserTextDataResponse(response);

                // 立刻将消息添加 (单条)
                // ChatList：5.本地发送数据源
                chatListManager.immediatelyAddMessage(item);
            }

            @Override
            public void receiveGroupText(@NonNull GroupTextDataResponse response) {
                // else
            }

            @Override
            public void haveReadMessage(@NonNull HaveReadMessageResponse response) {
                Log.d(TAG, "消息已读：senderAccount:" + response.senderAccount);
            }

            @Override
            public void receiveUserImage(@NonNull UserImageResponse response) {
                MessageItem item = MessageItem.getByUserImageResponse(response);

                chatListManager.immediatelyAddMessage(item);
            }
        };
    }

    // TODO 消息获取：1.外存获取，2.内存获取，3.网络获取，4.Socket获取 5.本地发送数据

    //===========Picture

    public AtomicReference<Uri> uriAtomicReference = new AtomicReference<>();

    public void sendPictureMessage(Context context,
                                   Bitmap bitmap, Long listTime) {
        // Http Send
        File imageFile = null;
        // 确保您在这里传入正确的 Uri
//        Uri uri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), imageName));
        imageFile = MainApplication.getInstance().getImageManager().bitmapToFile(bitmap, uriAtomicReference.get(), context);

        if (imageFile == null || !imageFile.exists()) {
            // 处理文件未创建或路径不正确的情况
            Log.e(TAG, "Image file creation failed");
            return;
        }

        // 获取文件名
        String originalFilename = imageFile.getName(); // 使用 getName() 获取文件名

        // 获取文件扩展名
        String fileExtension = originalFilename.contains(".") ?
                originalFilename.substring(originalFilename.lastIndexOf(".")) : ""; // 获取扩展名

        MultipartBody.Part filePart = com.czy.baseUtilsLib.file.FileUtil.createMultipartBodyPart(imageFile);

        // 文件名称，方便后端保存
        String fileName = MainApplication.getInstance().getUserLoginInfoAo().account + "_" + chatVo.contactAccount;

        apiRequestImpl.fileUpload(
                filePart,
                RequestBody.create(MediaType.parse("text/plain"), fileName),
                RequestBody.create(MediaType.parse("text/plain"), String.valueOf(listTime))
                , this::handleFileUpload
                , ViewModelUtil::globalThrowableToast
        );

        Log.e("Intercep", "send Image Time: " + listTime);

        // Socket Send
        SendImageRequest request = new SendImageRequest();
        request.senderId = MainApplication.getInstance().getUserLoginInfoAo().account;
        request.receiverId = chatVo.contactAccount;
        request.timestamp = String.valueOf(listTime);
        request.fileName = fileName + "_" + request.timestamp + fileExtension;
        socketMessageSender.sendImageToUser(request);
    }

    private void handleFileUpload(BaseResponse<FileUploadResponse> response) {
        if (ViewModelUtil.handleResponse(response)) {
            String uploadState = Optional.ofNullable(response.getData()).map(FileUploadResponse::getUploadState).orElse("");
//            ViewModelUtil.globalToast(uploadState);
            Log.d(TAG, "上传状态：" + uploadState);
        }
    }

    private void downloadMessageImage(String url, long listItemCreatedTime){
        apiRequestImpl.downloadImage(url,
                response -> {
                    handleDownloadImage(response, listItemCreatedTime);
                },
                ViewModelUtil::globalThrowableToast
        );
    }

    private void handleDownloadImage(BaseResponse<FileDownloadBytesResponse> response, long listItemCreatedTime) {
        if (ViewModelUtil.handleResponse(response)) {
            ImageManager imageManager = new ImageManager();
            Bitmap bitmap = imageManager.bytesToBitmap(response.getData().getFileBytes());
            bitmap = imageManager.processImage(bitmap, BaseConfig.BITMAP_MAX_SIZE);

            // bitmap需要设置到正确的id上面去
            List<ChatMessageItemVo> list = Optional.ofNullable(chatVo)
                    .map(chatVo -> chatVo.chatListVo)
                    .map(chatListVo -> chatListVo.chatMessageList)
                    .map(LiveData::getValue)
                    .orElse(null);
            if (list != null){
                Log.e("Intercep", "handleDownloadImage::listItemCreatedTime: " + listItemCreatedTime);
                for (int i = 0; i < list.size(); i++){
                    if (list.get(i).timestamp == listItemCreatedTime){
                        list.get(i).bitmap = bitmap;
                        Log.e("Intercep", "handleDownloadImage111: " + i);
                        Log.i("Intercep", "list.get(i).content: " + list.get(i).content);
                        Log.i("Intercep", "list.get(i).bitmap: " + list.get(i).bitmap);
                        break;
                    }
                }
            }

            // bitmap -> ChatMessageItemVo设置值 -> viewModel.chatVo.chatListVo.chatMessageList.postValue(currentList);
            Optional.ofNullable(chatVo)
                    .map(chatVo -> chatVo.chatListVo)
                    .map(chatListVo -> chatListVo.chatMessageList)
                    .ifPresent(liveData -> {
                        messageHandler.post(() -> {
                            liveData.postValue(list);
                        });
                    });
        }
    }

    //-----------------------EventBus-----------------------

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onMessageReceived(UserTextDataResponse response) {
        // 检查匹配是否是该用户的信息
        String receiverAccount = Optional.ofNullable(chatVo)
                .map(vo -> vo.contactAccount)
                .orElse("");
        if (receiverAccount.equals(response.getSenderId())){
            // 根据 message 的 type 执行对应的方法 TODO 梳理逻辑，这里有问题：chatListManager 和 消息队列分离了；chatListManager在Activity重新启动会出现数据丢失
            receiveMessageApi.receiveUserText(response);
        }
    }

    // 图片消息
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onMessageReceived(UserImageResponse response) {
        // 检查匹配是否是该用户的信息
        String receiverAccount = Optional.ofNullable(chatVo)
                .map(vo -> vo.contactAccount)
                .orElse("");
        if (receiverAccount.equals(response.getSenderId())){
            // 根据 message 的 type 执行对应的方法
            receiveMessageApi.receiveUserImage(response);
        }
    }

    private void initEventBus() {
        EventBus.getDefault().register(this);
    }

    private void unInitEventBus() {
        EventBus.getDefault().unregister(this);
    }

    public void storage(){
        // 消息存储指针指向此处
    }

    public void onPause() {
        storage();
    }

    public void onDestroy() {
        if (chatListManager != null){
            chatListManager.stop();
        }
        unInitEventBus();
    }
}
