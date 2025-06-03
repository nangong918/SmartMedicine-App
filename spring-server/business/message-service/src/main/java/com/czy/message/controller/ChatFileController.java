package com.czy.message.controller;

import com.czy.api.api.oss.OssService;
import com.czy.api.api.user.UserService;
import com.czy.api.constant.message.MessageConstant;
import com.czy.api.constant.netty.NettyConstants;
import com.czy.api.constant.netty.ResponseMessageType;
import com.czy.api.constant.oss.FileConfig;
import com.czy.api.domain.Do.user.UserDo;
import com.czy.api.domain.dto.base.BaseResponse;
import com.czy.api.domain.dto.http.response.UserImageResponse;
import com.czy.message.component.RabbitMqSender;
import com.utils.mvc.service.MinIOService;
import domain.FileOptionResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author 13225
 * @date 2025/4/30 10:29
 */
@Slf4j
@CrossOrigin(origins = "*") // 跨域
@RestController
@Validated // 启用校验
@RequiredArgsConstructor // 自动注入@Autowired
@RequestMapping(MessageConstant.ChatFile_CONTROLLER)
public class ChatFileController {

    private final String messageFileBucket = FileConfig.MESSAGE_IMAGE_BUCKET;
    @Reference(protocol = "dubbo", version = "1.0.0", check = false)
    private UserService userService;
    @Reference(protocol = "dubbo", version = "1.0.0", check = false)
    private OssService ossService;
    private final MinIOService minIOService;
    private final RabbitMqSender rabbitMqSender;
    @PostMapping("/uploadFileSend")
    public BaseResponse<String> uploadPostFilesAndSendMessage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("fileId") Long fileId,
            @RequestParam("messageId") Long messageId,
            @RequestParam("senderAccount") String senderAccount,
            @RequestParam("receiverAccount") String receiverAccount
    ){
        UserDo senderDo = userService.getUserByAccount(senderAccount);
        Long receiverId = userService.getIdByAccount(receiverAccount);
        if (senderDo == null || senderDo.getId() == null || receiverId == null){
            return BaseResponse.LogBackError("发送者或接收者id错误");
        }
        // messageId还未存储到mongoDb，此处不是查询，而是告诉receiver是哪条消息需要更新
        List<MultipartFile> files = new ArrayList<>();
        files.add(file);

        // 上传到minio
        FileOptionResult fileOptionResult = minIOService.uploadFiles(files, senderDo.getId(), messageFileBucket);
        if (fileOptionResult.getSuccessFiles().isEmpty()){
            return BaseResponse.LogBackError("上传文件失败");
        }
        // 上传成功之后记录到数据库
        ossService.uploadFilesRecord(fileOptionResult.getSuccessFiles(), senderDo.getId(), messageFileBucket);
        List<Long> fileIds = Collections.singletonList(fileId);
        List<String> fileUrl = ossService.getFileUrlsByFileIds(fileIds);

        if (fileUrl.isEmpty()){
            return BaseResponse.LogBackError("fileUrl解析失败");
        }
        UserImageResponse userImageResponse = new UserImageResponse();
        userImageResponse.setSenderId(senderAccount);
        userImageResponse.setReceiverId(receiverAccount);
        userImageResponse.setType(ResponseMessageType.Chat.RECEIVE_USER_IMAGE_MESSAGE);
        userImageResponse.setTimestamp(String.valueOf(System.currentTimeMillis()));

        userImageResponse.setMessageId(messageId);
        userImageResponse.setImageUrl(fileUrl.get(0));
        userImageResponse.setTitle(NettyConstants.imageMessageTitle);
        userImageResponse.setAvatarFileId(null);
        userImageResponse.setSenderName(senderDo.getUserName());
        // 已经是响应类型，不会内部转换
        rabbitMqSender.push(userImageResponse);

        return BaseResponse.getResponseEntitySuccess("发送成功");
    }
}
