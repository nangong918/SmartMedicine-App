package imports;

import com.api.mapper.medicine.DoctorMapper;
import com.czy.imports.ImportsApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

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

    @Test
    public void test(){
        System.out.println(doctorMapper);
    }

}
