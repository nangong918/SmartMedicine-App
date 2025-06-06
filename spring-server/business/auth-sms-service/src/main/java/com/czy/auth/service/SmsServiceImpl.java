package com.czy.auth.service;


import com.czy.api.api.sms.SmsService;
import com.czy.auth.constant.SmsConstant;
import com.czy.springUtils.debug.DebugConfig;
import com.czy.springUtils.service.RedisManagerService;
import exception.AppException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * @author 13225
 * @date 2025/1/15 22:00
 */
@Slf4j
@RequiredArgsConstructor
@Service
@org.apache.dubbo.config.annotation.Service(protocol = "dubbo", version = "1.0.0")
public class SmsServiceImpl implements SmsService {

    private final RedisManagerService redisManagerService;
    private final DebugConfig debugConfig;
    private final SmsConstant smsConstant;


    @Override
    public boolean sendSms(String phone) throws AppException {
        if (checkPhoneIsValid(phone)){
            String code = generateRandomCode();
            log.info("发送短信验证码成功，phone: {}, code: {}", phone, code);
            return redisManagerService.setObjectAsString(SmsConstant.SMS_CODE_KEY + phone, code, smsConstant.getSmsCodeExpireTime());
        }
        return false;
    }

    @Override
    public boolean checkSms(String phone, String code) throws AppException {
        if (!debugConfig.isVcodeCheck()){
            return true;
        }
        if (checkPhoneIsValid(phone)){
            String smsCode = (String) redisManagerService.getObjectFromString(SmsConstant.SMS_CODE_KEY + phone, Object.class);
            if (!StringUtils.hasText(smsCode)){
                throw new AppException("验证码不合法");
            }
            if (!smsCode.equals(code)){
                throw new AppException("验证码错误");
            }
            return true;
        }
        return false;
    }

    @Override
    public String getVcode(String phone){
        if (checkPhoneIsValid(phone)){
            return (String) redisManagerService.getObjectFromString(SmsConstant.SMS_CODE_KEY + phone, Object.class);
        }
        return null;
    }


    /**
     * 检查手机号是否合法
     * @param phone     手机号
     * @return          true: 合法
     * @throws AppException 抛出异常
     */
    private boolean checkPhoneIsValid(String phone) throws AppException {
        if (StringUtils.isEmpty(phone)){
            String errorMsg = String.format("phone不能为空，phone: %s", phone);
            log.warn(errorMsg);
            throw new AppException(errorMsg);
        }
        if (!phone.startsWith(smsConstant.getPhonePrefix())){
            String errorMsg = String.format("phone前缀错误，phone: %s", phone);
            log.warn(errorMsg);
            throw new AppException(errorMsg);
        }
        if (phone.length() != smsConstant.getPhoneLength()){
            String errorMsg = String.format("phone长度错误，phone: %s", phone);
            log.warn(errorMsg);
            throw new AppException(errorMsg);
        }
        return true;
    }

    /**
     * 生成随机的6位数
     * @return  随机的6位数
     */
    private String generateRandomCode() {
        int randomNum = (int) (Math.random() * 900000) + 100000;
        return String.valueOf(randomNum);
    }
}
