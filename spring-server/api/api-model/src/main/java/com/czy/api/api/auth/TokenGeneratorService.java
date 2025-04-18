package com.czy.api.api.auth;


import jwt.BaseJwtPayloadAo;

/**
 * @author 13225
 * @date 2025/1/15 17:16
 */
public interface TokenGeneratorService {
    /**
     * 对外提供通过完整jwtPayload生成 accessToken的方法
     * @param jwtPayloadAo  完整jwtPayloadAo
     * @return              accessToken
     * @param <T>   jwtPayloadAo
     */
    <T extends BaseJwtPayloadAo> String generateAccessToken(T jwtPayloadAo) throws Exception;

    /**
     * 对外提供听过jwtPayload生成refreshToken的方法
     * @param jwtPayloadAo  jwtPayloadAo
     * @return              refreshToken
     * @param <T>           jwtPayloadAo
     */
    <T extends BaseJwtPayloadAo> String generateRefreshToken(T jwtPayloadAo) throws Exception;
}
