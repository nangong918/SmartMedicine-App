package com.utils.mvc.config;

/**
 * @author 13225
 * @date 2025/5/5 15:06
 */
public class NerConstant {

    public static final String NER_REDIS_KEY = "ner:";

    // 30天，需要定时任务从新加入
    public static final Long NER_REDIS_EXPIRE_TIME = 60 * 60 * 24 * 30L;
    public static final String IS_LOAD_NER_FILES_KEY = "is_load_ner_files";
}
