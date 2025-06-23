package com.czy.message.controller;


import com.czy.api.api.message.ChatSearchService;
import com.czy.api.api.message.ChatService;
import com.czy.api.api.user_relationship.UserService;
import com.czy.api.constant.message.MessageConstant;
import com.czy.api.converter.domain.message.UserChatMessageConverter;
import com.czy.api.converter.mongoEs.UserChatMessageEsConverter;
import com.czy.api.domain.Do.message.UserChatMessageDo;
import com.czy.api.domain.Do.message.UserChatMessageEsDo;
import com.czy.api.domain.Do.user.UserDo;
import com.czy.api.domain.ao.message.FetchUserMessageAo;
import com.czy.api.domain.bo.message.UserChatLastMessageBo;
import com.czy.api.domain.bo.message.UserChatMessageBo;
import com.czy.api.domain.dto.base.BaseResponse;
import com.czy.api.domain.dto.http.base.BaseHttpRequest;
import com.czy.api.domain.dto.http.request.FetchUserMessageRequest;
import com.czy.api.domain.dto.http.request.FetchUserMessageResponse;
import com.czy.api.domain.dto.http.request.KeywordChatHistoryRequest;
import com.czy.api.domain.dto.http.response.UserNewMessageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 13225
 * @date 2025/2/25 11:06
 * Http拉取全部消息，不使用Netty的WebSocket而是使用Http；因为拉取的数据太大了
 * 冷热分离设计思路
 * 热数据用Redis或MongoDB，冷数据存到MySQL或文件系统
 * 单表性能差，分表是必须的。按用户ID或时间分表，比如每个月一个表，或者按用户哈希分到不同库。这样可以分散查询压力
 * 考虑读写分离。主库处理写操作，从库处理读操作，减少主库压力
 * Elasticsearch进行全文检索也是个点，如果用户需要搜索聊天内容，ES可以加速这类查询。不过ES适合文本搜索，不推荐作为主存储，所以需要和主数据库同步
 * RabbitMQ可以用作异步写入，削峰填谷，避免高并发写入直接冲击数据库。比如，收到消息后先发到队列，消费者慢慢写入数据库，提高系统吞吐量
 * 文件存储方面，对于特别大的数据，比如图片或文件，可以存到对象存储，数据库只存路径
 * 缓存策略方面，用Redis存储最近活跃的聊天记录，减少对数据库的访问。比如每个用户的最近100条消息存Redis，查询时先查缓存，没有再去数据库
 * 数据归档也很重要。定期将旧数据迁移到冷存储，如HBase或文件系统，减少主数据库的数据量，提高查询效率
 * MongoDB适合存储非结构化的聊天数据，容易扩展，适合分片。而MySQL需要分库分表，结构更固定。结合ES做搜索，Redis做缓存，RabbitMQ做消息队列，这样的组合应该可以应对亿级数据
 *
 * 热数据（最近活跃聊天）
 * 存储引擎：MongoDB（适合非结构化数据、自动分片、高并发读写）
 * 按会话ID分片，存储最近3个月的聊天记录。
 * 支持按时间范围快速查询。
 *
 * Redis缓存：
 * 每个会话的最近100条消息缓存到Redis（Key格式：chat:{session_id}:recent）。
 * 用户进入聊天界面时优先读取缓存。
 *
 * 冷数据（历史聊天）
 * 存储引擎：MySQL分库分表 + 对象存储（如S3）
 * MySQL按月分表（例如message_202301），存储消息元数据（发送者、时间、会话ID等）。
 * 聊天内容（文本/文件）存储到对象存储，MySQL中仅保存文件路径。
 * 历史数据查询时，先查MySQL定位时间范围，再拉取对象存储内容。
 *
 * 分库分表策略
 * 分片键：会话ID或用户ID（根据查询模式选择）
 * 分片规则：
 * -- 示例：按会话ID哈希分库（16个库）
 * db_index = hash(session_id) % 16
 * -- 按月分表（每月一张表）
 * table_name = "messages_" + year_month
 * 分散单表压力，避免亿级数据集中在一张表。
 * 按时间分表天然支持冷热分离。
 *
 * 读写分离与异步处理
 * 写操作：
 * 消息发送后，优先写入Redis和MongoDB（热数据）。
 * 异步通过RabbitMQ将消息持久化到MySQL和对象存储（削峰填谷）。
 *
 * 读操作：
 * 最近消息：从Redis/MongoDB查询。
 * 历史消息：从MySQL分表 + 对象存储查询。
 * 全文检索：通过Elasticsearch（索引消息内容）。
 *
 * 场景1：获取最近聊天记录：
 * Redis缓存最近100条消息，优先读取缓存。不足时从MongoDB拉取（按时间倒序）
 * MongoDB索引：db.recent_messages.createIndex({ session_id: 1, timestamp: -1 })
 *
 * 场景2：搜索历史消息
 * 用户输入关键词 → 请求Elasticsearch → 返回匹配的消息ID → 从MySQL获取元数据 → 从对象存储获取内容
 *
 * 数据同步与一致性
 * Redis/MongoDB同步：
 * 通过消息队列异步更新，容忍秒级延迟。
 * MySQL/ES同步：
 * 使用Binlog监听（如Canal）将MySQL数据实时同步到ES。
 * 最终一致性模型，保障搜索结果的及时性。
 *
 * 扩容与容灾
 * MongoDB：
 * 启用分片集群，动态添加Shard应对数据增长。
 * MySQL：
 * 按月分表，历史表可迁移到只读从库或归档存储。
 * ES：
 * 按时间创建索引（如messages-2023-08），定期关闭旧索引节省资源。
 *
 *
    数据类型	存储方案	查询延迟	成本	适用场景
    热数据	MongoDB + Redis	毫秒级	高	实时聊天、最近消息
    冷数据	MySQL分表 + 对象存储	10-100ms	低	历史消息翻页
    搜索数据	Elasticsearch	10-50ms	中	关键词搜索
    文件	对象存储（S3/MinIO）	100ms-1s	低	图片/文件消息
 */
