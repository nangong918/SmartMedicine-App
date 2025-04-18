package com.czy.message.mapper.es;

import com.czy.api.domain.Do.message.UserChatMessageEsDo;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @author 13225
 * @date 2025/4/16 17:39
 * 吊炸天的Spring Data Elasticsearch能通过函数名称实现查询DSL语句来查询
 */
public interface UserChatMessageEsMapper extends ElasticsearchRepository<UserChatMessageEsDo, Long> {
    // 查找 通过senderId和keyword查找
    List<UserChatMessageEsDo> findBySenderIdAndMsgContentContaining(Long senderId, String msgContent);

    // 查找 通过senderId和receiverId和keyword查找
    List<UserChatMessageEsDo> findBySenderIdAndReceiverIdAndMsgContentContaining(Long senderId, Long receiverId, String msgContent);

    // 查找通过 senderId 和 keyword 查找，支持分页
    Page<UserChatMessageEsDo> findBySenderIdAndMsgContentContaining(Long senderId, String msgContent, Pageable pageable);

    // 查找通过 senderId 和 receiverId 和 keyword 查找，支持分页
    Page<UserChatMessageEsDo> findBySenderIdAndReceiverIdAndMsgContentContaining(Long senderId, Long receiverId, String msgContent, Pageable pageable);
}
