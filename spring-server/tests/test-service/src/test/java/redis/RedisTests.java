package redis;

import com.api.mapper.medicine.redis.AppointmentDoctorOrderRedisMapper;
import com.czy.test.TestApplication;
import com.czy.test.domain.Do.TestDo;
import com.czy.test.mapper.TestRedisMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

/**
 * @author 13225
 * @date 2025/8/21 14:06
 */
@Slf4j
@SpringBootTest(classes = TestApplication.class)
public class RedisTests {

    @Autowired
    private TestRedisMapper testRedisMapper;

    @Test
    public void testJpaRedisTest(){
        TestDo testDo = new TestDo();
        testDo.setId(1L);
        testDo.setName("张三");
        testRedisMapper.save(testDo);
        // 查找对象
        Optional<TestDo> foundTestDo = testRedisMapper.findById(String.valueOf(1L));
        System.out.println("foundTestDo.get() = " + foundTestDo.get());
    }

    @Autowired
    private AppointmentDoctorOrderRedisMapper registerAppointmentRedisMapper;

}
