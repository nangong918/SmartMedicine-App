package imports;

import com.api.mapper.medicine.mybatis.DoctorMapper;
import com.api.mapper.medicine.mybatis.HospitalMapper;
import com.api.mapper.user.mybatis.user.UserMapper;
import com.czy.api.domain.Do.medicine.DoctorMerchantAppointmentDo;
import com.czy.imports.ImportsApplication;
import com.czy.imports.service.ImportDoctorMerchantAppointmentService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

/**
 * @author 13225
 * @date 2025/8/19 16:42
 */

@Slf4j
@SpringBootTest(classes = ImportsApplication.class)
@TestPropertySource("classpath:application.yml")
public class RegisterAppointmentImport {

    @Autowired
    private DoctorMapper doctorMapper;
    @Autowired
    private HospitalMapper hospitalMapper;
    @Autowired
    private UserMapper userMapper;

    @Test
    public void test(){
        System.out.println(doctorMapper);
    }

    @Autowired
    private ImportDoctorMerchantAppointmentService importDoctorMerchantAppointmentService;

    /**
     * 脚本：创建医生和医院
     */
    @Test
    public void createDoctorsHospital(){
        importDoctorMerchantAppointmentService.createDoctorsHospital();
    }


    @Test
    public void generatorDoctorsMerchantAppointmentDos(){
        List<DoctorMerchantAppointmentDo> dos = importDoctorMerchantAppointmentService.generatorDoctorMerchantAppointmentDos(10);

        dos.forEach(d -> {
            System.out.println("item: " + d.toJsonString());
        });
    }

    /**
     * 脚本：创建可预约商户信息
     */
    @Test
    public void createDoctorsMerchantAppointmentDos(){
        importDoctorMerchantAppointmentService.createDoctorMerchantAppointmentDos(50);
    }

}
