import com.api.mapper.medicine.mybatis.DoctorMerchantAppointmentMapper;
import com.api.mapper.medicine.mybatis.bo.DoctorMerchantBoMapper;
import com.api.mapper.medicine.redis.AppointmentDoctorOrderRedisMapper;
import com.czy.api.constant.UserOrderStatusEnum;
import com.czy.api.constant.medicine.AppointmentSortTypeEnum;
import com.czy.api.constant.medicine.DepartmentEnum;
import com.czy.api.domain.Do.medicine.DoctorMerchantAppointmentDo;
import com.czy.api.domain.ao.LocationAo;
import com.czy.api.domain.ao.medicine.AppointmentDoctorOrderListAo;
import com.czy.api.domain.ao.medicine.HospitalAo;
import com.czy.api.domain.ao.medicine.AppointmentDoctorSelectAo;
import com.czy.api.domain.bo.medicine.AppointmentDoctorMerchantCardBo;
import com.czy.api.domain.vo.medicine.AppointmentDoctorOrderListVo;
import com.czy.api.domain.vo.medicine.DoctorVo;
import com.czy.api.domain.vo.medicine.AppointmentDoctorDataVo;
import com.czy.api.domain.vo.medicine.AppointmentDoctorPageVo;
import com.czy.medicine.MedicineServiceApplication;
import com.czy.medicine.service.AppointmentDoctorService;
import date.DateUtils;
import json.BaseBean;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

    private AppointmentDoctorSelectAo getRegisterAppointmentSelectAo(){
        AppointmentDoctorSelectAo ao = new AppointmentDoctorSelectAo();
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
        AppointmentDoctorSelectAo ao = getRegisterAppointmentSelectAo();
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
        AppointmentDoctorSelectAo ao = getRegisterAppointmentSelectAo();
        LocalDateTime now = LocalDateTime.now();
        List<DoctorMerchantAppointmentDo> dos = doctorMerchantAppointmentMapper.getDosByParam(
                ao.getRegisterLocation(),
                now,
                ao.registerDepartmentCode,
                ao.registerSubjectCode
        );

        List<AppointmentDoctorMerchantCardBo> boList = doctorMerchantBoMapper.getDoctorCardBosByDoctorMerchantDos(dos);
        for (AppointmentDoctorMerchantCardBo bo : boList){
            log.info("bo: {}", bo);
        }
    }

    @Autowired
    private AppointmentDoctorService registerAppointmentService;

    @Test
    public void getListRegisterAppointment(){
        AppointmentDoctorSelectAo ao = getRegisterAppointmentSelectAo();

        AppointmentDoctorPageVo pageVo = registerAppointmentService.getPage(
                ao
        );

        log.info("pageVo: {}", pageVo.toJsonString());
    }

    @Test
    public void getAllDateRegisterAppointment(){
        AppointmentDoctorSelectAo ao = getRegisterAppointmentSelectAo();

        List<AppointmentDoctorDataVo> dataVos = registerAppointmentService.getDataVoList(
                ao
        );

        log.info("dataVos: {}", dataVos);
    }

    @Autowired
    private AppointmentDoctorOrderRedisMapper registerAppointmentRedisMapper;

    @Test
    public void appointmentDoctorOrderListAoTests(){
        long userId = 1L;

        // 经纬度
        double userLongitude = 22.650815387;
        double userLatitude = 114.01569422;

        List<AppointmentDoctorOrderListAo> aos = getAppointmentRecordList();

        // 增加数据到redis
        long startTime = System.currentTimeMillis();
        boolean listSaveResult = registerAppointmentRedisMapper.saveAppointmentDoctorOrderListAo(
                userId, aos
        );
        log.info("list save result: {}, 时间花费: {}ms", listSaveResult, System.currentTimeMillis() - startTime);

        // 获取数据
        getDataInfo(userId, userLongitude, userLatitude);

        // 插入单条
        LocalDateTime now = LocalDateTime.now();
        AppointmentDoctorOrderListAo ao1 = new AppointmentDoctorOrderListAo();
        ao1.setDoctorMerchantId(4L);
        ao1.setOrderId(4L);
        AppointmentDoctorOrderListVo vo1 = new AppointmentDoctorOrderListVo();
        vo1.setDoctorVo(new DoctorVo());
        vo1.setHospitalAo(new HospitalAo());
        BigDecimal cost1 = new BigDecimal("40");
        vo1.setCost(cost1.toString());
        vo1.setBeginDate(DateUtils.yyyyMMddHHmmssToString(now.plusDays(4)));
        vo1.setEndDate(DateUtils.yyyyMMddHHmmssToString(now.plusDays(5)));
        vo1.setApproveDate(vo1.getBeginDate());
        vo1.setMerchantStatus(1);
        ao1.setListVo(vo1);

        startTime = System.currentTimeMillis();
        boolean singleInsertResult = registerAppointmentRedisMapper.saveSingleAppointmentDoctorOrderListAo(
                userId, ao1
        );
        log.info("single insert result: {},  时间花费: {}ms", singleInsertResult, System.currentTimeMillis() - startTime);
        getDataInfo(userId, userLongitude, userLatitude);

        // 更新某条
        startTime = System.currentTimeMillis();
        boolean updateResult = registerAppointmentRedisMapper.updateAppointmentDoctorOrderListAoStatus(
                userId,
                ao1.getOrderId(),
                UserOrderStatusEnum.WAITING_PAYMENT.getCode()
        );
        log.info("更新某条: {}, 耗时：{}", updateResult, System.currentTimeMillis() - startTime);
        getDataInfo(userId, userLongitude, userLatitude);

        // 删除某条
        startTime = System.currentTimeMillis();
        registerAppointmentRedisMapper.deleteSingleAppointmentDoctorOrderListAo(userId, aos.get(0));
        log.info("删除某条, 耗时：{}", System.currentTimeMillis() - startTime);
        getDataInfo(userId, userLongitude, userLatitude);

        // 全部删除
        startTime = System.currentTimeMillis();
        registerAppointmentRedisMapper.deleteAllAppointmentDoctorOrderListAo(userId);
        log.info("删除全部, 耗时：{}", System.currentTimeMillis() - startTime);
        getDataInfo(userId, userLongitude, userLatitude);
    }

    private void getDataInfo(long userId, double userLongitude, double userLatitude){
        long startTime = System.currentTimeMillis();
        List<AppointmentDoctorOrderListAo> timeAoList = registerAppointmentRedisMapper.getAppointmentRecordList(
                userId, AppointmentSortTypeEnum.TIME.getCode(), null, null
        );
        List<AppointmentDoctorOrderListAo> costAoList = registerAppointmentRedisMapper.getAppointmentRecordList(
                userId, AppointmentSortTypeEnum.COST.getCode(), null, null
        );
        List<AppointmentDoctorOrderListAo> distanceAoList = registerAppointmentRedisMapper.getAppointmentRecordList(
                userId, AppointmentSortTypeEnum.DISTANCE.getCode(), userLongitude, userLatitude
        );
        ValidDataList time = new ValidDataList();
        time.validDataList = obtainValidData(timeAoList, userLongitude, userLatitude);
        ValidDataList cost = new ValidDataList();
        cost.validDataList = obtainValidData(costAoList, userLongitude, userLatitude);
        ValidDataList distance = new ValidDataList();
        distance.validDataList = obtainValidData(distanceAoList, userLongitude, userLatitude);
        log.info("[结果检查][时间花费: {}]\n[TIME: {}]\n[COST: {}]\n[DISTANCE: {}]",
                System.currentTimeMillis() - startTime,
                time,
                cost,
                distance
        );
    }

    @Data
    public static class ValidDataList implements BaseBean, Serializable {
        public List<ValidData> validDataList = new ArrayList<>();
    }

    @Data
    public static class ValidData implements BaseBean, Serializable {
        public BigDecimal cost;
        public LocalDateTime beginDate;
        public Integer customerStatus;
        public Double distance;
    }

    private List<ValidData> obtainValidData(List<AppointmentDoctorOrderListAo> list, double userLongitude, double userLatitude){
        if (CollectionUtils.isEmpty(list)){
            return new ArrayList<>();
        }
        List<ValidData> validDataList = new ArrayList<>();
        for (AppointmentDoctorOrderListAo ao : list){
            ValidData validData = new ValidData();
            validData.cost = ao.getCost();
            validData.beginDate = ao.getBeginDate();
            validData.customerStatus = ao.getListVo().getCustomerStatus();
            validData.distance = ao.getDistance(userLongitude, userLatitude);
            validDataList.add(validData);
        }
        return validDataList;
    }

    private List<AppointmentDoctorOrderListAo> getAppointmentRecordList() {
        LocalDateTime now = LocalDateTime.now();
        AppointmentDoctorOrderListAo ao1 = new AppointmentDoctorOrderListAo();
        ao1.setDoctorMerchantId(1L);
        ao1.setOrderId(1L);
        AppointmentDoctorOrderListVo vo1 = new AppointmentDoctorOrderListVo();
        vo1.setDoctorVo(new DoctorVo());
        vo1.setHospitalAo(new HospitalAo());
        BigDecimal cost1 = new BigDecimal("10");
        vo1.setCost(cost1.toString());
        vo1.setBeginDate(DateUtils.yyyyMMddHHmmssToString(now));
        vo1.setEndDate(DateUtils.yyyyMMddHHmmssToString(now.plusDays(1)));
        vo1.setApproveDate(vo1.getBeginDate());
        vo1.setMerchantStatus(1);
        ao1.setListVo(vo1);

        AppointmentDoctorOrderListAo ao2 = new AppointmentDoctorOrderListAo();
        ao2.setDoctorMerchantId(2L);
        ao2.setOrderId(2L);
        AppointmentDoctorOrderListVo vo2 = new AppointmentDoctorOrderListVo();
        vo2.setDoctorVo(new DoctorVo());
        vo2.setHospitalAo(new HospitalAo());
        BigDecimal cost2 = new BigDecimal("20");
        vo2.setCost(cost2.toString());
        vo2.setBeginDate(DateUtils.yyyyMMddHHmmssToString(now.plusDays(1)));
        vo2.setEndDate(DateUtils.yyyyMMddHHmmssToString(now.plusDays(2)));
        vo2.setApproveDate(vo2.getBeginDate());
        vo2.setMerchantStatus(2);
        ao2.setListVo(vo2);

        AppointmentDoctorOrderListAo ao3 = new AppointmentDoctorOrderListAo();
        ao3.setDoctorMerchantId(3L);
        ao3.setOrderId(3L);
        AppointmentDoctorOrderListVo vo3 = new AppointmentDoctorOrderListVo();
        vo3.setDoctorVo(new DoctorVo());
        vo3.setHospitalAo(new HospitalAo());
        BigDecimal cost = new BigDecimal(30);
        vo3.setCost(cost.toString());
        vo3.setBeginDate(DateUtils.yyyyMMddHHmmssToString(now.plusDays(2)));
        vo3.setEndDate(DateUtils.yyyyMMddHHmmssToString(now.plusDays(3)));
        vo3.setApproveDate(vo3.getBeginDate());
        vo3.setMerchantStatus(3);
        ao3.setListVo(vo3);

        List<AppointmentDoctorOrderListAo> list = new ArrayList<>();
        list.add(ao1);
        list.add(ao2);
        list.add(ao3);
        return list;
    }

}
