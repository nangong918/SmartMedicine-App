package com.czy.easysocial.activity;


import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.lifecycle.LiveData;

import com.czy.appcore.BaseConfig;
import com.czy.baseUtilsLib.activity.BaseActivity;
import com.czy.baseUtilsLib.image.ImageLoadUtil;
import com.czy.baseUtilsLib.permission.GainPermissionCallback;
import com.czy.baseUtilsLib.permission.PermissionUtil;
import com.czy.baseUtilsLib.ui.ToastUtils;
import com.czy.baseUtilsLib.viewModel.ViewModelUtil;
import com.czy.customviewlib.view.chatMessage.ChatMessageAdapter;
import com.czy.dal.ao.chat.ChatActivityStartAo;
import com.czy.dal.constant.MessageTypeEnum;
import com.czy.dal.vo.entity.message.ChatMessageItemVo;
import com.czy.dal.vo.viewModeVo.chat.ChatVo;
import com.czy.easysocial.MainApplication;
import com.czy.easysocial.databinding.ActivityChatBinding;
import com.czy.easysocial.viewModel.ApiViewModelFactory;
import com.czy.easysocial.viewModel.ChatViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author 13225
 */
public class ChatActivity extends BaseActivity<ActivityChatBinding> {

    public ChatActivity() {
        super(ChatActivity.class);
    }

    @Override
    protected void init() {
        super.init();
        initPictureLauncher();
        initViewModel();
        initRecyclerView();
        initIntentData();
//        viewModel.fetchUserMessage(System.currentTimeMillis(), 20);
        // 此处加载，否则出现bug：startAo在请求之后，导致请求的参数是null
        viewModel.initialNetworkRequest(viewModel.chatVo.contactAccount);
    }

    @Override
    protected void setListener() {
        super.setListener();

        binding.imgvBack.setOnClickListener(v -> finish());

        binding.smSendMessage.setSendClickListener(v -> {
            viewModel.sendMessage();
            binding.smSendMessage.setEditMessage("");
        });

        binding.smSendMessage.setImgClickListener(v -> {
            PermissionUtil.requestPermissionsX(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, new GainPermissionCallback() {
                @Override
                public void allGranted() {
                    com.czy.baseUtilsLib.photo.SelectPhotoUtil.selectImageFromAlbum(selectImageLauncher);
                }

                @Override
                public void notGranted(String[] notGrantedPermissions) {
                    ToastUtils.showToastActivity(ChatActivity.this, "获取权限失败");
                }
            });
        });
    }

    //-----------------------Intent Data-----------------------

    private void initIntentData() {
        // 获取传递的对象
        try {
            Intent intent = getIntent();
            Optional.ofNullable(intent)
                    .map(i -> (ChatActivityStartAo)i.getSerializableExtra(
                            ChatActivityStartAo.class.getName()
                    ))
                    .ifPresent(ao -> {
                        viewModel.setStartAo(ao);
                    });
        } catch (Exception e) {
            Log.d(TAG, "initIntentData::get ChatActivityStartAo SerializableExtra Error: ", e);
            ToastUtils.showToast(this, "获取聊天对象失败");
            finish();
        }
        if (TextUtils.isEmpty(viewModel.chatVo.contactAccount)){
            Log.e(TAG, "initIntentData::chatActivityStartAo is null or contactId is empty");
            ToastUtils.showToast(this, "获取聊天对象失败");
            finish();
        }
    }


    //-----------------------ViewModel-----------------------

    private ChatViewModel viewModel;

    private void initViewModel(){
        ApiViewModelFactory apiViewModelFactory = new ApiViewModelFactory(MainApplication.getApiRequestImplInstance(), MainApplication.getInstance().getMessageSender());
        viewModel = ViewModelUtil.newViewModel(this, apiViewModelFactory, ChatViewModel.class);

        initViewModelVo();
    }

