package imports;

import com.api.mapper.medicine.DoctorMapper;
import com.api.mapper.medicine.HospitalMapper;
import com.czy.api.domain.Do.medicine.DoctorDo;
import com.czy.api.domain.Do.medicine.HospitalDo;
import com.czy.imports.ImportsApplication;
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

    @Test
    public void test(){
        System.out.println(doctorMapper);
    }

    @Test
    public void createDoctorsHospital(){
        // 头像id
        Long avatarId = 1953385170029322240L;
        DoctorDo doctorDo = new DoctorDo();
        doctorDo.setName("张医生");
        doctorDo.setTitle("主治医师");
        doctorDo.setAvatarFileId(avatarId);
        doctorDo.setId(1234567890L);

        doctorMapper.insert(doctorDo);
    }

    private List<DoctorDo> createDoctors(){
        return null;
    }

    private List<HospitalDo> createHospitals(){
        HospitalDo hospitalDo = new HospitalDo();

        return null;
    }

}
