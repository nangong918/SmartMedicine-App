package com.czy.appcore.network.netty.api.receive;

import androidx.annotation.NonNull;

import com.czy.dal.annotation.MessageType;
import com.czy.dal.constant.netty.ResponseMessageType;
import com.czy.dal.dto.netty.response.UploadFileResponse;
import com.czy.dal.netty.Message;

public interface OssApiHandler {

    @MessageType(value = ResponseMessageType.Oss.UPLOAD_FILE_NOW, desc = "后端要求现在上传文件")
    void receiveUploadFileOrder(@NonNull UploadFileResponse response);

    @MessageType(value = ResponseMessageType.Oss.UPLOAD_FILE, desc = "收到上传文件结果")
    void receiveUploadResult(@NonNull Message response);

    @MessageType(value = ResponseMessageType.Oss.DELETE_FILE, desc = "收到删除文件结果")
    void receiveDeleteResult(@NonNull Message response);
}
