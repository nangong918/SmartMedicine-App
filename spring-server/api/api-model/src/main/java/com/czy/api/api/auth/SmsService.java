package com.czy.api.api.auth;


import exception.AppException;

/**
 * @author 13225
 * @date 2025/1/15 21:58
 */
public interface SmsService {

    /**
     * 发送短信验证码
     * @param phone 手机号
     * @return  是否发送成功
     */
    boolean sendSms(String phone) throws AppException;

    /**
     * 校验短信验证码
     * @param phone 手机号
     * @param code  验证码
     * @return      是否校验成功
     */
    boolean checkSms(String phone, String code) throws AppException;

    /**
     * 获取手机号的验证码
     * @param phone
     * @return
     */
    String getVcode(String phone);

}
