import com.api.mapper.medicine.mybatis.DoctorMerchantAppointmentMapper;
import com.api.mapper.medicine.mybatis.bo.DoctorMerchantBoMapper;
import com.api.mapper.medicine.redis.RegisterAppointmentRedisMapper;
import com.czy.api.constant.medicine.DepartmentEnum;
import com.czy.api.domain.Do.medicine.DoctorMerchantAppointmentDo;
import com.czy.api.domain.ao.LocationAo;
import com.czy.api.domain.ao.medicine.AppointmentDoctorOrderListAo;
import com.czy.api.domain.ao.medicine.HospitalAo;
import com.czy.api.domain.ao.medicine.RegisterAppointmentSelectAo;
import com.czy.api.domain.bo.medicine.RegisterAppointmentDoctorCardBo;
import com.czy.api.domain.vo.medicine.AppointmentDoctorOrderListVo;
import com.czy.api.domain.vo.medicine.DoctorVo;
import com.czy.api.domain.vo.medicine.RegisterAppointmentDataVo;
import com.czy.api.domain.vo.medicine.RegisterAppointmentPageVo;
import com.czy.medicine.MedicineServiceApplication;
import com.czy.medicine.service.RegisterAppointmentService;
import date.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author 13225
 * @date 2025/8/20 13:42
 */
@Slf4j
@SpringBootTest(classes = MedicineServiceApplication.class)
@TestPropertySource("classpath:application.yml")
public class RegisterAppointmentTests {

    @Autowired
    private DoctorMerchantAppointmentMapper doctorMerchantAppointmentMapper;
    @Autowired
    private DoctorMerchantBoMapper doctorMerchantBoMapper;

    private RegisterAppointmentSelectAo getRegisterAppointmentSelectAo(){
        RegisterAppointmentSelectAo ao = new RegisterAppointmentSelectAo();
        LocationAo locationAo = new LocationAo("广东省", "深圳市", "南山区");
        LocalDateTime now = LocalDateTime.now();
        ao.setRegisterLocation(locationAo);
        String nowTimeStr = DateUtils.yyyyMMddHHmmssToString(now);
        ao.setRegisterTime(nowTimeStr);
        ao.setRegisterDepartmentCode(DepartmentEnum.INTERNAL_MEDICINE.getCode());
        ao.setRegisterSubjectCode(DepartmentEnum.INTERNAL_MEDICINE.getSubjectEnums()[0].getCode());
        return ao;
    }

    /**
     * SELECT *
     * FROM doctor_merchant_appointment AS dma
     * LEFT JOIN hospital AS hos ON dma.hospital_id = hos.id
     * WHERE
     *     hos.province = '广东省'  -- 地区非空
     *     AND hos.city = '深圳市'  -- 如果 location.city 非空
     *     AND hos.region = '南山区' -- 如果 location.region 非空
     *     AND dma.department_id = 1  -- DepartmentEnum.INTERNAL_MEDICINE.getCode()
     *     AND dma.subject_id = 1     -- DepartmentEnum.INTERNAL_MEDICINE.getSubjectEnums()[0].getCode()
     *     AND (
     *         DATE(dma.begin_date) = DATE('2023-08-20') OR   -- 如果 date 非空
     *         DATE(dma.end_date) = DATE('2023-08-20') OR
     *         (dma.begin_date <= '2023-08-20' AND dma.end_date >= '2023-08-20')
     *     );
     */
    @Test
    public void doctorMerchantAppointmentTest(){
        RegisterAppointmentSelectAo ao = getRegisterAppointmentSelectAo();
        LocalDateTime now = LocalDateTime.now();

        for (int i = 0; i < 4; i++){
            LocalDateTime newDate = now.plusDays(i);
            List<DoctorMerchantAppointmentDo> dos = doctorMerchantAppointmentMapper.getDosByParam(
                    ao.getRegisterLocation(),
                    newDate,
                    ao.registerDepartmentCode,
                    ao.registerSubjectCode
            );
            if (CollectionUtils.isEmpty(dos)){
                log.info("[{}] 没有查询到数据", i);
            }
            else {
                log.info("[{}] 查询到数据", i);
                for (DoctorMerchantAppointmentDo d : dos) {
                    log.info(d.toJsonString());
                }
            }
        }

    }

    @Test
    public void doctorMerchantBoTest(){
        RegisterAppointmentSelectAo ao = getRegisterAppointmentSelectAo();
        LocalDateTime now = LocalDateTime.now();
        List<DoctorMerchantAppointmentDo> dos = doctorMerchantAppointmentMapper.getDosByParam(
                ao.getRegisterLocation(),
                now,
                ao.registerDepartmentCode,
                ao.registerSubjectCode
        );

        List<RegisterAppointmentDoctorCardBo> boList = doctorMerchantBoMapper.getDoctorCardBosByDoctorMerchantDos(dos);
        for (RegisterAppointmentDoctorCardBo bo : boList){
            log.info("bo: {}", bo);
        }
    }

    @Autowired
    private RegisterAppointmentService registerAppointmentService;

    @Test
    public void getListRegisterAppointment(){
        RegisterAppointmentSelectAo ao = getRegisterAppointmentSelectAo();

        RegisterAppointmentPageVo pageVo = registerAppointmentService.getPage(
                ao
        );

        log.info("pageVo: {}", pageVo.toJsonString());
    }

    @Test
    public void getAllDateRegisterAppointment(){
        RegisterAppointmentSelectAo ao = getRegisterAppointmentSelectAo();

        List<RegisterAppointmentDataVo> dataVos = registerAppointmentService.getDataVoList(
                ao
        );

        log.info("dataVos: {}", dataVos);
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
