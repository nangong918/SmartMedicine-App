package message;

import com.czy.api.api.message.ChatSearchService;
import com.czy.api.constant.MessageTypeEnum;
import com.czy.api.converter.mongoEs.UserChatMessageEsConverter;
import com.czy.api.domain.Do.message.UserChatMessageDo;
import com.czy.api.domain.Do.message.UserChatMessageEsDo;
import com.czy.api.domain.bo.message.UserChatMessageBo;
import com.czy.api.domain.dto.base.BaseResponse;
import com.czy.api.domain.dto.http.request.FetchUserMessageResponse;
import com.czy.message.MessageServiceApplication;
import com.czy.message.mapper.mysql.UserChatMessageMapper;
import com.czy.message.service.transactional.MessageStorageService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.TestPropertySource;

import reactor.core.publisher.Mono;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * @author 13225
 * @date 2025/3/29 10:08
 */
@Slf4j
@SpringBootTest(classes = MessageServiceApplication.class)
@TestPropertySource("classpath:application.yml")
public class MessageTests {

    @Test
    public void test() {
        System.out.println("hello world");
    }

    /**
     * 待测试：
     *  1.聊天消息存储在Mongo和ES；
     *  2.MongoDB的分页，MySQL的分页，ES的分页。
     *  3.ES的两个功能，并实现Controller + 分页
     */

    @Autowired
    private MessageStorageService messageStorageService;

    // Mysql
    @Autowired
    private UserChatMessageMapper userChatMessageMapper;

    private List<UserChatMessageDo> getUserTestChatMessage(){
        List<UserChatMessageDo> userChatMessageDos = new ArrayList<>();
        final long userId1 = 26L;
        final long userId2 = 27L;
        for (int i = 0; i < 20; i++){
            UserChatMessageDo userChatMessageDo = new UserChatMessageDo();
            // 奇偶性设置userId
            if (i % 2 == 0){
                userChatMessageDo.setSenderId(userId1);
                userChatMessageDo.setReceiverId(userId2);
            }
            else {
                userChatMessageDo.setSenderId(userId2);
                userChatMessageDo.setReceiverId(userId1);
            }
            userChatMessageDo.setMsgContent("两个用户聊天存储到数据库测试：" + i);
            userChatMessageDo.setMsgType(MessageTypeEnum.text.code);
            userChatMessageDo.setTimestamp(System.currentTimeMillis());
            userChatMessageDos.add(userChatMessageDo);
        }
        return userChatMessageDos;
    }

