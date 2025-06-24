package com.czy.api.constant.user_relationship;


import com.czy.api.constant.gateway.InterceptorConstant;
import com.czy.api.constant.test.TestConstant;

/**
 * @author 13225
 * @date 2025/1/4 17:31
 */
public class UserConstant {

    public static final String serviceName = "user-relationship-service";
    public static final String serviceRoute = "/" + serviceName;
    public static final String Login_CONTROLLER = "/login";
    public static final String User_File_CONTROLLER = "/userFile";
    public static final String Password_Register = "/register";
    public static final String Password_Login = "/passwordLoginUser";
    public static final String Send_Sms = "/sendSms";
    public static final String Sms_Login = "/smsLoginUser";
    public static final String Check_Phone_Is_Register = "/checkPhoneIsRegister";
    public static final String Check_Account_Is_Register = "/checkAccountIsRegister";
    public static final String Reset_Password_Vcode = "/vcode/resetPwd";
    public static final String[] loginIpInterceptedURL = new String[]{
            serviceRoute + Login_CONTROLLER + Password_Register,
            serviceRoute + Login_CONTROLLER + Password_Login,
            serviceRoute + Login_CONTROLLER + Send_Sms,
            serviceRoute + Login_CONTROLLER + Sms_Login,
            serviceRoute + Login_CONTROLLER + Reset_Password_Vcode,
            TestConstant.Test_CONTROLLER + TestConstant.Temp_Test
    };

    public static final String Reset_Password_Jwt = "/jwt/resetPwd";
    public static final String Reset_UserInfo = "/resetUserInfo";

    public static final String[] loginJwtInterceptedURL = new String[]{
            serviceRoute + Login_CONTROLLER + Reset_Password_Jwt,
            serviceRoute + Login_CONTROLLER + Reset_UserInfo,
    };

    public static final String Update_Image = "/updateImage";

    public static final String serviceUri = "lb://" + serviceName;

    // 限制url + ip
    public static final String urlIp = InterceptorConstant.IP_PREFIX;

    // 限制发送验证码
    public static final String sendSms = "sendSms:";
    public static final int sendSmsWhiteLimitNum = 5;
    public static final long sendSmsBlackLimitTime = 5 * 60L;
    public static final int User_Permission = 1;
    public static final int Admin_Permission = 2;

    public static final String JWT_FUNCTION_LOGIN = "login";
    public static final String JWT_FUNCTION_REGISTER = "register";
    // 默认15秒注册限制时间
    public static final long USER_CHANGE_KEY_EXPIRE_TIME = 15L;
    // 注册缓存的redisKey
    public static final String USER_REGISTER_REDIS_KEY = "user_register:";

    // 文件存储桶
    public static final String USER_FILE_BUCKET = "user-files-";
}
