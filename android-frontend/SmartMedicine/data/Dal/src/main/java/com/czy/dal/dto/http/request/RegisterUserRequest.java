package com.czy.dal.dto.http.request;



/**
 * @author 13225
 * @date 2025/1/2 13:48
 */
public class RegisterUserRequest extends DeviceInfoRequest {
    public Integer id;
    // 字符数量在2 ~ 16之间
    public String userName;
    public String account;
    public String password;
    public String phone;
    public String vcode;
    // 前端写成必须上传头像，暂时不考虑不上传的情况
    public Boolean isHaveImage = true;
}