    private void initViewModelVo(){
        ChatVo chatVo = new ChatVo();

        viewModel.init(chatVo);

        // 观察数据
        observeData();

        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);
    }

    private void observeData(){
        // 观察RecyclerView
        Optional.ofNullable(viewModel)
                .map(vm -> vm.chatVo)
                .map(cvo -> cvo.chatListVo)
                .map(cvo -> cvo.chatMessageList)
                .ifPresent(liveData -> {
                    liveData.observe(this, newList -> {
                        Optional.ofNullable(((ChatMessageAdapter)binding.rclvMessage.getAdapter()))
                                .ifPresent(chatMessageAdapter -> {
                                    runOnUiThread(() -> {
                                        chatMessageAdapter.setCurrentList(
                                                newList,
                                                () -> {
                                                    Log.e("Intercep", "observeData:newList: " + (newList.size() - 1));
                                                    if (!newList.isEmpty()){
                                                        Log.e("Intercep", "observeData:newList::bitmap: " + newList.get(newList.size() - 1).bitmap);
                                                    }
                                                    runOnUiThread(() -> {
                                                        binding.rclvMessage.scrollToPosition(
                                                                chatMessageAdapter.getItemCount() - 1
                                                        );
                                                    });
                                                }
                                        );
                                    });
                                });
                    });
                });
        // 标题
        Optional.ofNullable(viewModel)
                .map(vm -> vm.chatVo)
                .map(cvo -> cvo.name)
                .ifPresent(liveData -> {
                    liveData.observe(this, newName -> {
                        binding.tvTitle.setText(newName);
                    });
                });
        // 头像
        Optional.ofNullable(viewModel)
                .map(vm -> vm.chatVo)
                .map(cvo -> cvo.avatarUrlOrUri)
                .ifPresent(liveData -> {
                    liveData.observe(this, newAvatarUrlOrUri -> {
                        if (!TextUtils.isEmpty(newAvatarUrlOrUri)) {
                            ImageLoadUtil.loadImageViewByResource(
                                    newAvatarUrlOrUri,
                                    binding.imgvCircle
                            );
                        }
                    });
                });
        // 输入框
//        Optional.ofNullable(viewModel)
//                .map(vm -> vm.chatVo)
//                .map(cvo -> cvo.inputText)
//                .ifPresent(liveData -> {
//                    liveData.observe(this, newText -> {
//                        Log.i(TAG, "initViewModelVo::inputText: " + newText);
//                        if (newText != null){
//                            binding.smSendMessage.setEditMessage(newText);
//                        }
//                    });
//                });
    }

    //-----------------------Recycler View-----------------------

    private void initRecyclerView(){
        List<ChatMessageItemVo> chatMessageList = Optional.ofNullable(viewModel)
                .map(ao -> ao.chatVo)
                .map(ao -> ao.chatListVo)
                .map(ao -> ao.chatMessageList)
                .map(LiveData::getValue)
                .orElse(new ArrayList<>());
        ChatMessageAdapter adapter = new ChatMessageAdapter(chatMessageList);
        binding.rclvMessage.setAdapter(adapter);
    }

    //===========Picture

    private ActivityResultLauncher<Intent> selectImageLauncher;

    private void initPictureLauncher(){
        selectImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    Intent data = result.getData();
                    if (data != null) {
                        Uri imageUri = data.getData();
                        viewModel.uriAtomicReference.set(imageUri);
                        Bitmap bitmap = MainApplication.getInstance().getImageManager().uriToBitmapMediaStore(this, imageUri);
                        bitmap = MainApplication.getInstance().getImageManager().processImage(bitmap, BaseConfig.BITMAP_MAX_SIZE);
                        ChatMessageItemVo chatMessageItemVo = new ChatMessageItemVo();
                        chatMessageItemVo.bitmap = bitmap;
                        chatMessageItemVo.setTimeByStringTimeStamp(System.currentTimeMillis());
                        chatMessageItemVo.viewType = ChatMessageItemVo.VIEW_TYPE_SENDER;
                        chatMessageItemVo.content = "";
                        chatMessageItemVo.messageType = MessageTypeEnum.image.code;
                        List<ChatMessageItemVo> currentList = Optional.ofNullable(viewModel)
                                .map(vm -> vm.chatVo)
                                .map(cvo -> cvo.chatListVo)
                                .map(cvo -> cvo.chatMessageList)
                                .map(LiveData::getValue)
                                .orElse(new ArrayList<>());

                        currentList.add(chatMessageItemVo);
                        viewModel.chatVo.chatListVo.chatMessageList.postValue(currentList);
                        // 发送图片消息
                        viewModel.sendPictureMessage(
                                this,
                                bitmap,
                                chatMessageItemVo.getCreatedTimestamp()
                        );
                    }
                });
    }

    @Override
    protected void onPause() {
        super.onPause();
        viewModel.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        viewModel.onDestroy();
    }
}

