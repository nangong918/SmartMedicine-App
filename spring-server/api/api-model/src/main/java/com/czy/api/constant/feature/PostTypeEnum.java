package com.czy.api.constant.feature;

import lombok.Getter;

/**
 * @author 13225
 * @date 2025/5/10 13:59
 * #日常分享 #专业医疗知识 #养生技巧 #医疗新闻 #其他
 */
@Getter
public enum PostTypeEnum {

    DAILY_SHARE("日常分享", 1),
    PROFESSIONAL_MEDICAL_KNOWLEDGE("专业医疗知识", 2),
    REMEDY_TIPS("养生技巧", 3),
    MEDICAL_NEWS("医疗新闻", 4),
    OTHER("其他", 5);

    private final String name;
    private final Integer code;

    PostTypeEnum(String name, Integer code) {
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public Integer getCode() {
        return code;
    }

    // code -> o
    public static PostTypeEnum getByCode(Integer code) {
        for (PostTypeEnum o : values()) {
            if (o.getCode().equals(code)) {
                return o;
            }
        }
        return OTHER;
    }

    // name -> o
    public static PostTypeEnum getByName(String name) {
        for (PostTypeEnum o : values()) {
            if (o.getName().equals(name)) {
                return o;
            }
        }
        return OTHER;
    }

}
