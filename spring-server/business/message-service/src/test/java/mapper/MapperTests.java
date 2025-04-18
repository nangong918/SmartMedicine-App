package mapper;

import com.czy.api.constant.MessageTypeEnum;
import com.czy.api.domain.Do.message.UserChatMessageDo;
import com.czy.api.domain.bo.message.UserChatMessageBo;
import com.czy.message.MessageServiceApplication;
import com.czy.message.mapper.mysql.UserChatMessageMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * @author 13225
 * @date 2025/3/29 10:08
 */
@Slf4j
@SpringBootTest(classes = MessageServiceApplication.class)
@TestPropertySource("classpath:application.yml")
public class MapperTests {

    @Test
    public void test() {
        System.out.println("hello world");
    }

    @Autowired
    private UserChatMessageMapper mapper;

    // getChatList
    private List<UserChatMessageDo> getChatList() {
        List<UserChatMessageDo> list = new ArrayList<>();
        final Long senderId = 26L;
        final Long receiverId = 27L;
        final String msgContent = "hello world";
        for (int i = 0; i < 10; i++) {
            UserChatMessageDo userChatMessageDo = new UserChatMessageDo();
            // 奇数索引使用 senderId 和 receiverId
            if (i % 2 == 0) {
                userChatMessageDo.setSenderId(senderId); // 偶数位置，senderId
                userChatMessageDo.setReceiverId(receiverId); // receiverId
            }
            else {
                userChatMessageDo.setSenderId(receiverId); // 奇数位置，senderId
                userChatMessageDo.setReceiverId(senderId); // receiverId
            }
            userChatMessageDo.setMsgType(MessageTypeEnum.text.code);
            userChatMessageDo.setMsgContent(msgContent + i);
            // 设置时间戳，当前时间 + 随机范围内的毫秒值
            long randomOffset = (long) (Math.random() * 20000) - 10000; // 生成 -10000 到 +10000 毫秒之间的随机偏移
            userChatMessageDo.setTimestamp(System.currentTimeMillis() + randomOffset);
            list.add(userChatMessageDo);
        }
        return list;
    }

    // batchInsert
    @Test
    public void testBatchInsert() {
        List<UserChatMessageDo> list = getChatList();
        if (CollectionUtils.isEmpty(list)){
            System.err.println("list is empty");
            return;
        }
        mapper.batchInsert(list);
    }

    private final static long testTimestamp = 1741790282401L;

    // selectMessagesBefore
    @Test
    public void testSelectMessagesBefore() {
        List<UserChatMessageBo> list = mapper.selectMessagesBefore(26L, 27L, testTimestamp, 10);
        for (UserChatMessageBo userChatMessageBo : list) {
            System.out.println(userChatMessageBo.toJsonString());
        }
    }

    // selectMessagesAfter
    @Test
    public void testSelectMessagesAfter() {
        List<UserChatMessageBo> list = mapper.selectMessagesAfter(26L, 27L, testTimestamp, 10);
        for (UserChatMessageBo userChatMessageBo : list) {
            System.out.println(userChatMessageBo.toJsonString());
        }
    }
}