@Slf4j
@CrossOrigin(origins = "*") // 跨域
@RestController
@Validated // 启用校验
@RequiredArgsConstructor // 自动注入@Autowired
@RequestMapping(MessageConstant.Chat_CONTROLLER)
public class ChatController {

    private final ChatService chatService;
    private final ChatSearchService chatSearchService;
    private final UserChatMessageConverter userChatMessageConverter;
    private final UserChatMessageEsConverter userChatMessageEsConverter;
    @Reference(protocol = "dubbo", version = "1.0.0", check = false)
    private UserService userService;


    /**
     * 从Redis拉取用户的全部聊天消息(限制200条，超过就流式传输)：某个用户跟所有用户的1条最新消息List
     * @param request
     * @return 跟每个用户的最新一条消息
     */
    @PostMapping("/getUserNewMessage")
    public BaseResponse<UserNewMessageResponse>
    getUserNewMessage(@Valid @RequestBody BaseHttpRequest request) {
        if (!userService.checkUserExist(request.getSenderId())){
            return BaseResponse.LogBackError("用户不存在");
        }
        // 获取用户的最新消息List
        List<UserChatLastMessageBo> lastMessageList = chatService.getUserAllChatMessage(request.getSenderId());
        // 封装响应
        UserNewMessageResponse userNewMessageResponse = new UserNewMessageResponse();
        userNewMessageResponse.setLastMessageList(lastMessageList);
        return BaseResponse.getResponseEntitySuccess(userNewMessageResponse);
    }

    /**
     * 拉取用户和某个用户全部聊天消息(分页：一次拉取50条最新聊天消息)
     * @param request   拉取用户和某个用户全部聊天消息的请求
     * @return  用户和某个用户全部聊天消息
     */
    @PostMapping("/fetchUserMessage")
    public BaseResponse<FetchUserMessageResponse>
    fetchUserMessage(@Valid @RequestBody FetchUserMessageRequest request) {
        // 封装请求 -> Ao
        FetchUserMessageAo fetchUserMessageAo = new FetchUserMessageAo();
        fetchUserMessageAo.setSenderAccount(request.getSenderAccount());
        fetchUserMessageAo.setReceiverAccount(request.getReceiverAccount());
        fetchUserMessageAo.setTimestampIndex(request.getTimestampIndex());
        fetchUserMessageAo.setMessageCount(request.getMessageCount());
        // 获取用户聊天消息
        List<UserChatMessageBo> messageList = chatService.getUserChatMessage(fetchUserMessageAo);
        // 封装响应
        FetchUserMessageResponse fetchUserMessageResponse = new FetchUserMessageResponse();
        fetchUserMessageResponse.setMessageList(messageList);
        return BaseResponse.getResponseEntitySuccess(fetchUserMessageResponse);
    }

    /**
     * 获取用户的全部keyword聊天记录
     * @param request   聊天记录请求
     * @return          用户的全部keyword聊天记录
     */
    @PostMapping("/getUserKeyChatHistory")
    public BaseResponse<FetchUserMessageResponse>
    getUserKeyChatHistory(@Valid @RequestBody KeywordChatHistoryRequest request) {
        UserDo userDo = userService.getUserByAccount(request.getSenderAccount());
        if (userDo == null || userDo.getId() == null) {
            String warningMessage = String.format("用户account不存在，account: %s", request.getSenderAccount());
            return BaseResponse.LogBackError(warningMessage);
        }

        List<UserChatMessageEsDo> messageEsList;
        // 没有receiver查询全部
        if (StringUtils.isEmpty(request.getReceiverAccount())) {
            // 如果没有接收者账户，查询发送者的聊天记录
            messageEsList = chatSearchService.searchUserChatMessageLimit(userDo.getId(), request.getKeyword(), 20);
        }
        // 有receiver查询局部
        else {
            // 如果有接收者账户，查询发送者和接收者之间的聊天记录
            UserDo receiverDo = userService.getUserByAccount(request.getReceiverAccount());
            if (receiverDo == null || receiverDo.getId() == null) {
                String warningMessage = String.format("用户account不存在，account: %s", request.getReceiverAccount());
                return BaseResponse.LogBackError(warningMessage);
            }
            messageEsList = chatSearchService.searchUserChatMessageLimit(userDo.getId(), receiverDo.getId(), request.getKeyword(), 20);
        }

        List<UserChatMessageDo> messageList = userChatMessageEsConverter.esListToMongoList(messageEsList);
        if (messageList.isEmpty()) {
            return BaseResponse.getResponseEntitySuccess(new FetchUserMessageResponse());
        }
        else {
            List<Long> idList = messageList.stream().map(UserChatMessageDo::getId).collect(Collectors.toList());
            List<String> accountList = userService.getUserAccountListByUserIdList(idList);
            List<UserChatMessageBo> messageBoList = new ArrayList<>();

            for (int i = 0; i < messageList.size(); i++) {
                String receiver = StringUtils.isEmpty(request.getReceiverAccount()) ? accountList.get(i) : request.getReceiverAccount();
                UserChatMessageBo bo = userChatMessageConverter.toBo(messageList.get(i), accountList.get(i), receiver);
                messageBoList.add(bo);
            }

            FetchUserMessageResponse fetchUserMessageResponse = new FetchUserMessageResponse();
            fetchUserMessageResponse.messageList = messageBoList;
            return BaseResponse.getResponseEntitySuccess(fetchUserMessageResponse);
        }
    }
}
