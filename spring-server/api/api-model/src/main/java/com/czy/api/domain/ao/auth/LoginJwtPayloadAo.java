package com.czy.api.domain.ao.auth;



import jwt.BaseJwtPayloadAo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Ao（Application Object）：应用对象
 * <p>
 * Ao 是在Web层与Service层之间抽象的复用对象模型，极为贴近展示层，复用度不高。
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class LoginJwtPayloadAo extends BaseJwtPayloadAo { // JWT Payload(负载)
    // id
    private Long userId;
    // phone
    private String phone;
    // account
    private String account;
    // 登录用户UUID
    private String uuid;
    // 功能权限
    private String function;

    public LoginJwtPayloadAo(Long userId, String phone, String account, String uuid, String function) {
        this.userId = userId;
        this.phone = phone;
        this.account = account;
        this.uuid = uuid;
        this.function = function;
    }

    @Override
    public String getSubject() {
        return String.valueOf(userId);
    }
}
