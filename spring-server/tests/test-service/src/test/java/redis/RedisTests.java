package redis;

import com.api.mapper.common.redis.FileRedisMapper;
import com.api.mapper.medicine.redis.RegisterAppointmentRedisMapper;
import com.czy.api.domain.ao.medicine.AppointmentDoctorOrderListAo;
import com.czy.api.domain.ao.medicine.HospitalAo;
import com.czy.api.domain.vo.medicine.AppointmentDoctorOrderListVo;
import com.czy.api.domain.vo.medicine.DoctorVo;
import com.czy.test.TestApplication;
import com.czy.test.domain.Do.TestDo;
import com.czy.test.mapper.TestRedisMapper;
import domain.FileResAo;
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
    private RegisterAppointmentRedisMapper registerAppointmentRedisMapper;

    @Test
    public void testRegisterAppointmentRedisMapper(){
        AppointmentDoctorOrderListAo ao = new AppointmentDoctorOrderListAo();
        ao.setDoctorMerchantId(1L);
        ao.setOrderId(1L);
        AppointmentDoctorOrderListVo vo = new AppointmentDoctorOrderListVo();
        vo.setDoctorVo(new DoctorVo());
        vo.setHospitalAo(new HospitalAo());
        vo.setCost("10");
        vo.setBeginDate("2021-01-01");
        vo.setEndDate("2021-01-01");
        vo.setApproveDate("2021-01-01");
        vo.setMerchantStatus(1);
        ao.setListVo(vo);

        registerAppointmentRedisMapper.saveAppointmentDoctorOrderListAo(1L, 1L, 1L, ao);
        AppointmentDoctorOrderListAo ao1 = registerAppointmentRedisMapper.getAppointmentDoctorOrderListAo(1L, 1L, 1L);
        log.info("{}", ao1);
    }

}
