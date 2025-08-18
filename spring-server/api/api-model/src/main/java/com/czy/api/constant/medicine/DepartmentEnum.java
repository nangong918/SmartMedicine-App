package com.czy.api.constant.medicine;

import lombok.Getter;

/**
 * @author 13225
 * @date 2025/8/18 10:48
 */
@Getter
public enum DepartmentEnum {

    INTERNAL_MEDICINE("内科", 1, new SubjectEnum[]{
            SubjectEnum.CARDIOLOGY,
            SubjectEnum.RESPIRATORY,
            SubjectEnum.GASTROENTEROLOGY,
            SubjectEnum.ENDOCRINOLOGY,
            SubjectEnum.NEPHROLOGY,
            SubjectEnum.NEUROLOGY,
            SubjectEnum.HEMATOLOGY,
            SubjectEnum.RHEUMATOLOGY,
            SubjectEnum.GERIATRICS
    }),
    SURGERY("外科", 2, new SubjectEnum[]{
            SubjectEnum.GENERAL_SURGERY,
            SubjectEnum.NEUROSURGERY,
            SubjectEnum.THORACIC_SURGERY,
            SubjectEnum.ORTHOPEDICS,
            SubjectEnum.UROLOGY,
            SubjectEnum.PLASTIC_SURGERY,
            SubjectEnum.LIVER_GALLBLADDER_SURGERY,
            SubjectEnum.VASCULAR_SURGERY
    }),
    OBSTETRICS_AND_GYNECOLOGY("妇产科", 3, new SubjectEnum[]{
            SubjectEnum.GYNECOLOGY,
            SubjectEnum.OBSTETRICS,
            SubjectEnum.REPRODUCTIVE_MEDICINE
    }),
    PEDIATRICS("儿科", 4, new SubjectEnum[]{
            SubjectEnum.PEDIATRIC_INTERNAL_MEDICINE,
            SubjectEnum.PEDIATRIC_SURGERY,
            SubjectEnum.NEONATOLOGY
    }),
    DERMATOLOGY("皮肤科", 5, new SubjectEnum[]{
            SubjectEnum.DERMATOLOGICAL_DISEASES,
            SubjectEnum.STD
    }),
    OPHTHALMOLOGY("眼科", 6, new SubjectEnum[]{
            SubjectEnum.EYE_DISEASES,
            SubjectEnum.EYE_SURGERY
    }),
    ENT("耳鼻喉科", 7, new SubjectEnum[]{
            SubjectEnum.EAR,
            SubjectEnum.NOSE,
            SubjectEnum.THROAT
    }),
    DENTISTRY("口腔科", 8, new SubjectEnum[]{
            SubjectEnum.DENTISTRY,
            SubjectEnum.ORAL_SURGERY
    }),
    TRADITIONAL_CHINESE_MEDICINE("中医科", 9, new SubjectEnum[]{
            SubjectEnum.TRADITIONAL_CHINESE_INTERNAL_MEDICINE,
            SubjectEnum.TRADITIONAL_CHINESE_SURGERY,
            SubjectEnum.ACUPUNCTURE
    }),
    PSYCHIATRY("精神科", 10, new SubjectEnum[]{
            SubjectEnum.PSYCHIATRY,
            SubjectEnum.PSYCHOLOGICAL_COUNSELING
    }),
    REHABILITATION("康复科", 11, new SubjectEnum[]{
            SubjectEnum.PHYSICAL_THERAPY,
            SubjectEnum.OCCUPATIONAL_THERAPY
    }),
    RADIOLOGY("放射科", 12, new SubjectEnum[]{
            SubjectEnum.IMAGING,
            SubjectEnum.INTERVENTIONAL_RADIOLOGY
    }),
    LABORATORY("检验科", 13, new SubjectEnum[]{
            SubjectEnum.CLINICAL_LABORATORY,
            SubjectEnum.PATHOLOGY
    });

    private final String name;
    private final int code;
    private final SubjectEnum[] subjectEnums;

    DepartmentEnum(String name, int code, SubjectEnum[] subjectEnums) {
        this.name = name;
        this.code = code;
        this.subjectEnums = subjectEnums;
    }

    public String getName() {
        return name;
    }

    public int getCode() {
        return code;
    }

    public SubjectEnum[] getSubjectEnums() {
        return subjectEnums;
    }

    // code -> DepartmentEnum
    public static DepartmentEnum getByCode(int code) {
        for (DepartmentEnum value : values()) {
            if (value.code == code) {
                return value;
            }
        }
        return INTERNAL_MEDICINE; // 默认返回
    }

    // name -> DepartmentEnum
    public static DepartmentEnum getByName(String name) {
        for (DepartmentEnum value : values()) {
            if (value.name.equals(name)) {
                return value;
            }
        }
        return INTERNAL_MEDICINE; // 默认返回
    }
}
