package com.czy.auth.validator;

import com.czy.api.constant.auth.JwtConstant;
import com.czy.api.constant.user.UserConstant;
import com.czy.api.domain.ao.auth.LoginJwtPayloadAo;
import com.czy.auth.utils.JwtGenerator;
import com.czy.auth.utils.JwtValidUtil;
import jwt.TokenStatue;
import org.junit.jupiter.api.Test;
import org.springframework.util.StringUtils;

/**
 * @author 13225
 * @date 2025/4/10 16:09
 */
class JwtValidatorTest {
    private static final LoginJwtPayloadAo jwtPayloadAo = new LoginJwtPayloadAo("13225", "123456", UserConstant.JWT_FUNCTION_LOGIN);
    @Test
    void validateToken() {
        String token = JwtGenerator.generateToken_s(jwtPayloadAo, JwtConstant.ACCESS_TOKEN_GENERATE_KEY, 30L);
        System.out.println("token = " + token);
        JwtValidator jwtValidator = new JwtValidator();
        boolean valid = jwtValidator.validateToken(token, JwtConstant.ACCESS_TOKEN_GENERATE_KEY);
        System.out.println("valid = " + valid);
    }

    @Test
    void getJwtTokenAo() {
        String token = JwtGenerator.generateToken_s(jwtPayloadAo, JwtConstant.ACCESS_TOKEN_GENERATE_KEY, 30L);
        System.out.println("token = " + token);
        JwtValidator jwtValidator = new JwtValidator();
        try {
            jwtValidator.getJwtTokenAo(token, LoginJwtPayloadAo.class);
            System.out.println("jwtPayloadAo = " + jwtPayloadAo.toJsonString());
        } catch (Exception e) {
            System.err.println("e = " + e);
        }
    }

    @Test
    void isTokenEffective() {
        String token = JwtGenerator.generateToken_s(jwtPayloadAo, JwtConstant.ACCESS_TOKEN_GENERATE_KEY, 2L);
        System.out.println("token = " + token);
        TokenStatue tokenStatue1 = checkTokenStatus(token, JwtConstant.ACCESS_TOKEN_GENERATE_KEY);
        System.out.println("tokenStatue1 = " + tokenStatue1);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        TokenStatue tokenStatue2 = checkTokenStatus(token, JwtConstant.ACCESS_TOKEN_GENERATE_KEY);
        System.out.println("tokenStatue2 = " + tokenStatue2);

        TokenStatue tokenStatue3 = checkTokenStatus("token", JwtConstant.ACCESS_TOKEN_GENERATE_KEY);
        System.out.println("tokenStatue3 = " + tokenStatue3);
    }

    public TokenStatue checkTokenStatus(String accessToken, String key) {
        if (!StringUtils.hasText(accessToken)){
            return TokenStatue.INVALID;
        }
        if (!StringUtils.hasText(key)){
            return TokenStatue.INVALID;
        }
        return JwtValidUtil.checkTokenStatus(accessToken, key);
    }
}