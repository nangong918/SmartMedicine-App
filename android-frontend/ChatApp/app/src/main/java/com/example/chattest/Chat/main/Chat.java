package com.example.chattest.Chat.main;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;
import com.example.chattest.Chat.message.SendMessage;
import com.example.chattest.Chat.message.MessageBackType;
import com.example.chattest.Chat.message.MessageRequestType;
import com.example.chattest.Chat.recyclerview.ChatAdapter;
import com.example.chattest.Test.MyDebug;
import com.example.chattest.URL.UrlUtil;
import com.example.chattest.Utils.CallBackInterface;
import com.example.chattest.Utils.RequestUtils;
import com.example.chattest.Utils.TimeUtils;
import com.example.chattest.Utils.Type.R_Util;
import com.example.chattest.Utils.Type.R_dataType;
import com.example.chattest.databinding.ActivityChatBinding;

import java.util.ArrayList;
import java.util.List;

public class Chat extends AppCompatActivity implements CallBackInterface {
    private final String RequestClass = "Chat";
    private ActivityChatBinding binding;
    public List<com.example.chattest.Chat.recyclerview.ChatAdapter.ChatItem> chatItems;

    public com.example.chattest.Chat.recyclerview.ChatAdapter ChatAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityChatBinding.inflate(getLayoutInflater());
        //UIThread();
        setContentView(binding.getRoot());
        Init(binding);
    }

    private void UIThread(){
        // 在主线程中创建一个 Handler
        Handler handler = new Handler(Looper.getMainLooper());

        // 线程休眠这里设置等待时间为5秒
        final int sleepTime = 5000;

        // 在主线程中创建一个线程
        Thread checkMessageThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    // 检查 receivedMessage 是否发生变化
                    if (!SendMessage.receivedMessage.isEmpty()) {
                        // 在 UI 线程中更新 UI
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                // 创建新的 ChatItem 对象
                                com.example.chattest.Chat.recyclerview.ChatAdapter.ChatItem newItem = new ChatAdapter.ChatItem();
                                newItem.viewType = 1; // 假设该项为发送方对话框
                                newItem.Message = SendMessage.receivedMessage;
                                SendMessage.receivedMessage = "";
                                TimeUtils timeUtils = new TimeUtils();
                                newItem.Date = timeUtils.getCurrentDateTime();

                                // 将新的 ChatItem 添加到 chatItems 列表中
                                chatItems.add(newItem);

                                // 通知 Adapter 数据集发生变化
                                ChatAdapter.notifyItemInserted(chatItems.size() - 1);

                                // 滚动 RecyclerView 到最后一项
                                binding.chatRecyclerview.smoothScrollToPosition(chatItems.size() - 1);
                            }
                        });
                        // 等待一段时间后再次检查
                        try {
                            Thread.sleep(sleepTime);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        // 启动线程
        checkMessageThread.start();
    }

    private void Init(ActivityChatBinding binding){
        setListeners();
        chatItems = new ArrayList<>();
        binding.progressBar.setVisibility(View.GONE);
        ChatAdapter = new ChatAdapter(chatItems);
        binding.chatRecyclerview.setAdapter(ChatAdapter);
    }

    private void setListeners(){
        binding.layoutSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = sendMessage();
                if(!message.isEmpty()){
                    // 创建新的 ChatItem 对象
                    com.example.chattest.Chat.recyclerview.ChatAdapter.ChatItem newItem = new ChatAdapter.ChatItem();
                    newItem.viewType = 0; // 假设该项为发送方对话框
                    newItem.Message = message;
                    TimeUtils timeUtils = new TimeUtils();
                    newItem.Date = timeUtils.getCurrentDateTime();

                    // 将新的 ChatItem 添加到 chatItems 列表中
                    chatItems.add(newItem);

                    // 通知 Adapter 数据集发生变化
                    ChatAdapter.notifyItemInserted(chatItems.size() - 1);

                    // 滚动 RecyclerView 到最后一项
                    binding.chatRecyclerview.smoothScrollToPosition(chatItems.size() - 1);

                    // 发送消息
                    MessageRequestType messageType = new MessageRequestType(message);
                    R_dataType rDataType = new R_dataType(messageType);
                    String jsonData = R_Util.R_JsonUtils.toJson(rDataType);
                    RequestUtils request_messageSend = new RequestUtils(10*1000,RequestClass,"POST", UrlUtil.GetDialogAI_url(),jsonData);
                    request_messageSend.callback = Chat.this;
                    request_messageSend.StartThread();
                }
            }
        });
        binding.imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public String sendMessage(){
        String message = binding.editTextInput.getText().toString();
        binding.editTextInput.setText(""); // 清空文本框内容
        return message;
    }

    private void getNewRecyclerViewItem(String message){
        com.example.chattest.Chat.recyclerview.ChatAdapter.ChatItem newItem = new ChatAdapter.ChatItem();
        newItem.viewType = 1; // 假设该项为发送方对话框
        newItem.Message = message;
        TimeUtils timeUtils = new TimeUtils();
        newItem.Date = timeUtils.getCurrentDateTime();

        // 将新的 ChatItem 添加到 chatItems 列表中
        chatItems.add(newItem);

        // 通知 Adapter 数据集发生变化
        ChatAdapter.notifyItemInserted(chatItems.size() - 1);

        // 滚动 RecyclerView 到最后一项
        binding.chatRecyclerview.smoothScrollToPosition(chatItems.size() - 1);
    }

    @Override
    public void onSuccess(String callbackClass, R_dataType rData) {
        MyDebug.Print("Chat成功");
        if(callbackClass.equals(RequestClass)){
            MessageBackType messageBackType = R_Util.R_JsonUtils.parseData(rData.GetKeywordData(), MessageBackType.class);
            if(messageBackType != null && messageBackType.reply != null){
                getNewRecyclerViewItem(messageBackType.reply);
            }
        }
    }
    @Override
    public void onFailure(String callbackClass) {
        MyDebug.Print("Chat失败");
        if(callbackClass.equals(RequestClass)){
            //提示连接失败
            Toast.makeText(this,"DialogAI服务器连接失败",Toast.LENGTH_SHORT).show();
        }
    }
}