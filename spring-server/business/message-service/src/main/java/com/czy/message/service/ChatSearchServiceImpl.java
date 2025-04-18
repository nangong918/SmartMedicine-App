package com.czy.message.service;

import com.czy.api.api.message.ChatSearchService;
import com.czy.api.domain.Do.message.UserChatMessageEsDo;
import com.czy.message.mapper.es.UserChatMessageEsMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 13225
 * @date 2025/4/16 18:14
 */
@Slf4j
@RequiredArgsConstructor
@Service
@org.apache.dubbo.config.annotation.Service(protocol = "dubbo", version = "1.0.0")
public class ChatSearchServiceImpl implements ChatSearchService {

    private final UserChatMessageEsMapper userChatMessageEsMapper;

    @Override
    public List<UserChatMessageEsDo> searchUserChatMessage(Long senderId, String keyword) {
        return userChatMessageEsMapper.findBySenderIdAndMsgContentContaining(senderId, keyword);
    }

    @Override
    public List<UserChatMessageEsDo> searchUserChatMessage(Long senderId, Long receiverId, String keyword) {
        return userChatMessageEsMapper.findBySenderIdAndReceiverIdAndMsgContentContaining(senderId, receiverId, keyword);
    }

    @Override
    public List<UserChatMessageEsDo> searchUserChatMessageLimit(Long senderId, String keyword, Integer page) {
        Pageable pageable = getPageable(page);

        // 调用 repository 方法
        Page<UserChatMessageEsDo> results = userChatMessageEsMapper.findBySenderIdAndMsgContentContaining(senderId, keyword, pageable);

        return results.getContent(); // 返回查询结果
    }

    @Override
    public List<UserChatMessageEsDo> searchUserChatMessageLimit(Long senderId, Long receiverId, String keyword, Integer page) {
        Pageable pageable = getPageable(page);

        // 调用 repository 方法
        Page<UserChatMessageEsDo> results = userChatMessageEsMapper.findBySenderIdAndReceiverIdAndMsgContentContaining(senderId, receiverId, keyword, pageable);

        return results.getContent();
    }

    private Pageable getPageable(Integer page) {
        int finalPage = 10;
        if (page != null && page > 0 && page < 50){
            finalPage = page;
        } else if (page != null && page >= 50){
            finalPage = 50;
        }
        return PageRequest.of(0, finalPage, Sort.by("timestamp").ascending());
    }
}
