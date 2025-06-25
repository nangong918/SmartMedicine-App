package com.czy.api.domain.dto.http.request;

import com.czy.api.constant.user_relationship.UserConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

/**
 * @author 13225
 * @date 2025/1/2 13:48
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class RegisterUserRequest extends DeviceInfoRequest {
    private Integer id;
    @NotEmpty(message = "用户名不能为空")
    // 字符数量在2 ~ 16之间
    @Size(min = 2, max = 16, message = "用户名长度必须在2到16个字符之间")
    private String userName;
    @NotEmpty(message = "用户账号不能为空")
    private String account;
    @NotEmpty(message = "用户密码不能为空")
    private String password;
    @NotEmpty(message = "手机号不能为空")
    private String phone;
    private Integer permission = UserConstant.User_Permission;
    @NotEmpty(message = "验证码不能为空")
    private String vcode;
    private Boolean isHaveImage = false;
}
