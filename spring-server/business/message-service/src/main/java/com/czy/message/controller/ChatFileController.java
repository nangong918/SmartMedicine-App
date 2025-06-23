package com.czy.message.controller;

import com.czy.api.api.oss.OssService;
import com.czy.api.api.user_relationship.UserService;
import com.czy.api.constant.message.MessageConstant;
import com.czy.api.constant.netty.NettyConstants;
import com.czy.api.constant.netty.ResponseMessageType;
import com.czy.api.domain.Do.user.UserDo;
import com.czy.api.domain.ao.oss.FileIsExistAo;
import com.czy.api.domain.dto.base.BaseResponse;
import com.czy.api.domain.dto.socket.response.UserImageResponse;
import com.czy.message.mq.sender.RabbitMqSender;
import com.utils.mvc.service.MinIOService;
import domain.FileIsExistResult;
import domain.FileOptionResult;
import domain.SuccessFile;
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
import java.util.List;
import java.util.stream.Collectors;

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
            @RequestParam("senderId") Long senderId,
            @RequestParam("receiverId") Long receiverId
    ){
        if (file == null){
            return BaseResponse.LogBackError("上传文件不能为空");
        }
        if (fileId == null){
            return BaseResponse.LogBackError("请检查文件id正确性");
        }

        UserDo senderDo = userService.getUserById(senderId);
        UserDo receiverDo = userService.getUserById(receiverId);
        if (senderDo == null || senderDo.getId() == null ||
                receiverDo == null || receiverDo.getId() == null){
            return BaseResponse.LogBackError("用户id信息错误");
        }

        String chatPostImageBucket = MessageConstant.MESSAGE_FILE_BUCKET + senderId;

        /*
          幂等性：
          1.判断：userId + fileName + bucketName + fileSize共同判断
          2.输入格式：List<FileIsExistAo>
          3.返回格式：List<FileIsExistResult>
          4.上传格式：List<MultipartFile>, List<FileIsExistResult> （不适用Map，但是要求两者要一一对应）
          5.上传结果格式：FileOptionResult
         */
        List<FileIsExistAo> fileIsExistAos = new ArrayList<>(1);
        String fileName = file.getOriginalFilename();
        Long fileSize = file.getSize();

        FileIsExistAo fileIsExistAo = new FileIsExistAo();
        fileIsExistAo.setFileName(fileName);
        fileIsExistAo.setFileSize(fileSize);
        fileIsExistAo.setUserId(senderId);
        fileIsExistAo.setBucketName(chatPostImageBucket);

        fileIsExistAos.add(fileIsExistAo);

        // 幂等性结果
        List<FileIsExistResult> results = ossService.checkFilesExistForResult(fileIsExistAos);

        List<MultipartFile> files = new ArrayList<>(1);
        files.add(file);
        // 上传到minIO
        FileOptionResult fileOptionResult = minIOService.uploadFilesWithIdempotent(
                files,
                results,
                chatPostImageBucket,
                senderId
        );

        // 上传记录数据到mysql
        ossService.uploadFilesRecord(fileOptionResult.getSuccessFiles(), senderId, chatPostImageBucket);

        // 获取成功ID
        List<Long> successIds = fileOptionResult.getSuccessFiles()
                .stream()
                .map(SuccessFile::getFileId)
                .collect(Collectors.toList());

        // 获取url
        List<String> urls = ossService.getFileUrlsByFileIds(successIds);
        if (urls.isEmpty()){
            return BaseResponse.LogBackError("fileUrl解析失败");
        }

        // 将url打包为响应体发送给接收方：
        UserImageResponse userImageResponse = new UserImageResponse();
        userImageResponse.setSenderId(senderId);
        userImageResponse.setReceiverId(receiverId);
        userImageResponse.setType(ResponseMessageType.Chat.RECEIVE_USER_IMAGE_MESSAGE);
        userImageResponse.setTimestamp(String.valueOf(System.currentTimeMillis()));
        userImageResponse.setAvatarFileId(fileId);
        userImageResponse.setMessageId(messageId);
        userImageResponse.setImageUrl(urls.get(0));
        userImageResponse.setTitle(NettyConstants.imageMessageTitle);
        userImageResponse.setAvatarFileId(null);
        userImageResponse.setSenderName(senderDo.getUserName());
        // 已经是响应类型，不会内部转换
        rabbitMqSender.push(userImageResponse);

        return BaseResponse.getResponseEntitySuccess("发送成功");
    }
}
