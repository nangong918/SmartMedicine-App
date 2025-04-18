package com.czy.springUtils.util;


import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

/**
 * @author 13225
 * @date 2025/1/3 9:54
 */
@Setter
@Getter
@Slf4j
@Component
@ConfigurationProperties(prefix = "spring.mvc.i18n")
public class I18nUtil {

    // 配置文件路径；@Value值不能为static
    @Value("${spring.messages.basename}")
    private String basename;

    // 将配置文件路径赋值给static的path
    private static String basenameStatic;

    /**
     * Spring 容器的工作原理:
     * Spring 容器在创建 bean 时，会通过反射机制为非 static 字段注入值。static 字段属于类本身，而不是类的实例，因此 Spring 无法在实例化 bean 时为 static 字段注入值。
     * 生命周期管理:
     * @Value 的注入是在 Spring 管理的 bean 生命周期的上下文中进行的，而 static 字段在类加载时就已经存在，和 Spring 的生命周期管理没有关系
     */
    @PostConstruct
    public void init() {
        // 非static的@Value转为static的path
        I18nUtil.basenameStatic = basename;
    }


    /**
     * 获取指定语言中的国际化信息，如果没有则走英文
     *
     * @param code 国际化 key
     * @param lang 语言参数
     * @return 国际化后内容信息
     */
    public static String getMessage(String code, String lang) {
        Locale locale;
        if (StringUtils.isEmpty(lang)) {
            locale = Locale.US;
        }
        else {
            try {
                String[] split = lang.split("-");//_
                locale = new Locale(split[0], split[1]);
            } catch (Exception e) {
                locale = Locale.US;
            }
        }
        return getMessage(code, null, code, locale);
    }


    public static String getMessage(String code, Object[] args, String defaultMessage, Locale locale) {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setDefaultEncoding(StandardCharsets.UTF_8.toString());
        messageSource.setBasename(I18nUtil.basenameStatic);
        String content;
        try {
            content = messageSource.getMessage(code, args, locale);
        } catch (Exception e) {
            log.error("国际化参数获取失败===>", e);
            content = defaultMessage;
        }
        return content;
    }

}
