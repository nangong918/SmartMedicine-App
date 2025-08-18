package com.czy.api.constant.medicine;

import lombok.Getter;

/**
 * @author 13225
 * @date 2025/8/18 10:51
 */
@Getter
public enum SubjectEnum {

    CARDIOLOGY("心脏内科", 1),
    RESPIRATORY("呼吸内科", 2),
    GASTROENTEROLOGY("消化内科", 3),
    ENDOCRINOLOGY("内分泌科", 4),
    NEPHROLOGY("肾内科", 5),
    NEUROLOGY("神经内科", 6),
    HEMATOLOGY("血液内科", 7),
    RHEUMATOLOGY("风湿免疫科", 8),
    GERIATRICS("老年病科", 9),
    GENERAL_SURGERY("普通外科", 10),
    NEUROSURGERY("神经外科", 11),
    THORACIC_SURGERY("心胸外科", 12),
    ORTHOPEDICS("骨科", 13),
    UROLOGY("泌尿外科", 14),
    PLASTIC_SURGERY("整形外科", 15),
    LIVER_GALLBLADDER_SURGERY("肝胆外科", 16),
    VASCULAR_SURGERY("血管外科", 17),
    GYNECOLOGY("妇科", 18),
    OBSTETRICS("产科", 19),
    REPRODUCTIVE_MEDICINE("生殖医学科", 20),
    PEDIATRIC_INTERNAL_MEDICINE("儿内科", 21),
    PEDIATRIC_SURGERY("儿外科", 22),
    NEONATOLOGY("新生儿科", 23),
    DERMATOLOGICAL_DISEASES("皮肤病科", 24),
    STD("性病科", 25),
    EYE_DISEASES("眼病科", 26),
    EYE_SURGERY("眼外科", 27),
    EAR("耳科", 28),
    NOSE("鼻科", 29),
    THROAT("喉科", 30),
    DENTISTRY("牙科", 31),
    ORAL_SURGERY("口腔外科", 32),
    TRADITIONAL_CHINESE_INTERNAL_MEDICINE("中医内科", 33),
    TRADITIONAL_CHINESE_SURGERY("中医外科", 34),
    ACUPUNCTURE("针灸科", 35),
    PSYCHIATRY("精神病学", 36),
    PSYCHOLOGICAL_COUNSELING("心理咨询科", 37),
    PHYSICAL_THERAPY("物理治疗", 38),
    OCCUPATIONAL_THERAPY("作业治疗", 39),
    IMAGING("影像学科", 40),
    INTERVENTIONAL_RADIOLOGY("介入放射科", 41),
    CLINICAL_LABORATORY("临床检验", 42),
    PATHOLOGY("病理科", 43);

    private final String name;
    private final int code;

    SubjectEnum(String name, int code) {
        this.name = name;
        this.code = code;
    }

}