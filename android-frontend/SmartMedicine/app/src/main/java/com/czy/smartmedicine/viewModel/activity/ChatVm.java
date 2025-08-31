package com.czy.smartmedicine.viewModel.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.RecyclerView;

import com.czy.appcore.BaseConfig;
import com.czy.appcore.network.netty.api.receive.ChatApiHandler;
import com.czy.appcore.network.netty.api.send.SocketMessageSender;
import com.czy.appcore.service.chat.ChatMessageManager;
import com.czy.appcore.service.chat.CurrentChatMessageContext;
import com.czy.appcore.service.chat.MessageItem;
import com.czy.appcore.service.chat.OnChatMessageChange;
import com.czy.baseutil.image.ImageManager;
import com.czy.baseutil.network.BaseResponse;
import com.czy.baseutil.permission.GainPermissionCallback;
import com.czy.baseutil.permission.PermissionUtil;
import com.czy.baseutil.ui.ToastUtils;
import com.czy.appview.view.chatMessage.ChatMessageAdapter;
import com.czy.domain.ao.chat.ChatActivityStartAo;
import com.czy.domain.bo.UserChatMessageBo;
import com.czy.domain.constant.MessageTypeEnum;
import com.czy.domain.constant.NettyConstants;
import com.czy.domain.dto.netty.forwardMessage.GroupTextDataResponse;
import com.czy.domain.dto.netty.forwardMessage.SendImageRequest;
import com.czy.domain.dto.netty.forwardMessage.SendTextDataRequest;
import com.czy.domain.dto.netty.forwardMessage.UserImageResponse;
import com.czy.domain.dto.netty.forwardMessage.UserTextDataResponse;
import com.czy.domain.dto.netty.request.FetchUserMessageRequest;
import com.czy.domain.dto.netty.response.FetchUserMessageResponse;
import com.czy.domain.dto.netty.response.HaveReadMessageResponse;
import com.czy.domain.dto.netty.response.UploadFileResponse;
import com.czy.domain.vo.entity.message.ChatMessageItemVo;
import com.czy.domain.fragmentActivityAo.chat.ChatVo;
import com.czy.dao.networkRepository.ApiRequestImpl;
import com.czy.smartmedicine.MainApplication;
import com.czy.smartmedicine.activity.ChatActivity;
import com.czy.smartmedicine.manager.HttpRequestManager;
import com.czy.smartmedicine.utils.ViewModelUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class ChatVm extends ViewModel {

    private static final String TAG = ChatVm.class.getName();

    private final ApiRequestImpl apiRequestImpl;
    private final SocketMessageSender socketMessageSender;

    public ChatVm(ApiRequestImpl apiRequestImpl, SocketMessageSender socketMessageSender) {
        this.apiRequestImpl = apiRequestImpl;
        this.socketMessageSender = socketMessageSender;
    }

    //---------------------------Vo Ld---------------------------

    private Handler messageHandler;
    public ChatMessageAdapter chatMessageAdapter;

    public ChatVo chatVo = new ChatVo();
    public void init(ChatVo chatVo) {
        this.messageHandler = new Handler(Looper.getMainLooper());
        setVo(chatVo);
        initSocketReceiver();
        initEventBus();
        setChatMessageManager();
    }

    private void setVo(ChatVo chatVo){
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

    public void notifyMessageListChange(){
        List<ChatMessageItemVo> currentList = Optional.ofNullable(chatVo)
                .map(cvo -> cvo.chatListVo)
                .map(cvo -> cvo.chatMessageList)
                .orElse(new ArrayList<>());
        messageHandler.post(() -> chatMessageAdapter.setCurrentList(currentList));
    }

    public void notifyMessageListChange(List<ChatMessageItemVo> list){
        List<ChatMessageItemVo> currentList = Optional.ofNullable(chatVo)
                .map(cvo -> cvo.chatListVo)
                .map(cvo -> cvo.chatMessageList)
                .map(l -> {
                    l.clear();
                    l.addAll(list);
                    return l;
                })
                .orElse(list);
        messageHandler.post(() -> chatMessageAdapter.setCurrentList(currentList));
    }

    @SuppressLint("NotifyDataSetChanged")
    public void initRecyclerView(@NonNull RecyclerView recyclerView){
        chatMessageAdapter = new ChatMessageAdapter();
        chatMessageAdapter.setOnSetMessageCallback(
                () -> {
                    // recyclerView滚动到最下面
                    recyclerView.scrollToPosition(
                            this.chatMessageAdapter.getItemCount() - 1
                    );
                }
        );
        recyclerView.setAdapter(
                this.chatMessageAdapter
        );

//        // 初始化view
//        notifyMessageListChange();
    }

    //-----------------------Start-----------------------

    public void setStartAo(ChatActivityStartAo ao){
        chatVo.name.setValue(ao.contactName);
        chatVo.inputText.setValue(ao.inputText);
        chatVo.contactAccount = ao.contactAccount;
        chatVo.contactId = ao.contactId;
        chatVo.avatarUrlOrUri.setValue(ao.avatarUrl);
    }
    // TODO 改为下拉刷新view （全部做完再完善）
    //---------------------------NetWork---------------------------
    public void initialNetworkRequest(Long contactId){
        if (contactId == null || contactId.equals(NettyConstants.ERROR_ID)){
            Log.w(TAG, "contactId is null");
            return;
        }
        // HttpRequestManager会在断开连接的时候调用refreshAllValue清除所有的缓存，会从新请求最新的聊天数据
        String key = ChatActivity.class.getName() + ":" + contactId;
        if (HttpRequestManager.getIsFirstOpen(key)){
            fetchUserMessage(System.currentTimeMillis(), BaseConfig.DEFAULT_MESSAGE_FETCH_COUNT);
        }
        else {
            // 从缓存获取数据
            List<MessageItem> messageItems = Optional.ofNullable(MainApplication.getInstance().getChatMessageManager())
                    .map(manager -> manager.getChatMessages(contactId))
                    .orElse(new ArrayList<>());

            Long myId = Optional.ofNullable(MainApplication.getInstance().getUserLoginInfoAo())
                    .map(ao -> ao.userId)
                    .orElse(NettyConstants.ERROR_ID);

            if (NettyConstants.ERROR_ID.equals(myId)){
                Log.w(TAG, "myId is null");
                return;
            }

            // 将值设置给chatListVo
            this.chatVo.chatListVo.chatMessageList = messageItems.stream()
                    .map(item -> item.toChatMessageItemVo(myId))
                    .collect(Collectors.toList());

            // 通知adapter更新view
            // 创建 Handler
            Handler handler = new Handler(Looper.getMainLooper());

            // 定义 Runnable
            Runnable checkAdapterRunnable = new Runnable() {
                @Override
                public void run() {
                    if (chatMessageAdapter != null) {
                        // 更新 UI
                        notifyMessageListChange();
                    }
                    else {
                        // 如果 chatMessageAdapter 仍然为 null，300 毫秒后继续检查
                        Log.i(TAG, "chatMessageAdapter is null 继续等待300");
                        handler.postDelayed(this, 300);
                    }
                }
            };

            // 开始检查
            handler.post(checkAdapterRunnable);
        }
    }
    //==========主动与此好友的消息

    public void fetchUserMessage(Long timestampIndex, Integer messageCount){
        FetchUserMessageRequest request = new FetchUserMessageRequest();
        request.timestampIndex = timestampIndex;
        request.messageCount = messageCount;
        request.senderId = MainApplication.getInstance().getUserLoginInfoAo().userId;
        request.receiverId = chatVo.contactId;

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
            MainApplication.getInstance().getChatMessageManager().cacheMessage(
                    messageList,
                    chatVo.contactId
            );
        }
    }

    //---------------------------logic---------------------------

    public void sendMessage(){
        String message = chatVo.inputText.getValue();
//        String receiverAccount = chatVo.contactAccount;
        Long receiverId = chatVo.contactId;
        // 用Netty长连接发送消息
        SendTextDataRequest request = new SendTextDataRequest();
        request.setContent(message);
        request.setSenderId(MainApplication.getInstance().getUserLoginInfoAo().userId);
        request.setReceiverId(receiverId);
        request.setTimestamp(String.valueOf(System.currentTimeMillis()));

        // 发送消息
        socketMessageSender.sendTextToUser(request);

        // 本地展示
        MessageItem messageItem = MessageItem.getBySendTextDataRequest(request);
        // 添加到缓存 + 更新UI
        MainApplication.getInstance().getChatMessageManager().immediatelyAddChatMessage(
                messageItem,
                receiverId,
                getOnChatMessageChange()
        );
    }

    //===========ChatListManager

    // 设置聊天列表管理器的参数
    private void setChatMessageManager(){
        Long contactId = Optional.ofNullable(chatVo)
                .map(vo -> vo.contactId)
                .orElse(null);

        if (contactId == null){
            Log.w(TAG, "contactId is null");
            return;
        }

        ChatMessageManager chatMessageManager = MainApplication.getInstance().getChatMessageManager();

        // 当前聊天的上下文（userId + 变化回调监听）
        OnChatMessageChange onChatMessageChange = getOnChatMessageChange();
        CurrentChatMessageContext currentChatMessageContext = new CurrentChatMessageContext(
                contactId,
                onChatMessageChange
        );
        chatMessageManager.setCurrentChatMessageContext(currentChatMessageContext);
    }

    private OnChatMessageChange onChatMessageChange;

    private OnChatMessageChange getOnChatMessageChange(){
        if (onChatMessageChange == null){
            onChatMessageChange = list -> {
                chatVo.isLoading.setValue(false);

                // 图片消息：3.3 UI更新
                // 图片消息：3.3.1 数据类型转换
                Long myId = Optional.ofNullable(MainApplication.getInstance().getUserLoginInfoAo())
                        .map(ao -> ao.userId)
                        .orElse(null);
                List<ChatMessageItemVo> chatMessageItemVos = new ArrayList<>();
                for (MessageItem item : list){

                    ChatMessageItemVo chatMessageItemVo = item.toChatMessageItemVo(
                            myId
                    );

                    chatMessageItemVos.add(chatMessageItemVo);
                }

                // 图片消息：3.3.2 调用adapter UI更新
                notifyMessageListChange(chatMessageItemVos);
            };
        }
        return onChatMessageChange;
    }

    //===========selectImage

    private ActivityResultLauncher<Intent> selectImageLauncher;

    // 图片消息：3.初始化图片选择器
    public void initPictureSelectorLauncher(FragmentActivity activity){
        selectImageLauncher = activity.registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    // 图片消息：3.1 获取图片uri
                    Uri imageUri = Optional.ofNullable(result)
                            .map(ActivityResult::getData)
                            .map(Intent::getData)
                            .orElse(null);
                    if (imageUri != null){

                        long currentTime = System.currentTimeMillis();
                        String content = Optional.ofNullable(chatVo)
                                .map(cvo -> cvo.inputText)
                                .map(LiveData::getValue)
                                .orElse("");

                        // 图片消息：3.2 创建消息，赋值
                        // ui展示 （chatMessageManager 回调去处理）
                        ChatMessageItemVo vo = new ChatMessageItemVo();
                        vo.avatarUrlOrUri = imageUri.toString();
                        vo.content = chatVo.inputText.getValue();
                        vo.timestamp = currentTime;
                        vo.setTimeByStringTimeStamp(currentTime);
                        vo.isRead = false;
                        vo.viewType = ChatMessageItemVo.VIEW_TYPE_SENDER;
                        vo.messageType = MessageTypeEnum.image.code;

                        // 图片消息：3.3 缓存消息到本地

                        ChatMessageManager chatMessageManager = MainApplication.getInstance().getChatMessageManager();
                        // 图片消息：3.3.1 消息转为MessageItem
                        MessageItem item = MessageItem.getItemByChatMessageItemVo(
                                vo,
                                Optional.ofNullable(MainApplication.getInstance().getUserLoginInfoAo())
                                        .map(ao -> ao.userId)
                                        .orElse(NettyConstants.ERROR_ID),
                                chatVo.contactId
                        );
                        // 图片消息：3.3.2 ChatMessageManager缓存消息并回调UI更新
                        chatMessageManager.immediatelyAddChatMessage(
                                item,
                                chatVo.contactId,
                                getOnChatMessageChange()
                        );

                        // 图片消息：3.3.4 netty发送图片消息
                        sendPictureMessageFirst(
                                currentTime,
                                vo
                        );
                    }
                    else {
                        ToastUtils.showToast(activity, activity.getString(com.czy.appview.R.string.send_image_failed));
                    }
                }
        );
    }

    // 选择 + 发送图片
    // 图片消息：2.获取图片权限
    public void beginSelectPicture(FragmentActivity activity){
        PermissionUtil.requestPermissionsX(activity, new String[]{
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        }, new GainPermissionCallback() {
            @Override
            public void allGranted() {
                com.czy.baseutil.photo.SelectPhotoUtil.selectImageFromAlbum(selectImageLauncher);
            }

            @Override
            public void notGranted(String[] notGrantedPermissions) {
                ToastUtils.showToastActivity(activity,
                        activity.getString(com.czy.appview.R.string.gain_permission_failed)
                );
            }
        });
    }

    //===========Message

    private void initSocketReceiver(){
        chatApiHandler = new ChatApiHandler() {
            @Override
            public void receiveUserText(@NonNull UserTextDataResponse response) {
                MessageItem item = MessageItem.getByUserTextDataResponse(response);

                // 立刻将消息添加 (单条)
                // ChatList：5.本地发送数据源
                MainApplication.getInstance().getChatMessageManager().immediatelyAddChatMessage(
                        item,
                        chatVo.contactId,
                        getOnChatMessageChange()
                );
            }

            @Override
            public void receiveGroupText(@NonNull GroupTextDataResponse response) {
                // else
            }

            @Override
            public void haveReadMessage(@NonNull HaveReadMessageResponse response) {
                Log.d(TAG, "消息已读：haveReadUserId:" + response.haveReadUserId);
            }

            // 图片消息：6.2 执行图片消息处理逻辑
            @Override
            public void receiveUserImage(@NonNull UserImageResponse response) {
                // 图片消息：6.2.1 消息转换
                MessageItem item = MessageItem.getByUserImageResponse(response);

                // todo 要考虑后端的图片消息ttl过期之后再次获取图片的逻辑
                // 图片消息：6.2.2 缓存并展示回调
                MainApplication.getInstance().getChatMessageManager().immediatelyAddChatMessage(
                        item,
                        chatVo.contactId,
                        getOnChatMessageChange()
                );
            }
        };
    }

    // receive
    private ChatApiHandler chatApiHandler;

    //===========Picture
