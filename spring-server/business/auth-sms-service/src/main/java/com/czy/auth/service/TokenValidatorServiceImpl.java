package com.czy.auth.service;

import com.czy.api.domain.ao.auth.LoginJwtPayloadAo;
import exception.AppException;
import jwt.BaseJwtPayloadAo;
import jwt.TokenStatue;
import com.czy.api.api.auth.TokenValidatorService;
import com.czy.auth.utils.JwtValidUtil;
import com.czy.auth.validator.JwtValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.Optional;

/**
 * @author 13225
 * @date 2025/1/15 17:46
 */
@Slf4j
@RequiredArgsConstructor
@org.apache.dubbo.config.annotation.Service(protocol = "dubbo", version = "1.0.0")
public class TokenValidatorServiceImpl implements TokenValidatorService {

    private final JwtValidator jwtValidator;


    @Override
    public boolean checkTokenBelongUser(String token, Long userId) throws Exception{
        try {
            LoginJwtPayloadAo ao = getJwtTokenAo(token, LoginJwtPayloadAo.class);
            Long id = Optional.ofNullable(ao)
                    .map(LoginJwtPayloadAo::getUserId)
                    .orElse(-1L);
            return id.equals(userId);
        } catch (Exception e){
            throw new AppException("Token无效，解析失败");
        }
    }

    @Override
    public TokenStatue checkTokenStatus(String accessToken, String key) {
        if (!StringUtils.hasText(accessToken)){
            return TokenStatue.INVALID;
        }
        if (!StringUtils.hasText(key)){
            return TokenStatue.INVALID;
        }
        return JwtValidUtil.checkTokenStatus(accessToken, key);
    }

    @Override
    public boolean validateToken(String accessToken, String key) {
        if (!StringUtils.hasText(accessToken)){
            return false;
        }
        if (!StringUtils.hasText(key)){
            return false;
        }
        return jwtValidator.validateToken(accessToken, key);
    }

    @Override
    public boolean checkTokenIsValid(String token, String key) {
        try {
            return jwtValidator.isTokenCorrect(token, key);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean checkTokenIsEffective(String token, String key) {
        try {
            return jwtValidator.isTokenEffective(token, key);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public <T extends BaseJwtPayloadAo> T getJwtTokenAo(String token, Class<T> clazz) throws Exception {
        return jwtValidator.getJwtTokenAo(token, clazz);
    }
}
