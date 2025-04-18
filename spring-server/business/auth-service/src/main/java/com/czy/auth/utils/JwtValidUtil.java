package com.czy.auth.utils;


import cn.hutool.core.bean.BeanUtil;


import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jwt.TokenStatue;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jwt.BaseJwtPayloadAo;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

/**
 * @author 13225
 * @date 2025/1/7 15:35
 * TODO 目前JWT不够安全，只有公钥没有私钥，userId作为加密值过于简单
 */
public class JwtValidUtil {


    /**
     * 获取Claims
     * @param token     jwtToken
     * @param secretKey 密钥
     * @return          Claims  JWT 的有效载荷部分。具体来说，Claims 是一个包含关于用户及其权限的声明或信息的集合
     * @throws Exception 获取Claims失败
     */
    private static Claims getClaimsFromToken(String token, String secretKey) throws Exception{
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    /**
     * 获取JwtTokenAo部分
     * @param token     jwtToken
     * @param secretKey 密钥
     * @return          T
     * @throws Exception 获取JwtTokenAo失败
     */
    public static <T extends BaseJwtPayloadAo> T getJwtTokenAo(String token, Class<T> clazz, String secretKey) throws Exception {
        Claims claims;
        try {
            claims = getClaimsFromToken(token, secretKey);
        } catch (Exception e) {
            throw new Exception(e);
        }
        return BeanUtil.copyProperties(claims, clazz);
    }

    /**
     * 校验token是否正确
     * @param token     jwtToken
     * @param secretKey 密钥
     * @return           true：正确   false：错误
     */
    public static boolean isTokenCorrect(String token, String secretKey) {
        int statue = checkTokenStatus(token, secretKey).getStatue();
        return TokenStatue.VALID.getStatue() == statue || TokenStatue.EFFECTIVE.getStatue() == statue;
    }

    /**
     * 校验token是否有效
     * @param token     jwtToken
     * @param secretKey 密钥
     * @return           true：未过期   false：过期
     */
    public static boolean isTokenEffective(String token, String secretKey) {
        return TokenStatue.VALID.getStatue() == checkTokenStatus(token, secretKey).getStatue();
    }

    /**
     * 校验token状态
     * @param token     jwtToken
     * @param secretKey 密钥
     * @return          TokenStatue
     */
    public static TokenStatue checkTokenStatus(String token, String secretKey) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return TokenStatue.VALID;
        } catch (ExpiredJwtException ex) {
            return TokenStatue.EFFECTIVE;
        } catch (JwtException | IllegalArgumentException ex) {
            return TokenStatue.INVALID;
        }
    }

}