;
    // 图片消息：3.3.4 netty发送图片消息
    private void sendPictureMessageFirst(Long listTime, ChatMessageItemVo vo){
        SendImageRequest request = new SendImageRequest();
        request.senderId = Optional.ofNullable(MainApplication.getInstance().getUserLoginInfoAo())
                .map(ao -> ao.userId)
                .orElse(NettyConstants.ERROR_ID);
        request.receiverId = chatVo.contactId;
        request.timestamp = String.valueOf(listTime);
        request.content = vo.content;
        request.androidMessageId = vo.getItemId();
        try {
            // uriStr -> uri
            Uri imageUri = Uri.parse(vo.avatarUrlOrUri);
            String[] fileNames = ImageManager.getImageName(imageUri);
            if (fileNames != null && fileNames.length >= 2){
                request.fileName = fileNames[0] + "_" + request.timestamp + fileNames[1];
            }
            else {
                Log.w(TAG, "图片名称获取失败");
            }
        } catch (Exception e){
            Log.e(TAG, "sendPictureMessage: " + e);
        }
        // netty 发送图片的基本信息，等待获取fileId
        socketMessageSender.sendImageToUser(request);
    }

    // 图片消息：4 等待图片消息被后端确认返回fileId，然后http上传图片

    /**
     * 处理要求上传图片的指令
     * @param response  图片上传响应
     * @return  是否消费了EventBus的事件
     */
    private boolean handleUploadPictureMessageOrder(UploadFileResponse response){
        List<ChatMessageItemVo> list = chatVo.chatListVo.chatMessageList;
        if (list == null || list.isEmpty()){
            Log.i(TAG, "onMessageReceived: list is empty");
            return false;
        }

        Long fileId = response.fileId;

        for (ChatMessageItemVo vo : list){
            // 找到对应的消息
            if (response.messageId != null && response.messageId.equals(vo.getItemId())){

                Uri imageUri = Uri.parse(vo.avatarUrlOrUri);
                if (imageUri == null){
                    Log.i(TAG, "onMessageReceived:imageUri解析错误, imageUri is null");
                }

                File file = MainApplication.getInstance().getImageManager().imageUriToFile(
                        imageUri,
                        MainApplication.getInstance(),
                        BaseConfig.BITMAP_MAX_SIZE
                );
                if (file == null || !file.exists()) {
                    // 处理文件未创建或路径不正确的情况
                    Log.e(TAG, "Image file creation failed");
                    return false;
                }

                // 获取文件名
//                String originalFilename = file.getName(); // 使用 getName() 获取文件名

                // 获取文件扩展名
//                String fileExtension = originalFilename.contains(".") ?
//                        originalFilename.substring(originalFilename.lastIndexOf(".")) : ""; // 获取扩展名

                MultipartBody.Part filePart = com.czy.baseutil.file.FileUtil.createMultipartBodyPart(
                        file,
                        "file"
                );

                // 文件名称，方便后端保存
//                String fileName = MainApplication.getInstance().getUserLoginInfoAo().account + "_" + chatVo.contactAccount;

                // 创建其他参数请求体
                RequestBody fileIdPart = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(fileId));
                RequestBody senderIdPart = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(
                        MainApplication.getInstance().getUserLoginInfoAo().userId
                ));
                RequestBody receiverIdPart = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(
                        chatVo.contactId
                ));

                // 图片消息：5 上传图片
                uploadImageAndSend(
                        filePart,
                        fileIdPart,
                        senderIdPart,
                        receiverIdPart
                );
                return true;
            }
        }
        return false;
    }

    // 图片消息：5 上传图片
    private void uploadImageAndSend(MultipartBody.Part filePart, RequestBody fileIdPart,
                                    RequestBody senderIdPart, RequestBody receiverIdPart){
        apiRequestImpl.uploadAndSend(
                filePart,
                fileIdPart,
                senderIdPart,
                receiverIdPart,
                uploadResp -> {
                    Log.i(TAG, "图片消息发送成功");
                },
                throwable -> {
                    Log.e(TAG, "图片消息发送失败" + throwable);
                }
        );
    }

