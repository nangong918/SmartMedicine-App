package com.czy.auth.service;

import com.czy.api.constant.gateway.EncryptConstant;
import com.czy.auth.utils.JwtGenerator;
import com.czy.api.constant.auth.JwtConstant;
import com.czy.api.api.auth.TokenGeneratorService;
import jwt.BaseJwtPayloadAo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;


/**
 * @author 13225
 * @date 2025/1/14 14:49
 * Token生成的Service，用于Token生成：
 *  1.生成JWT无状态Token：无状态Token的JwtPayloadAo是泛型，因为可能存在不同的JwtPayloadAo
 *  2.生成随机存在状态的Token
 */
@Slf4j
@RequiredArgsConstructor
@org.apache.dubbo.config.annotation.Service(protocol = "dubbo", version = "1.0.0")
public class TokenGeneratorServiceImpl implements TokenGeneratorService {

    private final JwtGenerator jwtGenerator;

    @Override
    public <T extends BaseJwtPayloadAo> String generateAccessToken(T jwtPayloadAo) throws Exception {
        if (jwtPayloadAo == null){
            throw throwException("生成accessToken失败，loginJwtPayloadAo is null");
        }
        String accessToken = jwtGenerator.generateToken(
                jwtPayloadAo,
                JwtConstant.ACCESS_TOKEN_GENERATE_KEY,
                EncryptConstant.Token.AccessToken_EXPIRATION_TIME
                );
        if (!StringUtils.hasText(accessToken)){
            throw throwException(
                    String.format("生成accessToken失败，loginJwtPayloadAo 存在问题：%s",
                            jwtPayloadAo.toJsonString())
            );
        }
        return accessToken;
    }

    @Override
    public <T extends BaseJwtPayloadAo> String generateRefreshToken(T jwtPayloadAo) throws Exception {
        if (jwtPayloadAo == null){
            throw throwException("生成refreshToken失败，loginJwtPayloadAo is null");
        }
        String refreshToken = jwtGenerator.generateToken(
                jwtPayloadAo,
                JwtConstant.REFRESH_TOKEN_GENERATE_KEY,
                EncryptConstant.Token.RefreshToken_EXPIRATION_TIME
        );
        if (!StringUtils.hasText(refreshToken)){
            throw throwException(
                    String.format("生成refreshToken失败，loginJwtPayloadAo 存在问题：%s",
                            jwtPayloadAo.toJsonString())
            );
        }
        return refreshToken;
    }

    private Exception throwException(String errorMsg){
        log.warn(errorMsg);
        return new InterruptedException(errorMsg);
    }
}
