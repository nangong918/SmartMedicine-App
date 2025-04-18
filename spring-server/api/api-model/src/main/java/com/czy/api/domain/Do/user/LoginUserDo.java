package com.czy.api.domain.Do.user;

import io.swagger.v3.oas.annotations.media.Schema;
import json.BaseBean;
import lombok.Data;

/**
 * @author 13225
 * @date 2025/1/2 13:48
 */
@Schema(description = "用户基本信息")
@Data
public class LoginUserDo implements BaseBean {
    private Long id;
    @Schema(description = "用户名称", type = "string", example = "张三")
    private String userName;
    @Schema(description = "用户登录账号", type = "string", example = "SweetLemon77")
    private String account;
    private String password;
    private String phone;
    @Schema(description = "用户权限等级:[user:1][Admin:2]", type = "integer", example = "1")
    private Integer permission;
    // register_time
    @Schema(description = "注册时间", type = "long", example = "1643676800")
    private Long registerTime;
    // last_online_time
    @Schema(description = "最后登录时间", type = "long", example = "1643676800")
    private Long lastOnlineTime;
}
