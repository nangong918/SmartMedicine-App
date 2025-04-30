package com.czy.message.mapper.mongo;

import com.czy.api.domain.Do.message.UserChatMessageDo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * @author 13225
 * @date 2025/4/16 15:09
 */

@RequiredArgsConstructor
@Repository
public class UserChatMessageMongoMapper {

    private final MongoTemplate mongoTemplate;

    @PostConstruct
    public void init() {
        createIndexes();
    }

    // 创建复合索引
    private void createIndexes() {
//        IndexOperations indexOps = mongoTemplate.indexOps(UserChatMessageDo.class);
//        indexOps.ensureIndex(new Index().on("senderId", Sort.Direction.ASC)
//                .on("receiverId", Sort.Direction.ASC)
//                .on("timestamp", Sort.Direction.ASC));
        mongoTemplate.indexOps(UserChatMessageDo.class).ensureIndex(new Index().on("senderId", Sort.Direction.ASC));
        mongoTemplate.indexOps(UserChatMessageDo.class).ensureIndex(new Index().on("receiverId", Sort.Direction.ASC));
        mongoTemplate.indexOps(UserChatMessageDo.class).ensureIndex(new Index().on("timestamp", Sort.Direction.ASC));
        mongoTemplate.indexOps(UserChatMessageDo.class).ensureIndex(new Index()
                .on("senderId", Sort.Direction.ASC)
                .on("receiverId", Sort.Direction.ASC)
                .on("timestamp", Sort.Direction.ASC));
    }

    // 保存单个消息
    public void saveMessage(UserChatMessageDo chatMessage) {
        mongoTemplate.save(chatMessage);
    }

    // 保存全部消息
    public void saveAllMessage(List<UserChatMessageDo> chatMessages) {
        mongoTemplate.insert(chatMessages, UserChatMessageDo.class);
    }

    // 获取特定用户之间的消息
    public List<UserChatMessageDo> findMessages(Long senderId, Long receiverId) {
        return mongoTemplate.find(
                Query.query(
                        Criteria.where("senderId").is(senderId)
                        .and("receiverId").is(receiverId))
                .with(Sort.by("timestamp")), UserChatMessageDo.class);
    }

    /**
     * 获取特定用户之间的消息 + 分页
     * @param senderId
     * @param receiverId
     * @param page          页码；从0开始
     * @param size          每页记录数
     * @return
     */
    public List<UserChatMessageDo> findMessagesPaging(Long senderId, Long receiverId, int page, int size) {
        Query query = Query.query(
                        Criteria.where("senderId").is(senderId)
                                .and("receiverId").is(receiverId))
                .with(Sort.by("timestamp"))
                .skip((long) page * size)  // 跳过前面页的记录
                .limit(size);               // 限制返回的记录数

        return mongoTemplate.find(query, UserChatMessageDo.class);
    }

    // 获取特定用户之间某个时间戳之前的n条消息
    public List<UserChatMessageDo> findMessagesBeforeTimestamp(Long senderId, Long receiverId, Long timestamp, int n) {
        Query query = Query.query(
                Criteria.where("senderId").is(senderId)
                        .and("receiverId").is(receiverId)
                        .and("timestamp").lt(timestamp))
                .with(Sort.by(Sort.Direction.DESC, "timestamp"))
                .limit(n);
        return mongoTemplate.find(query, UserChatMessageDo.class);
    }

    // 获取特定用户之间某个时间戳之后的n条消息（包括当前时间戳）
    public List<UserChatMessageDo> findMessagesAfterTimestamp(Long senderId, Long receiverId, Long timestamp, int n) {
        Query query = Query.query(
                Criteria.where("senderId").is(senderId)
                        .and("receiverId").is(receiverId)
                        .and("timestamp").gte(timestamp))
                .with(Sort.by(Sort.Direction.ASC, "timestamp"))
                .limit(n);
        return mongoTemplate.find(query, UserChatMessageDo.class);
    }

    // 根据senderId，receiverId，timestamp更新消息
    public void updateMessage(Long senderId, Long receiverId, Long timestamp, String newMessage, String newType) {
        Query query = Query.query(Criteria.where("senderId").is(senderId)
                .and("receiverId").is(receiverId)
                .and("timestamp").is(timestamp));

        Update update = new Update();
        update.set("msgContent", newMessage);
        update.set("msgType", newType);  // 如果需要更新消息类型

        mongoTemplate.updateFirst(query, update, UserChatMessageDo.class);
    }

    // 删除两个好友之间的全部消息
    public void deleteAllMessages(Long senderId, Long receiverId) {
        // 构建查询条件，确保无论 senderId 和 receiverId 的位置如何都能匹配
        Query query = new Query(new Criteria().orOperator(
                Criteria.where("senderId").is(senderId).and("receiverId").is(receiverId),
                Criteria.where("senderId").is(receiverId).and("receiverId").is(senderId)
        ));

        // 执行删除操作
        mongoTemplate.remove(query, UserChatMessageDo.class);
    }
}