//    private void handleFileUpload(BaseResponse<FileUploadResponse> response) {
//        if (ViewModelUtil.handleResponse(response)) {
//            String uploadState = Optional.ofNullable(response.getData()).map(FileUploadResponse::getUploadState).orElse("");
////            ViewModelUtil.globalToast(uploadState);
//            Log.d(TAG, "上传状态：" + uploadState);
//        }
//    }

/*    @Deprecated(since = "2025/8/4 现在使用minio生成的uri加载，而不是直接从后端获取byte[]")
    private void downloadMessageImage(String url, long listItemCreatedTime){
        apiRequestImpl.downloadImage(url,
                response -> {
                    handleDownloadImage(response, listItemCreatedTime);
                },
                ViewModelUtil::globalThrowableToast
        );
    }

    @Deprecated(since = "2025/8/4 现在使用minio生成的uri加载，而不是直接从后端获取byte[]")
    private void handleDownloadImage(BaseResponse<FileDownloadBytesResponse> response, long listItemCreatedTime) {
        if (ViewModelUtil.handleResponse(response)) {
            ImageManager imageManager = new ImageManager();
            Bitmap bitmap = imageManager.bytesToBitmap(response.getData().getFileBytes());
            bitmap = imageManager.processImage(bitmap, BaseConfig.BITMAP_MAX_SIZE);

            // bitmap需要设置到正确的id上面去
            List<ChatMessageItemVo> list = Optional.ofNullable(chatVo)
                    .map(chatVo -> chatVo.chatListVo)
                    .map(chatListVo -> chatListVo.chatMessageList)
//                    .map(LiveData::getValue)
                    .orElse(null);
            if (list != null){
                Log.e("Intercep", "handleDownloadImage::listItemCreatedTime: " + listItemCreatedTime);
                for (int i = 0; i < list.size(); i++){
                    if (list.get(i).timestamp == listItemCreatedTime){
                        Log.e("Intercep", "handleDownloadImage111: " + i);
                        Log.i("Intercep", "list.get(i).content: " + list.get(i).content);
                        break;
                    }
                }
            }

            // bitmap -> ChatMessageItemVo设置值 -> viewModel.chatVo.chatListVo.chatMessageList.postValue(currentList);
            notifyMessageListChange();
        }
    }*/

    //-----------------------EventBus-----------------------

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onMessageReceived(UserTextDataResponse response) {
        // 检查匹配是否是该用户的信息
//        String receiverAccount = Optional.ofNullable(chatVo)
//                .map(vo -> vo.contactAccount)
//                .orElse("");
        Long receiverId = Optional.ofNullable(chatVo)
                .map(vo -> vo.contactId)
                .orElse(NettyConstants.ERROR_ID);
        if (receiverId.equals(response.getSenderId())){
            // 根据 message 的 type 执行对应的方法 TODO 梳理逻辑，这里有问题：chatListManager 和 消息队列分离了；chatListManager在Activity重新启动会出现数据丢失
            chatApiHandler.receiveUserText(response);
            // 移除已处理的粘性事件
            EventBus.getDefault().removeStickyEvent(response);
        }
    }

    // todo todo 需要把全部消息相关eventBus交给ChatManager，因为消息同时被Message和Chat监听，任何一方移除粘性事件都会让另一方消息丢失。
    // 图片消息：6 收到图片消息，在缓存并ui展示
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onMessageReceived(UserImageResponse response) {
        // 图片消息：6.1 消息匹配：检查匹配是否是该用户的信息
        Long contactId = Optional.ofNullable(chatVo)
                .map(vo -> vo.contactId)
                .orElse(NettyConstants.ERROR_ID);
        if (!NettyConstants.ERROR_ID.equals(contactId) && contactId.equals(response.getSenderId())){
            // 图片消息：6.2 执行图片消息处理逻辑
            chatApiHandler.receiveUserImage(response);
            // 移除已处理的粘性事件
            EventBus.getDefault().removeStickyEvent(response);
        }
    }

    // 被要求上传图片
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onMessageReceived(UploadFileResponse response){
        boolean isConsumed = handleUploadPictureMessageOrder(response);
        if (isConsumed){
            // 移除已处理的粘性事件
            EventBus.getDefault().removeStickyEvent(response);
        }
    }

    // 上传图片结果
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onReceiveUploadFileResult(Message response){
        // 移除已处理的粘性事件 todo 上传成功之后需要将消息转圈改为非转圈
        EventBus.getDefault().removeStickyEvent(response);
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
        MainApplication.getInstance().getChatMessageManager().cleanChatActivityParam();
        unInitEventBus();
    }
}
