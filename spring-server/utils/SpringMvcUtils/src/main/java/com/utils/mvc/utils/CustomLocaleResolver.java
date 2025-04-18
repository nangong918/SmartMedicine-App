package com.utils.mvc.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.util.TextUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

/**
 * @author 13225
 * @date 2025/1/3 9:48
 */
@Slf4j
public class CustomLocaleResolver implements LocaleResolver {

    public static final String I18N_LANGUAGE = "language";


    /**
     * 从HttpServletRequest中获取Locale
     * 要求前端传递必须按照 zh-CN;en-US;xx-XX 的格式
     * @param httpServletRequest    httpServletRequest
     * @return                      语言Local
     */
    @NotNull
    @Override
    public Locale resolveLocale(@NotNull HttpServletRequest httpServletRequest) {
        //获取请求中的语言参数
        String language = httpServletRequest.getParameter(I18N_LANGUAGE);
        //如果没有就使用默认的（根据主机的语言环境生成一个 Locale
        Locale locale = Locale.getDefault();
        //如果请求的链接中携带了 国际化的参数
        if (!TextUtils.isEmpty(language)){
            //zh_CN
            String[] s = language.split("-");//_
            //国家，地区
            locale = new Locale(s[0], s[1]);
        }
        return locale;
    }

    /**
     * 用于实现Locale的切换。比如SessionLocaleResolver获取Locale的方式是从session中读取，但如果
     * 用户想要切换其展示的样式(由英文切换为中文)，那么这里的setLocale()方法就提供了这样一种可能
     *
     * @param httpServletRequest    HttpServletRequest
     * @param httpServletResponse   HttpServletResponse
     * @param locale                locale
     */
    @Override
    public void setLocale(@NotNull HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Locale locale) {
        log.info("切换语言, locale: {}", locale);
    }
}