    // 聊天消息存储在Mongo和ES
    @Test
    public void saveMessageToMongoAndEs() {
        List<UserChatMessageDo> list = getUserTestChatMessage();
        // 存入 Es + Mongo
        messageStorageService.storeMessagesToDatabase(list);
        // 存入 mysql
        userChatMessageMapper.batchInsert(list);
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

    // MongoDB分页查询
    @Autowired
    private com.czy.message.mapper.mongo.UserChatMessageMongoMapper userChatMessageMongoMapper;
    @Test
    public void searchMongoUserChatMessage() {
        final long userId1 = 26L;
        final long userId2 = 27L;
        List<UserChatMessageDo> list = userChatMessageMongoMapper.findMessagesPaging(userId1, userId2, 0, 10);
        for (UserChatMessageDo userChatMessageDo : list) {
            System.out.println(userChatMessageDo);
        }
    }

    // MySQL分页查询
    @Test
    public void searchMysqlUserChatMessage() {
        final long userId1 = 26L;
        final long userId2 = 27L;
        List<UserChatMessageBo> list = userChatMessageMapper.selectMessagesBefore(userId1, userId2, System.currentTimeMillis(), 10);
        for (UserChatMessageBo userChatMessageDo : list) {
            System.out.println(userChatMessageDo);
        }
    }

    // Elastic Search分页查询
    @Autowired
    private com.czy.message.mapper.es.UserChatMessageEsMapper userChatMessageEsMapper;
    @Test
    public void searchEsUserChatMessage() {
        final long userId1 = 26L;
        final long userId2 = 27L;
        Pageable pageable = getPageable(10);
        long start = System.currentTimeMillis();
        Page<UserChatMessageEsDo> results = userChatMessageEsMapper.findBySenderIdAndMsgContentContaining(userId1, "数据库", pageable);
        System.out.println("查询耗时：" + (System.currentTimeMillis() - start));
        List<UserChatMessageEsDo> list = results.getContent();
        for (UserChatMessageEsDo userChatMessageEsDo : list) {
            System.out.println(userChatMessageEsDo);
        }
    }

    // 三个数据库查询速度测试
    @Test
    public void searchControllerUserChatMessage() {
        final long userId1 = 26L;
        final long userId2 = 27L;

        // mongo
        for (int i = 0; i < 10; i++){
            long startMongo = System.currentTimeMillis();
            List<UserChatMessageDo> mongoList = userChatMessageMongoMapper.findMessagesPaging(userId1, userId2, 0, 10);
            // mongo查询耗时：3ms
            System.out.println("mongo查询耗时：" + (System.currentTimeMillis() - startMongo));

            long startMysql = System.currentTimeMillis();
            List<UserChatMessageBo> mysqlList = userChatMessageMapper.selectMessagesBefore(userId1, userId2, System.currentTimeMillis(), 10);
            // mysql查询耗时：5ms
            System.out.println("mysql查询耗时：" + (System.currentTimeMillis() - startMysql));

            // es查询耗时：19
            Pageable pageable = getPageable(10);
            long startEs = System.currentTimeMillis();
            Page<UserChatMessageEsDo> esList = userChatMessageEsMapper.findBySenderIdAndReceiverIdAndMsgContentContaining(userId1, userId2, "数据库", pageable);
            System.out.println("es查询耗时：" + (System.currentTimeMillis() - startEs));
        }
        /**
         * mysql查询耗时：4
         * es查询耗时：10
         * mongo查询耗时：2
         */
    }

    // 分别压测三个数据库
    @Test
    public void searchControllerUserChatMessage2() {
        final long userId1 = 26L;
        final long userId2 = 27L;
        Pageable pageable = getPageable(10);
        // mysql
        long startMysql = System.currentTimeMillis();
        for (int i = 0; i < 100; i++){
            List<UserChatMessageBo> mysqlList = userChatMessageMapper.selectMessagesBefore(userId1, userId2, System.currentTimeMillis(), 10);
        }
        // mysql查询耗时：402ms
        System.out.println("mysql查询耗时：" + (System.currentTimeMillis() - startMysql));

        // mongo
        long startMongo = System.currentTimeMillis();
        for (int i = 0; i < 100; i++){
            List<UserChatMessageDo> mongoList = userChatMessageMongoMapper.findMessagesPaging(userId1, userId2, 0, 10);
        }
        // 2233ms
        System.out.println("mongo查询耗时：" + (System.currentTimeMillis() - startMongo));

        // elastic search
        long startEs = System.currentTimeMillis();
        for (int i = 0; i < 100; i++){
            Page<UserChatMessageEsDo> esList = userChatMessageEsMapper.findBySenderIdAndReceiverIdAndMsgContentContaining(userId1, userId2, "数据库", pageable);
        }
        // es查询耗时：1526
        System.out.println("es查询耗时：" + (System.currentTimeMillis() - startEs));
        /**
         * mysql查询耗时：253
         * mongo查询耗时：2238
         * es查询耗时：1056
         *
         * mongo可能启动慢
         */
    }

    @Autowired
    ChatSearchService chatSearchService;
    @Autowired
    UserChatMessageEsConverter userChatMessageEsConverter;


    // Controller的功能
    @Test
    public void getUserKeyChatHistoryWithReceiver() {
        final long userId1 = 26L;
        final long userId2 = 27L;
        List<UserChatMessageEsDo> messageEsList = chatSearchService.searchUserChatMessageLimit(userId1, userId2, "数据库", 20);
        List<UserChatMessageDo> messageList = userChatMessageEsConverter.esListToMongoList(messageEsList);
        if (messageList.isEmpty()){
            return;
        }
        messageList.forEach(item-> System.out.println("item:" + item));
    }
}
