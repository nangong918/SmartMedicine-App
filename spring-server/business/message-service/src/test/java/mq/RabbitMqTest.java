package mq;

import com.czy.api.constant.netty.MessageTypeTranslator;
import com.czy.api.domain.dto.base.BaseResponseData;
import com.czy.api.domain.dto.http.request.SendTextDataRequest;
import com.czy.api.domain.dto.socket.response.UserTextDataResponse;
import com.czy.api.domain.entity.event.Message;
import com.czy.message.MessageServiceApplication;
import com.czy.message.mq.sender.RabbitMqSender;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@Slf4j
@SpringBootTest(classes = MessageServiceApplication.class)
public class RabbitMqTest {

    @Autowired
    private RabbitMqSender rabbitMqSender;

    // 压测handler
    @Test
    public void test() throws Exception {
        for (int i = 0; i < 100; i++){

            SendTextDataRequest request = new SendTextDataRequest();
            request.senderId = 1L;
            request.receiverId = 2L;
            request.type = "message";
            request.setContent("message" + i);
            request.timestamp = String.valueOf(System.currentTimeMillis());
            UserTextDataResponse response = new UserTextDataResponse();
            response.initResponseByRequest(request);
            response.setContent(request.getContent());

            Message message = change(response);

//            message = new Message();
//            message.setSenderId("1");
//            message.setReceiverId("2");
//            message.setType("message");
//
//            Map<String, String> data = new HashMap<>();
//            data.put("code", "success");
//            data.put("message", "成功");
//            data.put("content", "message" + i);
//            message.setData(data);

            Message message1 = new Message();
            message1.setSenderId(message.getSenderId());
            message1.setReceiverId(message.getReceiverId());
            message1.setType(message.getType());
            log.info("转换类型的map：{}, map的大小：{}", message.getData(), message.getData().size());
//            Map<String, String> data = new HashMap<>();
//            for (Map.Entry<String, String> entry : message.getData().entrySet()){
//                data.put(entry.getKey(), entry.getValue());
//            }
            message.nonNull();
            if (message.getData() != null){
                message1.setData(message.getData());
            }
            message1.setTimestamp(message.getTimestamp());

            rabbitMqSender.push(message1);
            log.info("发送消息：{}", message1);
        }
        log.info("发送完毕, 等待接收者接收消息");
        Thread.sleep(1000L * 5);
    }

    @Test
    public void baseDataResponseTest() throws Exception {
        for (int i = 0; i < 100; i++){
            SendTextDataRequest request = new SendTextDataRequest();
            request.senderId = 1L;
            request.receiverId = 2L;
            request.type = "message";
            request.setContent("message" + i);
            request.timestamp = String.valueOf(System.currentTimeMillis());

            UserTextDataResponse response = new UserTextDataResponse();
            response.initResponseByRequest(request);
            response.setContent(request.getContent());

            Message message = change(response);
            message.setType("message");
            message.nonNull();


            Message message1 = new Message(message);
            rabbitMqSender.push(message1);
            log.info("发送消息：{}", message1);

        }
        log.info("发送完毕, 等待接收者接收消息");
        Thread.sleep(1000L * 5);
    }

    public static  <T extends BaseResponseData> Message change(T t){
        Message message = t.getMessageByResponse();
        message.setType(MessageTypeTranslator.translateClean(t.getType()));
        return message;
    }

    public static void main(String[] args) {
        SendTextDataRequest request = new SendTextDataRequest();
        request.senderId = 1L;
        request.receiverId = 2L;
        request.type = "message";
        request.setContent("message" + 90);
        request.timestamp = String.valueOf(System.currentTimeMillis());
        UserTextDataResponse response = new UserTextDataResponse();
        response.initResponseByRequest(request);
        response.setContent(request.getContent());

        Message message = change(response);

        log.info("message：{}", message);
    }

}
