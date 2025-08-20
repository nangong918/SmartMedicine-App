package com.czy.imports.service.impl;

import com.api.mapper.medicine.DoctorMapper;
import com.api.mapper.medicine.HospitalMapper;
import com.api.mapper.user.mysql.user.UserMapper;
import com.czy.api.domain.Do.medicine.DoctorDo;
import com.czy.api.domain.Do.medicine.HospitalDo;
import com.czy.api.domain.Do.user.UserDo;
import com.czy.imports.service.ImportDoctorMerchantAppointmentService;
import location.GeoUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    private List<DoctorDo> createDoctors(@NonNull Long avatarId){
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
        List<DoctorDo> doctorDos = createDoctors(avatarId);
        List<HospitalDo> hospitalDos = createHospitals();

        doctorMapper.insertBatch(doctorDos);
        hospitalMapper.insertBatch(hospitalDos);
    }
}
