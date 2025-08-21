package redis;

import com.api.mapper.common.redis.FileRedisMapper;
import com.czy.test.TestApplication;
import domain.FileResAo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author 13225
 * @date 2025/8/21 14:06
 */
@Slf4j
@SpringBootTest(classes = TestApplication.class)
public class RedisTests {

    @Autowired
    private FileRedisMapper fileRedisMapper;

    @Test
    public void fileRedisMapperTest() {
        FileResAo resAo = new FileResAo();
        String redisKey = "fileRedisKey:";
        resAo.setFileId(1L);
        resAo.setFileUrl("fileUrl");
        System.out.println(fileRedisMapper.insertFileResAo(redisKey, resAo, 5L));
        FileResAo resAo1 = fileRedisMapper.getFileResAo(redisKey);
        log.info("resAo1: {}", resAo1);
    }

}