/**
 * TODO 架构优化与设计
 * 聊天记录ChatList
 * 一、数据源：外存：关系数据：SQLite
 *             非关系数据：File
 *             聊天记录怎么存储？有必要存储在数据库中吗？关系性很强吗？只有时间顺序需要索引没必要存储数据库吧？文件怎么索引内部内容？
 *             --结合Room的Paging3实现无缝分页加载
 *             --采用LRU缓存策略优化多媒体加载
 *             --根据用户idHash分表？
 *             --热数据存储在SQLite，冷数据存储在File
 *        内存：缓存List，Map
 *        网络：少量实时数据：Socket
 *              大量历史数据：Http
 *     数据List索引：timeStamp
 * 二、可能的情况：
 * 1.没有网络直接打开Activity
 *      1.直接从外存拿去数据
 * 2.有网络首次打开Activity
 *      1.读取外存数据
 *      2.读取内存消息队列数据
 *      3.Http网络请求数据
 * 3.有网络非首次打开Activity
 *      1.读取外存数据
 *      2.读取内存消息队列数据（不用再次请求Http，因为消息都在内存队列；如果Socket断开会从新请求Http）
 * 4.有网络，在Activity中，收到了消息
 *      1.Socket读取数据直接展示在Activity界面
 * 5.有网络，没有打开Activity（Activity存活），收到了消息
 *      1.Socket读取的数据通过EventBus异步通知存活的Activity界面
 * 6.有网络，没有打开Activity（Activity销毁），收到了消息
 *      1.将Socket的消息存储在消息队列中（等待Activity的打开）
 * 7.离线状态重连：在MessageFragment：Socket断开重连之后拉取Message界面Http消息记录
 *      断开之后就将MessageFragment和List<ChatActivity>的首次打开全部设置为true
 *      连接之后直接主动调用MessageList的Http方法，放在消息队列。
 *      打开Activity之后再调用对应User的ChatList的Http方法。
 * 8.离线状态重连：在ChatActivity：Socket断开重连之后拉取ChatList界面Http消息记录
 *      重连之后直接调用Http方法，展示在Activity
 * 三、存储策略
 *      缓存与IO：发送和收到之后都将消息存入消息队列，在消息达到一定数量或者一定时间之后，将消息存储到外存中。
 * 四、同步策略
 *      数据层面：内存，外存，网络获取的List<ChatItem>都进行时间戳同步：timeStamp
 *          需要思考用什么算法将对应的消息插入到对应的位置：快排，堆排序，二分排序，插入排序，选择排序，归并排序，快速排序，希尔排序，堆排序，基数排序，桶排序
 *          --归并排序：时间索引排序
 *          也需要考虑用什么数据类型：LinkList<ChatItem>？ArrayList<ChatItem>，Map<timeStamp, ChatItem>？
 *          还要考虑线程同步，因为可能有三个线程（外存线程，内存线程，网络线程）对同一个List<ChatItem>进行操作，所以需要考虑线程同步。
 *          --CopyOnWriteArrayList保证线程安全的同时保持读操作的高性能
 *          --通过状态LiveData实现细粒度的UI更新
 *      View层面：
 *          就使用RecyclerView？是否有更好的办法？surfaceView创建Ui外线程避免ui卡顿？
 *          RecyclerView怎么根据新的List<ChatItem>对象进行更新？
 *              LiveData？那ChatItem怎么设计成LiveData？怎么设计使得每次只更新指定的Item而不是更新整个RecyclerView
 *              DiffUtil？怎么设计DiffUtils使得每次只更新被更新的数据而不是刷新政改革RecyclerView
 * 五、检测指标
 * RecyclerView的帧率（确保>60fps）
 * 内存占用（防止OOM）
 * 数据库IO操作耗时
 *      100万条消息的查询性能
 *      并发查询性能
 *      分页加载性能
 *      缓存命中率
     *      指标                | 目标值
     * ---------------------------------
     * 单次查询时间        | < 50ms
     * 分页加载时间        | < 100ms
     * 缓存命中率         | > 90%
     * 内存占用           | < 50MB
     * 数据库文件大小      | < 500MB
 * 网络请求的响应时间
 *
 * 后端设计：
 * Redis：快速缓存命中
 * MySQL主库存储用户之间的消息关系
 * ES：存储热数据
 * HBase：存储冷数据
 */