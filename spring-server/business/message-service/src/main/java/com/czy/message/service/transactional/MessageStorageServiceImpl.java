package com.czy.message.service.transactional;

import com.czy.api.constant.MessageTypeEnum;
import com.czy.api.domain.Do.message.UserChatMessageDo;
import io.jsonwebtoken.lang.Collections;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 13225
 * @date 2025/4/16 15:33
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class MessageStorageServiceImpl implements MessageStorageService{

    private final MessageTransactionalService messageTransactionalService;

    @Override
    public void storeMessagesToDatabase(List<UserChatMessageDo> messages) {
        // type 文本：[[redis]mongo,es]
        List<UserChatMessageDo> textMessages = getTypeMessage(messages, MessageTypeEnum.text.code);
        messageTransactionalService.saveTextMessage(textMessages);
        // type 文件：[[redis]mongo,oss]
        List<UserChatMessageDo> fileMessages = getFileMessage(messages);
        messageTransactionalService.saveFileMessage(fileMessages);
    }

    private List<UserChatMessageDo> getTypeMessage(List<UserChatMessageDo> messages, int type){
        if (Collections.isEmpty(messages)){
            return new ArrayList<>();
        }
        return messages.stream()
                .filter(message -> message.getMsgType() == type)
                .collect(Collectors.toList());
    }

    private List<UserChatMessageDo> getFileMessage(List<UserChatMessageDo> messages){
        if (Collections.isEmpty(messages)){
            return new ArrayList<>();
        }
        return messages.stream()
                // 不是文本消息就是文件消息
                .filter(message -> message.getMsgType() != MessageTypeEnum.text.code)
                .collect(Collectors.toList());
    }
}
