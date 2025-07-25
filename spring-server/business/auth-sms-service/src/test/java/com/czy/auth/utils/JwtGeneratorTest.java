package com.czy.auth.utils;

import com.czy.api.constant.auth.JwtConstant;
import com.czy.api.constant.user_relationship.UserConstant;
import com.czy.api.domain.ao.auth.LoginJwtPayloadAo;
import org.junit.jupiter.api.Test;

/**
 * @author 13225
 * @date 2025/4/10 15:38
 */
class JwtGeneratorTest {
    private static final LoginJwtPayloadAo jwtPayloadAo = new LoginJwtPayloadAo(
            1L,
            "13225",
            "13225",
            "123456",
            UserConstant.JWT_FUNCTION_LOGIN);
    @Test
    void generateToken() {
        JwtGenerator jwtGenerator = new JwtGenerator();

        String accessToken = jwtGenerator.generateToken(jwtPayloadAo, JwtConstant.ACCESS_TOKEN_GENERATE_KEY, 60L);

        System.out.println(accessToken);
    }
}