package com.czy.imports.service.impl;

import cn.hutool.core.util.IdUtil;
import com.api.mapper.medicine.mybatis.DoctorMapper;
import com.api.mapper.medicine.mybatis.DoctorMerchantAppointmentMapper;
import com.api.mapper.medicine.mybatis.HospitalMapper;
import com.api.mapper.medicine.redis.DoctorMerchantAppointmentRedisMapper;
import com.api.mapper.user.mybatis.user.UserMapper;
import com.czy.api.constant.BaseEnum;
import com.czy.api.constant.BaseParentEnum;
import com.czy.api.constant.medicine.DepartmentEnum;
import com.czy.api.domain.Do.medicine.DoctorDo;
import com.czy.api.domain.Do.medicine.DoctorMerchantAppointmentDo;
import com.czy.api.domain.Do.medicine.HospitalDo;
import com.czy.api.domain.Do.user.UserDo;
import com.czy.imports.service.ImportDoctorMerchantAppointmentService;
import com.czy.imports.utils.RandomDateTimeGenerator;
import location.GeoUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * @author 13225
 * @date 2025/8/20 10:31
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class ImportDoctorMerchantAppointmentServiceImpl implements ImportDoctorMerchantAppointmentService {

    private final DoctorMapper doctorMapper;
    private final HospitalMapper hospitalMapper;
    private final UserMapper userMapper;
    private final DoctorMerchantAppointmentMapper doctorMerchantAppointmentMapper;
    // 导入数据的时候用于缓存数据
    private final DoctorMerchantAppointmentRedisMapper doctorMerchantAppointmentRedisMapper;

    public void importRecord(){}

    private static final int HOSPITAL_COUNT = 5;
    private static final String[] HOSPITAL_NAMES = {"广东省人民医院","深圳市第一医院","深圳大学第一附属医院","上海交通大学附属医院","广州医院"};
    private static final String[] HOSPITAL_LEVELS = {"三级甲等", "三级乙等", "二级甲等", "二级乙等", "二级丙等"};
    // 22.32,19N, 114.01.17E
    private static final double[] LOCATION = {22.63667, 114.01472};
    private static final double RADIUS = 5000;

    private static final int DOCTOR_COUNT = 5;
    private static final Long START_ID = 1234567890L;
    private static final String[] DOCTOR_NAMES = {"张医生", "王医生", "李医生", "赵医生", "孙医生"};
    private static final String[] DOCTOR_TITLES = {"主治医师", "副主任医师", "主任医师", "副主任医师", "主治医师"};

    private List<DoctorDo> createDoctors(){
        // 头像id
        Long avatarId = 1953385170029322240L;
        List<UserDo> userDos = userMapper.fuzzyGetUserByName("内科李大夫");
        if (!CollectionUtils.isEmpty(userDos)){
            log.info("数据非空");
            avatarId = userDos.get(0).getAvatarFileId();
        }
        else {
            log.warn("数据为空");
        }

        List<DoctorDo> doctorDos = new ArrayList<>();
        for (int i = 0; i < DOCTOR_COUNT; i++){
            DoctorDo doctorDo = new DoctorDo();
            int finalI = i;
            String name = Optional.of(DOCTOR_NAMES)
                    .filter(names -> names.length > finalI)
                    .map(names -> names[finalI])
                    .orElse(DOCTOR_NAMES[DOCTOR_NAMES.length - 1]);
            doctorDo.setName(name);
            String title = DOCTOR_TITLES[i % DOCTOR_TITLES.length];
            doctorDo.setTitle(title);
            doctorDo.setAvatarFileId(avatarId);
            doctorDo.setId(START_ID + i);
            doctorDos.add(doctorDo);
        }
        return doctorDos;
    }

    private List<HospitalDo> createHospitals(){
        List<HospitalDo> hospitalDos = new ArrayList<>();
        for (int i = 0; i < HOSPITAL_COUNT; i++) {
            HospitalDo hospitalDo = new HospitalDo();
            hospitalDo.setId(START_ID + i);
            hospitalDo.setName(HOSPITAL_NAMES[i % HOSPITAL_NAMES.length]);
            hospitalDo.setLevel(HOSPITAL_LEVELS[i % HOSPITAL_LEVELS.length]);
            hospitalDo.setProvince("广东省");
            hospitalDo.setCity("深圳市");
            hospitalDo.setRegion("南山区");
            Double[] coordinates = GeoUtils.generateRandomCoordinates(
                    LOCATION[0],
                    LOCATION[1],
                    RADIUS
            );
            hospitalDo.setLongitude(coordinates[0]);
            hospitalDo.setLatitude(coordinates[1]);
            hospitalDos.add(hospitalDo);
        }
        return hospitalDos;
    }

    @Override
    public void createDoctorsHospital(){
        List<DoctorDo> doctorDos = createDoctors();
        List<HospitalDo> hospitalDos = createHospitals();

        doctorMapper.insertBatch(doctorDos);
        hospitalMapper.insertBatch(hospitalDos);
    }

    private static final BigDecimal[] costList = new BigDecimal[]{
            new BigDecimal("20.0"),
            new BigDecimal("30.0"),
            new BigDecimal("40.0"),
            new BigDecimal("45.0"),
            new BigDecimal("60.0"),
            new BigDecimal("80.0"),
            new BigDecimal("100.0"),
    };

    private static final int MAX_REMAIN_COUNT = 20;
    private static final int TEST_DATA_COUNT = 4;

    @Override
    public void createDoctorMerchantAppointmentDos(int count){
        List<DoctorMerchantAppointmentDo> doctorMerchantAppointmentDos = generatorDoctorMerchantAppointmentDos(count);

        if (!CollectionUtils.isEmpty(doctorMerchantAppointmentDos)){
            // 批量插入数据库
            doctorMerchantAppointmentMapper.insertDoctorMerchantAppointmentBatch(
                    doctorMerchantAppointmentDos
            );
            log.info("批量插入数据库成功");

            // 异步存入redis
            doctorMerchantAppointmentRedisMapper.saveDoctorMerchantAppointmentDos(
                    doctorMerchantAppointmentDos
            );
            log.info("异步存入redis成功");
            boolean semaphorePermitsResult = doctorMerchantAppointmentRedisMapper.initAppointmentListSemaphorePermits(
                    doctorMerchantAppointmentDos
            );
            log.info("初始化信号量: {}", semaphorePermitsResult);
        }
    }

    @Override
    public List<DoctorMerchantAppointmentDo> generatorDoctorMerchantAppointmentDos(int count){
        if (count <= 0){
            return new ArrayList<>();
        }

        int doctorCount = doctorMapper.getCount();
        int hospitalCount = hospitalMapper.getCount();
        if (doctorCount == 0){
            log.warn("没有医生数据, 开始创建数据");
            doctorMapper.insertBatch(createDoctors());
        }
        else {
            log.warn("有医生数据: {}, 跳过创建数据", doctorCount);
        }
        if (hospitalCount == 0){
            log.warn("没有医院数据: {}, 创建数据", hospitalCount);
            hospitalMapper.insertBatch(createHospitals());
        }

        List<DoctorDo> doctorDos = doctorMapper.getRandomByLimit(DOCTOR_COUNT);
        List<HospitalDo> hospitalDos = hospitalMapper.getRandomByLimit(HOSPITAL_COUNT);
        List<BaseParentEnum> departmentDict = getDepartmentDict();

        Random random = new Random();
        List<DoctorMerchantAppointmentDo> result = new ArrayList<>();

        for (int i = 0; i < count + TEST_DATA_COUNT; i++) {
            int randomDoctorIndex = random.nextInt(doctorDos.size());
            int randomHospitalIndex = random.nextInt(hospitalDos.size());

            DoctorDo doctorDo = doctorDos.get(randomDoctorIndex);
            HospitalDo hospitalDo = hospitalDos.get(randomHospitalIndex);

            DoctorMerchantAppointmentDo doctorMerchantAppointmentDo = new DoctorMerchantAppointmentDo();
            doctorMerchantAppointmentDo.setId(IdUtil.getSnowflakeNextId());
            doctorMerchantAppointmentDo.setDoctorId(doctorDo.getId());
            doctorMerchantAppointmentDo.setHospitalId(hospitalDo.getId());
            Integer[] departmentSubjectDict = getRandomDepartmentSubjectDict(departmentDict);
            doctorMerchantAppointmentDo.setDepartmentId(departmentSubjectDict[0]);
            doctorMerchantAppointmentDo.setSubjectId(departmentSubjectDict[1]);

            int randomCostIndex = random.nextInt(costList.length);
            doctorMerchantAppointmentDo.setCost(costList[randomCostIndex]);

            // 0 ~ 20 个可预约的
            int randomRemainCount = random.nextInt(MAX_REMAIN_COUNT + 1);
            doctorMerchantAppointmentDo.setRemainCount(randomRemainCount);

            LocalDateTime[] randomDateTime = RandomDateTimeGenerator.getRandomStartAndEndDateTimes();
            doctorMerchantAppointmentDo.setBeginDate(randomDateTime[0]);
            doctorMerchantAppointmentDo.setEndDate(randomDateTime[1]);

            result.add(doctorMerchantAppointmentDo);
        }

        // 添加一定生成的数据，避免在测试的时候找不到数据的情况
        getMustGenerateDoctorMerchantAppointmentDos(result);

        return result;
    }

    private void getMustGenerateDoctorMerchantAppointmentDos
            (@NonNull List<DoctorMerchantAppointmentDo> list) {
        assert list.size() >= TEST_DATA_COUNT;
        for (int i = 0; i < TEST_DATA_COUNT; i++){
            LocalDateTime[] resetTimes = RandomDateTimeGenerator.getTodayTimes(LocalDate.now().plusDays(i));
            // 设置日期
            list.get(i).setBeginDate(resetTimes[0]);
            list.get(i).setEndDate(resetTimes[1]);
            // 内科
            list.get(i).setDepartmentId(DepartmentEnum.INTERNAL_MEDICINE.getCode());
            // 心脏内科
            list.get(i).setSubjectId(DepartmentEnum.INTERNAL_MEDICINE.getSubjectEnums()[0].getCode());
        }
    }

    private List<BaseParentEnum> getDepartmentDict (){
        List<BaseParentEnum> departmentDict = new ArrayList<>();
        for (DepartmentEnum dEnum : DepartmentEnum.values()){
            Optional.ofNullable(dEnum)
                    .map(DepartmentEnum::getBaseParentEnum)
                    .ifPresent(departmentDict::add);
        }
        return departmentDict;
    }

    private Integer[] getRandomDepartmentSubjectDict (List<BaseParentEnum> departmentDict){
        Integer[] departmentSubjectDict = new Integer[2];
        Random random = new Random();
        int departmentIndex = random.nextInt(departmentDict.size());
        BaseParentEnum department = departmentDict.get(departmentIndex);
        int subjectIndex = random.nextInt(department.childEnums.length);
        BaseEnum subject = department.childEnums[subjectIndex];
        departmentSubjectDict[0] = department.code;
        departmentSubjectDict[1] = subject.code;
        return departmentSubjectDict;
    }
}
