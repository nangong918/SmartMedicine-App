package com.czy.message.service.transactional;

import com.czy.api.converter.mongoEs.UserChatMessageEsConverter;
import com.czy.api.domain.Do.message.UserChatMessageDo;
import com.czy.api.domain.Do.message.UserChatMessageEsDo;
import com.czy.message.mapper.es.UserChatMessageEsMapper;
import com.czy.message.mapper.mongo.UserChatMessageMongoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author 13225
 * @date 2025/4/16 15:51
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class MessageTransactionalServiceImpl implements MessageTransactionalService{

    private final UserChatMessageMongoMapper userChatMessageMongoMapper;
    private final UserChatMessageEsMapper userChatMessageEsMapper;
    private final UserChatMessageEsConverter userChatMessageEsConverter;

    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @Override
    public void saveTextMessage(List<UserChatMessageDo> messages) {
        // type 文本：[[redis]mongo,es]
        // mongo
        userChatMessageMongoMapper.saveAllMessage(messages);
        // es
        List<UserChatMessageEsDo> esMessages =userChatMessageEsConverter.mongoListToEsList(messages);
        userChatMessageEsMapper.saveAll(esMessages);
    }

    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @Override
    public void saveFileMessage(List<UserChatMessageDo> messages) {
        // type 文件：[[redis]mongo,oss]
        // mongo
        userChatMessageMongoMapper.saveAllMessage(messages);
        // oss：oss是另外的http请求
    }
}
