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
    // 登录用户ID
    private String userAccount;
    // 登录用户UUID
    private String uuid;
    // 功能权限
    private String function;


    public LoginJwtPayloadAo(String userAccount, String uuid, String function) {
        this.userAccount = userAccount;
        this.uuid = uuid;
        this.function = function;
    }

    @Override
    public String getSubject() {
        return userAccount;
    }
}
