package kafka;


import com.czy.test.TestApplication;
import com.czy.test.consumer.Demo01AConsumer;
import com.czy.test.producer.Demo01Producer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.concurrent.ExecutionException;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestApplication.class)
public class Demo01ProducerTest {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private Demo01Producer demo01Producer;

    @Autowired
    private Demo01AConsumer demo01AConsumer;

    @Test
    public void testSyncSend() throws ExecutionException, InterruptedException {
        int id = (int) (System.currentTimeMillis() / 1000);
        // 发送消息并获取结果
        SendResult<Object, Object> result = demo01Producer.syncSend(id);
        logger.info("[testSyncSend][发送编号：[{}] 发送结果：[{}]]", id, result);

        Thread.sleep(4000L);
    }

    @Test
    public void testASyncSend() throws InterruptedException {
        int id = (int) (System.currentTimeMillis() / 1000);
        demo01Producer.asyncSend(id).addCallback(new ListenableFutureCallback<SendResult<Object, Object>>() {

            @Override
            public void onFailure(Throwable e) {
                logger.info("[testASyncSend][发送编号：[{}] 发送异常]]", id, e);
            }

            @Override
            public void onSuccess(SendResult<Object, Object> result) {
                logger.info("[testASyncSend][发送编号：[{}] 发送成功，结果为：[{}]]", id, result);
            }

        });

        // 阻塞等待，保证消费
        Thread.sleep(4000L);
    }
//
//    @Test
//    public void testOnewaySend() throws InterruptedException {
//        int id = (int) (System.currentTimeMillis() / 1000);
//        producer.onewaySend(id);
//        logger.info("[testOnewaySend][发送编号：[{}] 发送完成]", id);
//
//        // 阻塞等待，保证消费
//        new CountDownLatch(1).await();
//    }

//    @Test
//    public void testSyncSendMore() throws ExecutionException, InterruptedException {
//        for (int i = 0; i < 1000; i++) {
//            int id = (int) (System.currentTimeMillis() / 1000);
//            SendResult result = producer.syncSend(id);
//            logger.info("[testSyncSend][发送编号：[{}] 发送结果：[{}]]", id, result);
//            Thread.sleep(10);
//        }
//
//        // 阻塞等待，保证消费
//        new CountDownLatch(1).await();
//    }
//
//    @Test
//    public void block() throws InterruptedException {
//        // 阻塞等待，保证消费
//        new CountDownLatch(1).await();
//    }

}
