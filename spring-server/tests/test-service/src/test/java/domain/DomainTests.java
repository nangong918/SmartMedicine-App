package domain;

import com.czy.api.constant.UserOrderStatusEnum;
import com.czy.api.constant.medicine.AppointmentMerchantStatusEnum;
import com.czy.api.converter.domain.medicine.RegisterAppointmentDoctorCardConverter;
import com.czy.api.domain.ao.LocationAo;
import com.czy.api.domain.ao.medicine.HospitalAo;
import com.czy.api.domain.vo.medicine.AppointmentDoctorOrderListVo;
import com.czy.api.domain.vo.medicine.DoctorVo;
import com.czy.api.domain.vo.medicine.HospitalVo;
import com.czy.api.domain.vo.medicine.AppointmentDoctorMerchantCardVo;
import com.czy.test.TestApplication;
import date.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;

/**
 * @author 13225
 * @date 2025/8/21 15:42
 */
@Slf4j
@SpringBootTest(classes = TestApplication.class)
@TestPropertySource("classpath:application.yml")
public class DomainTests {

    @Autowired
    private RegisterAppointmentDoctorCardConverter registerAppointmentDoctorCardConverter;

    @Test
    public void appointmentCloneTest() throws CloneNotSupportedException {
        AppointmentDoctorMerchantCardVo vo = new AppointmentDoctorMerchantCardVo();

        vo.doctorVo = new DoctorVo();
        vo.doctorVo.doctorAvatarFileAo = new FileResAo();
        vo.doctorVo.doctorAvatarFileAo.fileId = 1L;
        vo.doctorVo.doctorAvatarFileAo.fileUrl = "https://www.baidu.com";
        vo.doctorVo.doctorAvatarFileAo.uploadUserId = 1L;
        vo.doctorVo.doctorName = "张三";
        vo.doctorVo.doctorTitle = "医生";
        vo.hospitalAo = new HospitalAo();
        vo.hospitalAo.hospitalVo = new HospitalVo();
        vo.hospitalAo.hospitalVo.name = "上海大学";
        vo.hospitalAo.hospitalVo.level = "三级甲等";
        vo.hospitalAo.locationAo = new LocationAo();
        vo.hospitalAo.locationAo.province = "上海";
        vo.hospitalAo.locationAo.city = "上海";
        vo.hospitalAo.locationAo.region = "浦东新区";
        vo.hospitalAo.longitude = 121.5;
        vo.hospitalAo.latitude = 31.0;
        vo.remainCount = 10;
        vo.cost = "10元";
        vo.beginDate = "2023-05-01";
        vo.endDate = "2023-05-31";

        String dateStr = DateUtils.yyyyMMddHHmmssToString(LocalDateTime.now());
        AppointmentDoctorOrderListVo listVo = registerAppointmentDoctorCardConverter.getAppointmentDoctorOrderListVo(vo,
                dateStr,
                AppointmentMerchantStatusEnum.NULL.getCode(),
                UserOrderStatusEnum.NULL.getCode()
        );
        log.info("listVo: {}", listVo);
    }
}
